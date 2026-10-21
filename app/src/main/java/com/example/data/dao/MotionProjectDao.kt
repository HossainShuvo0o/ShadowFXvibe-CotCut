package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MotionProject
import kotlinx.coroutines.flow.Flow

@Dao
interface MotionProjectDao {
    @Query("SELECT * FROM motion_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<MotionProject>>

    @Query("SELECT * FROM motion_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): MotionProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: MotionProject): Long

    @Update
    suspend fun updateProject(project: MotionProject)

    @Delete
    suspend fun deleteProject(project: MotionProject)

    @Query("DELETE FROM motion_projects WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM motion_projects")
    fun getProjectCount(): Flow<Int>
}
