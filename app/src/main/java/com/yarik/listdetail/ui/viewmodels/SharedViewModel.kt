package com.yarik.listdetail.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _timeSpentOnDetail = MutableStateFlow<Long?>(null)
    val timeSpentOnDetail = _timeSpentOnDetail.asStateFlow()

    fun setTimeSpent(seconds: Long) {
        _timeSpentOnDetail.value = seconds
    }

    fun clearTimeSpent() {
        _timeSpentOnDetail.value = null
    }
}