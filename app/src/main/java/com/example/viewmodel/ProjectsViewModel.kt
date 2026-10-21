package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MotionProject
import com.example.data.repository.MotionProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val repository: MotionProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<MotionProject>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val projectCount: StateFlow<Int> = repository.projectCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun deleteProject(project: MotionProject) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun duplicateProject(project: MotionProject, onDuplicated: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = repository.duplicateProject(project)
            onDuplicated(newId)
        }
    }
}
