package com.example.tv_app_frontend.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv_app_frontend.data.remote.HomeResponse
import com.example.tv_app_frontend.domain.repository.HomeRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Home screen providing favorites, recent, continue, and categories.
 */
class HomeViewModel(
    private val repo: HomeRepository = HomeRepository()
) : ViewModel() {

    private val _state = MutableLiveData<HomeState>(HomeState.Loading)
    val state: LiveData<HomeState> = _state

    // PUBLIC_INTERFACE
    fun load() {
        /** Load home data with recommendations fallback. */
        viewModelScope.launch {
            try {
                val home = repo.fetchHome()
                _state.postValue(HomeState.Data(home))
            } catch (ex: Exception) {
                _state.postValue(HomeState.Error(ex.message ?: "Failed to load"))
            }
        }
    }
}

sealed interface HomeState {
    object Loading : HomeState
    data class Data(val home: HomeResponse) : HomeState
    data class Error(val message: String) : HomeState
}
