package com.nikol.lms_impl.di

import com.nikol.lms.data.CourseRepositoryImpl
import com.nikol.lms.domain.repo.CourseRepository
import dagger.Binds
import dagger.Module

@Module
interface LmsDataModule {
    @Binds
    fun bindCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository
}