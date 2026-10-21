package com.example.data.repository

import com.example.data.dao.MotionProjectDao
import com.example.data.model.MotionProject
import kotlinx.coroutines.flow.Flow

class MotionProjectRepository(
    private val dao: MotionProjectDao
) {
    val allProjects: Flow<List<MotionProject>> = dao.getAllProjects()
    val projectCount: Flow<Int> = dao.getProjectCount()

    suspend fun getProjectById(id: Long): MotionProject? {
        return dao.getProjectById(id)
    }

    suspend fun saveProject(project: MotionProject): Long {
        return dao.insertProject(project)
    }

    suspend fun updateProject(project: MotionProject) {
        dao.updateProject(project)
    }

    suspend fun deleteProject(project: MotionProject) {
        dao.deleteProject(project)
    }

    suspend fun deleteProjectById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun duplicateProject(original: MotionProject): Long {
        val copy = original.copy(
            id = 0,
            title = "${original.title} (কপি)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return dao.insertProject(copy)
    }
}
