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

    private val _thumbnailUrl = MutableLiveData<String?>()
    val thumbnailUrl: LiveData<String?> = _thumbnailUrl

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    // PUBLIC_INTERFACE
    fun loadWorkout(id: String) {
        /** Load workout detail by id and discover thumbnail for backdrop. */
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val item = repo.fetchWorkoutDetail(id)
                _detail.postValue(item)

                // Discover thumbnail from local/mock workouts list
                val list = repo.fetchWorkouts().items
                val thumb = list.firstOrNull { it.id == id }?.thumbnailUrl
                _thumbnailUrl.postValue(thumb)
            } catch (_: Exception) {
                _detail.postValue(null)
                _thumbnailUrl.postValue(null)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
