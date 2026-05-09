package com.yarik.listdetail.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.listdetail.data.ItemEntity
import com.yarik.listdetail.data.Repository
import com.yarik.listdetail.ui.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    val repository: Repository
): ViewModel() {

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val backgroundEnabledIds = MutableStateFlow<Set<Long>>(emptySet())

//    val itemsState = repository.getAllItems()
//        .map { items -> ListState(items) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

    val itemsState = combine(
        repository.getAllItems(),
        backgroundEnabledIds
    ) { items, backgroundEnabledIds ->
        ListState(
            items,
            backgroundEnabledIds
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

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
            _snackbarEvent.send("Saved!")
        }
    }

    fun onSaveDescription(description: String, id: Long) {
        viewModelScope.launch {
            repository.updateDescription(description, id)
            _snackbarEvent.send("Description saved!")
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
}