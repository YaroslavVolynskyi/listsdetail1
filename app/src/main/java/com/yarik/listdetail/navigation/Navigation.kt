package com.yarik.listdetail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object ListScreenKey: NavKey

@Serializable
data class DetailsScreenKey(
    val noteId: Long
): NavKey