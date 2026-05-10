package com.yarik.listdetail.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    protected suspend fun showSnackbar(message: String) {
        _snackbarEvent.send(message)
    }
}
