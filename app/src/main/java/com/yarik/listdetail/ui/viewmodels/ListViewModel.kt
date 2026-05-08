package com.yarik.listdetail.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.listdetail.data.ItemEntity
import com.yarik.listdetail.data.Repository
import com.yarik.listdetail.ui.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    val repository: Repository
): ViewModel() {

//    val itemsState = MutableStateFlow(ListState())

    val itemsState = repository.getAllItems()
        .map { items -> ListState(items) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

//    fun getAllItems(): Flow<List<ItemEntity>>
//
//    fun getById(id: Long): Flow<ItemEntity>

    fun addItem(text: String) {
        viewModelScope.async {
            repository.addItem(
                ItemEntity(text = text)
            )
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.async {
            repository.deleteItem(itemId)
        }
    }
}