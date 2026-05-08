package com.yarik.listdetail.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    navigateToDetails: (Long) -> Unit,
    listViewModel: ListViewModel = hiltViewModel(),
) {

    val listState = listViewModel.itemsState.collectAsStateWithLifecycle()
    ItemsList(
        itemsList = listState.value.items,
        onDelete = listViewModel::deleteItem,
        navigateToDetails = navigateToDetails,
        addNote = listViewModel::addItem,
        onSave = listViewModel::onSaveClicked,
        onCheckedChange = listViewModel::onCheckedChanged
    )
}

@Composable
fun ItemsList(
    modifier: Modifier = Modifier,
    onDelete: (Long) -> Unit,
    navigateToDetails: (Long) -> Unit,
    addNote: (String) -> Unit,
    onSave: (String, Long) -> Unit,
    onCheckedChange: (Boolean, Long) -> Unit,
    itemsList: List<ItemEntity>
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNote("item ${itemsList.size + 1}") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(items = itemsList, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val focusManager = LocalFocusManager.current
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val text =
                            rememberSaveable(item.text) { mutableStateOf(item.text ?: "") }
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
                        Checkbox(checked = item.isChecked, onCheckedChange = { isChecked ->
                            onCheckedChange(isChecked, item.id)
                        })
                        IconButton(
                            onClick = { onDelete(item.id) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete note")
                        }
                        IconButton (onClick = {
                            onSave(text.value, item.id)
                            focusManager.clearFocus()
                        }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                        IconButton(onClick = { navigateToDetails(item.id) }) {
                            Icon(Icons.Default.Info, contentDescription = "Details")
                        }
                    }
                }
            }
        }
    }
}