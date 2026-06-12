package com.nikol.lms_impl.di

import com.nikol.lms.domain.repo.CourseRepository
import com.nikol.lms.domain.useCase.GetCoursesUseCase
import dagger.Module
import dagger.Provides

@Module
class LmsUseCaseModule {

    @Provides
    @LmsScope
    fun provideGetCoursesUseCase(courseRepository: CourseRepository): GetCoursesUseCase =
        GetCoursesUseCase(courseRepository)

}