package com.yarik.listdetail.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.util.TableInfo
import com.yarik.listdetail.data.ItemEntity
import com.yarik.listdetail.ui.theme.Purple80
import com.yarik.listdetail.ui.viewmodels.activityViewModel
import com.yarik.listdetail.ui.viewmodels.DetailViewModel
import com.yarik.listdetail.ui.viewmodels.SharedViewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    itemId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateBackWithTime: (Long) -> Unit = {},
) {
    BackHandler {
        onNavigateBack()
        Log.e("yarik", "yarik back pressed back handler")
    }

    val sharedViewModel: SharedViewModel = activityViewModel()
    val detailViewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory> (
        key = "detail_$itemId",
        creationCallback = {
            factory -> factory.create(entryId = itemId)
        }
    )

    val entryTime = remember { System.currentTimeMillis() }
    DisposableEffect(Unit) {
        onDispose {
            Log.e("yarik", "yarik onDispose")
            val secondsSpent = (System.currentTimeMillis() - entryTime) / 1000
            sharedViewModel.setTimeSpent(secondsSpent)      // approach 1: shared ViewModel
            onNavigateBackWithTime(secondsSpent)             // approach 2: callback via navigation
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        detailViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    val item = detailViewModel.item.collectAsStateWithLifecycle()
    item.value?.let {
        Detail(
            item = it,
            snackbarHostState = snackbarHostState,
            onSaveItem = detailViewModel::saveEditedItem
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(
    modifier: Modifier = Modifier,
    item: ItemEntity,
    snackbarHostState: SnackbarHostState,
    onSaveItem: (ItemEntity) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Details screen of item = ${item.text}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple80
                )
            )
        }
    ) { paddingValues ->
        val description = rememberSaveable(item.id) { mutableStateOf(item.description) }
        val name = rememberSaveable(item.id) { mutableStateOf(item.text) }
        val focusManager = LocalFocusManager.current
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    label = { Text("Name") },
                    value = name.value ?: "",
                    onValueChange = { newValue ->
                        name.value = newValue
                    }
                )
                TextField(
                    label = { Text("Description") },
                    value = description.value ?: "",
                    onValueChange = { newValue ->
                        description.value = newValue
                    }
                )
                IconButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onSaveItem(
                            item.copy(
                                text = name.value,
                                description = description.value
                            )
                        )
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(Icons.Default.Check, "Save Info")
                }
            }
        }
    }
}