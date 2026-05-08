package com.yarik.listdetail.ui

import com.yarik.listdetail.data.ItemEntity

data class ListState(
    val items: List<ItemEntity> = listOf(),
    val backgroundEnabledIds: Set<Long> = emptySet()
)