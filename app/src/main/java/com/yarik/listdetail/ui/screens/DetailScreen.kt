package com.yarik.listdetail.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yarik.listdetail.ui.viewmodels.DetailViewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    itemId: Long,
) {
    val detailViewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory> (
        creationCallback = {
            factory -> factory.create(entryId = itemId)
        }
    )
    val item = detailViewModel.item.collectAsStateWithLifecycle()
    item.value?.text?.let {
        Detail(details = it)
    }
}

@Composable
fun Detail(
    modifier: Modifier = Modifier,
    details: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = details
        )
    }
}