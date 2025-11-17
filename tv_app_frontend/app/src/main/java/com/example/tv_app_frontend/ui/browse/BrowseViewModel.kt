package com.example.tv_app_frontend.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv_app_frontend.data.remote.WorkoutItem
import com.example.tv_app_frontend.domain.repository.WorkoutRepository
import kotlinx.coroutines.launch

/**
 * ViewModel to manage workout filters and list for browse screen.
 */
class BrowseViewModel(
    private val repo: WorkoutRepository = WorkoutRepository()
) : ViewModel() {

    private val _items = MutableLiveData<List<WorkoutItem>>(emptyList())
    val items: LiveData<List<WorkoutItem>> = _items

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var currentCategory: String? = null
    private var currentLevel: String? = null
    private var durMin: Int? = null
    private var durMax: Int? = null

    // PUBLIC_INTERFACE
    fun setFilters(category: String?, level: String?, durationMin: Int?, durationMax: Int?) {
        /** Set filters and reload items. */
        currentCategory = category
        currentLevel = level
        durMin = durationMin
        durMax = durationMax
        load()
    }

    // PUBLIC_INTERFACE
    fun load() {
        /** Load list using current filters. */
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val list = repo.fetchWorkouts(currentCategory, currentLevel, durMin, durMax).items
                _items.postValue(list)
            } catch (_: Exception) {
                _items.postValue(emptyList())
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
