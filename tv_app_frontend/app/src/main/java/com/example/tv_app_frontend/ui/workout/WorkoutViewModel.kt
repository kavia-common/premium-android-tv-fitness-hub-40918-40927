package com.example.tv_app_frontend.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv_app_frontend.data.remote.WorkoutDetail
import com.example.tv_app_frontend.domain.repository.WorkoutRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Workout detail and playback coordination.
 */
class WorkoutViewModel(
    private val repo: WorkoutRepository = WorkoutRepository()
) : ViewModel() {

    private val _detail = MutableLiveData<WorkoutDetail?>()
    val detail: LiveData<WorkoutDetail?> = _detail

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    // PUBLIC_INTERFACE
    fun loadWorkout(id: String) {
        /** Load workout detail by id. */
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val item = repo.fetchWorkoutDetail(id)
                _detail.postValue(item)
            } catch (_: Exception) {
                _detail.postValue(null)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
