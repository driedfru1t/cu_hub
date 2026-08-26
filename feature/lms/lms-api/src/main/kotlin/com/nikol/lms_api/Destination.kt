package com.nikol.lms_api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Courses : NavKey

@Serializable
data object ArchiveCourses : NavKey

@Serializable
data class Course(
    val id: Int,
    val name: String
) : NavKey

@Serializable
data class CourseAction(
    val id: Int,
    val name: String
) : NavKey

@Serializable
data class CourseInfo(
    val sillabusUrl: String?,
    val timeChannelUrl: String?
) : NavKey

@Serializable
data class ThemeMaterial(
    val id: Int,
    val name: String,
    val themeId: Int
) : NavKey

@Serializable
data object Tasks : NavKey

@Serializable
data class TaskDetail(
    val id: Int
) : NavKey
