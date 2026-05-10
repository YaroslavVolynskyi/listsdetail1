package com.yarik.listdetail.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yarik.listdetail.ui.viewmodels.ListViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yarik.listdetail.data.ItemEntity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yarik.listdetail.ui.theme.Purple80
import kotlin.Boolean

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    navigateToDetails: (Long) -> Unit,
    listViewModel: ListViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            Log.e("yarik", "observer from composable. Lifecycle event - ${event.name}")
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        lifecycleOwner.lifecycle.addObserver(listViewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            lifecycleOwner.lifecycle.removeObserver(listViewModel)
        }
    }

    val listState = listViewModel.itemsState.collectAsStateWithLifecycle()
    val backgroundEnabledIds = listState.value.backgroundEnabledIds
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        listViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ItemsList(
        snackbarHostState = snackbarHostState,
        itemsList = listState.value.items,
        onDelete = listViewModel::deleteItem,
        navigateToDetails = navigateToDetails,
        addNote = listViewModel::addItem,
        onSave = listViewModel::onSaveClicked,
        onSaveDescription = listViewModel::onSaveDescription,
        onCheckedChange = listViewModel::onCheckedChanged,
        isBackgroundEnabled = { id ->
            backgroundEnabledIds.contains(id)
        },
        toggleBackground = listViewModel::toggleBackground
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsList(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onDelete: (Long) -> Unit,
    navigateToDetails: (Long) -> Unit,
    addNote: (String) -> Unit,
    onSave: (String, Long) -> Unit,
    onSaveDescription: (String, Long) -> Unit,
    onCheckedChange: (Boolean, Long) -> Unit,
    itemsList: List<ItemEntity>,
    isBackgroundEnabled: (Long) -> Boolean,
    toggleBackground: (Long) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
//    val showScrollToTopButton = remember { mutableStateOf(false) }
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .collect { index ->
//                showScrollToTopButton.value = index > 2
//            }
//    }

    val showScrollToTopButton = remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("List with ${itemsList.size} items") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple80
                )
            )
        },
        floatingActionButton = {
            Column {
                AnimatedVisibility(
                    visible = showScrollToTopButton.value,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    FloatingActionButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                    }
                }
                FloatingActionButton(
                    onClick = {
                        addNote("item ${itemsList.size + 1}")
                        coroutineScope.launch {
                            listState.animateScrollToItem(itemsList.size)
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add note")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(paddingValues)
        ) {
            items(items = itemsList, key = { it.id }) { item ->
                val dismissState = rememberSwipeToDismissBoxState()
                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(item.id)
                    }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .background(
                                    Color.Red,
                                    shape = CardDefaults.shape
                                )
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    },
                    enableDismissFromStartToEnd = false
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .animateContentSize()
                    ) {
                        val isExpanded = rememberSaveable(item.id) { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                        ) {
                            SingleRow(
                                item = item,
                                isExpanded = isExpanded.value,
                                onIsExpandedToggled = {
                                    isExpanded.value = !isExpanded.value
                                },
                                onSave = onSave,
                                onCheckedChange = onCheckedChange,
                                navigateToDetails = navigateToDetails,
                                isBackgroundEnabled = isBackgroundEnabled,
                                toggleBackground = toggleBackground
                            )
                            val description = rememberSaveable(item.description) {
                                mutableStateOf(item.description ?: "")
                            }
                            AnimatedVisibility(
                                visible = isExpanded.value,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    val focusManager = LocalFocusManager.current
                                    TextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .onFocusChanged { focusState ->
                                                if (!focusState.isFocused) {
                                                    description.value = item.description ?: ""
                                                }
                                            },
                                        value = description.value,
                                        onValueChange = { newValue ->
                                            description.value = newValue
                                        },
                                    )
                                    IconButton(onClick = {
                                        onSaveDescription(description.value, item.id)
                                        focusManager.clearFocus()
                                        isExpanded.value = false
                                    }) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "save description"
                                        )
                                    }
                                }
                            }
                        }
                    }
                } // SwipeToDismissBox
            }
        }
    }
}

@Composable
fun SingleRow(
    modifier: Modifier = Modifier,
    item: ItemEntity,
    isExpanded: Boolean = false,
    onIsExpandedToggled: () -> Unit,
    onSave: (String, Long) -> Unit,
    onCheckedChange: (Boolean, Long) -> Unit,
    navigateToDetails: (Long) -> Unit,
    isBackgroundEnabled: (Long) -> Boolean,
    toggleBackground: (Long) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .padding(16.dp)
            .background(if (isBackgroundEnabled(item.id)) {
                Color.Red
            } else {
                Color.Unspecified
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val text = rememberSaveable(item.text) {
            mutableStateOf(item.text ?: "")
        }
        TextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        text.value = item.text ?: ""
                    }
                },
            value = text.value,
            onValueChange = {
                text.value = it
            }
        )
        IconButton(onClick = {
            onIsExpandedToggled()
        }) {
            Crossfade(targetState = isExpanded) { expanded ->
                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) {
                        "collapse"
                    } else {
                        "expand"
                    }
                )
            }
        }
        Checkbox(checked = item.isChecked, onCheckedChange = { isChecked ->
            onCheckedChange(isChecked, item.id)
        })
        IconButton(onClick = {
            onSave(text.value, item.id)
            focusManager.clearFocus()
        }) {
            Icon(Icons.Default.Check, contentDescription = "Save")
        }
        IconButton(onClick = { navigateToDetails(item.id) }) {
            Icon(Icons.Default.Info, contentDescription = "Details")
        }
        IconButton( onClick = { toggleBackground(item.id) }) {
            Icon(Icons.Default.Refresh, "background")
        }
    }
}