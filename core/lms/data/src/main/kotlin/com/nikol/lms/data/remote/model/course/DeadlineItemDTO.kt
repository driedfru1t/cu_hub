package com.nikol.lms.data.remote.model.course


import com.nikol.lms.data.remote.model.common.ShortThemeDTO
import com.nikol.lms.data.remote.model.task.ShortLongreadDto
import com.nikol.lms.data.remote.model.task.TaskCourseDto
import com.nikol.lms.data.remote.model.task.TaskStateDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeadlineItemDTO(
    @SerialName("course")
    val courseDto: TaskCourseDto,
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("deadline")
    val deadline: String?,
    @SerialName("exercise")
    val exercise: DeadlineExerciseDTO,
    @SerialName("id")
    val id: Int,
    @SerialName("longread")
    val longreadDto: ShortLongreadDto,
    @SerialName("rejectAt")
    val rejectAt: String?,
    @SerialName("reviewer")
    val reviewer: String?,
    @SerialName("state")
    val state: TaskStateDto,
    @SerialName("theme")
    val themeDto: ShortThemeDTO
)

typealias DeadlineItemsDto = List<DeadlineItemDTO>
