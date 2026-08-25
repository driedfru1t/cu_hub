package com.nikol.lms_impl.di

import com.nikol.common.CuHubDispatcher
import com.nikol.common.Dispatcher
import com.nikol.lms.domain.repo.CourseRepository
import com.nikol.lms.domain.repo.GradeRepository
import com.nikol.lms.domain.repo.TaskRepository
import com.nikol.lms.domain.useCase.GetArchiveCourses
import com.nikol.lms.domain.useCase.GetCourseActivitiesPerformanceUseCase
import com.nikol.lms.domain.useCase.GetCourseDeadlines
import com.nikol.lms.domain.useCase.GetCourseExercisesUseCase
import com.nikol.lms.domain.useCase.GetCourseScoreUseCase
import com.nikol.lms.domain.useCase.GetCourseTasksPerformanceUseCase
import com.nikol.lms.domain.useCase.GetCourseUseCase
import com.nikol.lms.domain.useCase.GetCoursesUseCase
import com.nikol.lms.domain.useCase.GetTaskDetails
import com.nikol.lms.domain.useCase.GetTasksUseCase
import com.nikol.lms.domain.useCase.GetThemeMaterialsUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module
class LmsUseCaseModule {

    @Provides
    @LmsScope
    fun provideGetCoursesUseCase(courseRepository: CourseRepository): GetCoursesUseCase =
        GetCoursesUseCase(courseRepository)

    @Provides
    @LmsScope
    fun provideGetCourseOverview(
        courseRepository: CourseRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseUseCase {
        return GetCourseUseCase(coroutineDispatcher, courseRepository)
    }

    @Provides
    @LmsScope
    fun provideGetTaskDetailUseCase(
        taskRepository: TaskRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetTaskDetails {
        return GetTaskDetails(taskRepository, coroutineDispatcher)
    }

    @Provides
    @LmsScope
    fun provideGetMaterialUseCase(
        courseRepository: CourseRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetThemeMaterialsUseCase {
        return GetThemeMaterialsUseCase(courseRepository, coroutineDispatcher)
    }

    @Provides
    @LmsScope
    fun provideGetCourseDeadlines(
        courseRepository: CourseRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseDeadlines {
        return GetCourseDeadlines(courseRepository, coroutineDispatcher)
    }

    @Provides
    @LmsScope
    fun provideGetCourseActivitiesPerformanceUseCase(
        repo: GradeRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseActivitiesPerformanceUseCase {
        return GetCourseActivitiesPerformanceUseCase(coroutineDispatcher, repo)
    }

    @Provides
    @LmsScope
    fun provideGetCourseExercisesUseCase(
        taskRepo: TaskRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseExercisesUseCase {
        return GetCourseExercisesUseCase(coroutineDispatcher, taskRepo)
    }

    @Provides
    @LmsScope
    fun provideGetCourseTasksPerformanceUseCase(
        repo: GradeRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseTasksPerformanceUseCase {
        return GetCourseTasksPerformanceUseCase(coroutineDispatcher, repo)
    }

    @Provides
    @LmsScope
    fun provideGetCourseScoreUseCase(
        repo: CourseRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetCourseScoreUseCase {
        return GetCourseScoreUseCase(coroutineDispatcher, repo)
    }

    @Provides
    @LmsScope
    fun provideGetArchiveCourses(
        repo: CourseRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetArchiveCourses {
        return GetArchiveCourses(coroutineDispatcher, repo)
    }

    @Provides
    @LmsScope
    fun provideGetTaskUseCase(
        repo: TaskRepository,
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher
    ): GetTasksUseCase {
        return GetTasksUseCase(coroutineDispatcher, repo)
    }

}