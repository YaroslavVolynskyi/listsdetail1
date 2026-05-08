package com.yarik.listdetail.ui.screens

import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
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
        addNote = listViewModel::addItem
    )
}

@Composable
fun ItemsList(
    modifier: Modifier = Modifier,
    onDelete: (Long) -> Unit,
    navigateToDetails: (Long) -> Unit,
    addNote: (String) -> Unit,
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
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.text ?: ""
                    )
                    IconButton(
                        onClick = { onDelete(item.id) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete note")
                    }
                    Button(onClick = {
                        navigateToDetails(item.id)
                    }) {
                        Text("Details")
                    }
                }
            }
        }
    }
}