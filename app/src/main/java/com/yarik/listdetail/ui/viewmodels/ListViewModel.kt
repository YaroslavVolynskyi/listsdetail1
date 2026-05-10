package com.yarik.listdetail.ui.viewmodels

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.listdetail.data.ItemEntity
import com.yarik.listdetail.data.Repository
import com.yarik.listdetail.ui.ListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    val repository: Repository
): BaseViewModel(), DefaultLifecycleObserver {

    private val backgroundEnabledIds = MutableStateFlow<Set<Long>>(emptySet())

//    val itemsState = repository.getAllItems()
//        .map { items -> ListState(items) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

    val itemsState = combine(
        repository.getAllItems(),
        backgroundEnabledIds
    ) { items, backgroundEnabledIds ->
        ListUiState.Success(
            items,
            backgroundEnabledIds
        ) as ListUiState
    }.onStart { emit(ListUiState.Loading) }
        .catch { e -> emit(ListUiState.Error(e.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    fun addItem(text: String) {
        viewModelScope.launch {
            repository.upsert(
                ItemEntity(text = text)
            )
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    fun onSaveClicked(currentText: String, id: Long) {
        viewModelScope.launch {
            repository.updateText(currentText, id)
            showSnackbar("Saved!")
        }
    }

    fun onSaveDescription(description: String, id: Long) {
        viewModelScope.launch {
            repository.updateDescription(description, id)
            showSnackbar("Description saved!")
        }
    }

    fun onCheckedChanged(isChecked: Boolean, id: Long) {
        viewModelScope.launch {
            repository.onCheckedChanged(isChecked, id)
        }
    }

    fun toggleBackground(id: Long) {
        if (backgroundEnabledIds.value.contains(id)) {
            backgroundEnabledIds.value -= id
        } else {
            backgroundEnabledIds.value += id
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.e("yarik", "onPause from viewModel")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Log.e("yarik", "onResume from viewModel")
    }
}