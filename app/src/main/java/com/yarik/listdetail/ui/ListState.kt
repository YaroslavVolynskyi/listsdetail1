package com.yarik.listdetail.ui

import androidx.compose.runtime.Immutable
import com.yarik.listdetail.data.ItemEntity

@Immutable
sealed interface ListUiState {
    data object Loading : ListUiState
    data class Error(val message: String) : ListUiState
    data class Success(
        val items: List<ItemEntity> = listOf(),
        val backgroundEnabledIds: Set<Long> = emptySet()
    ) : ListUiState
}