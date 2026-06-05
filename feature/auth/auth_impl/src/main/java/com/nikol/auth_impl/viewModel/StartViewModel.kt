package com.nikol.auth_impl.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            courseRepository.getCourses()
        }
    }
}