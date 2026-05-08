package com.yarik.listdetail.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.listdetail.data.ItemEntity
import com.yarik.listdetail.data.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    val repository: Repository,
    @Assisted val entryId: Long,
): ViewModel() {


    val item: StateFlow<ItemEntity?> = repository
        .getById(entryId)
        .stateIn(viewModelScope,  SharingStarted.WhileSubscribed(5000) ,null)

    @AssistedFactory
    interface Factory {
        fun create(entryId: Long) : DetailViewModel
    }
}