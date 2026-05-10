package com.yarik.listdetail.ui

import androidx.compose.runtime.Immutable
import com.yarik.listdetail.data.ItemEntity

@Immutable
data class ListState(
    val items: List<ItemEntity> = listOf(),
    val backgroundEnabledIds: Set<Long> = emptySet()
)