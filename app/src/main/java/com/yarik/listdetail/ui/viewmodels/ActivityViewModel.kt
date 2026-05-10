package com.yarik.listdetail.ui.viewmodels

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
inline fun <reified T : ViewModel> activityViewModel(): T {
    return hiltViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    )
}