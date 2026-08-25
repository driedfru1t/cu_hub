package com.nikol.lms_impl.di

import com.nikol.lms.data.CourseRepositoryImpl
import com.nikol.lms.data.GradeRepositoryImpl
import com.nikol.lms.data.TaskRepositoryImpl
import com.nikol.lms.data.local.TaskStateStorage
import com.nikol.lms.data.local.TaskStateStorageImpl
import com.nikol.lms.domain.repo.CourseRepository
import com.nikol.lms.domain.repo.GradeRepository
import com.nikol.lms.domain.repo.TaskRepository
import dagger.Binds
import dagger.Module

@Module
interface LmsDataModule {
    @Binds
    fun bindCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository

    @Binds
    fun bindTaskRepo(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    fun bindGradeRepo(gradeRepositoryImpl: GradeRepositoryImpl): GradeRepository

    @Binds
    fun bindTaskStateStorage(taskStateStorageImpl: TaskStateStorageImpl): TaskStateStorage
}