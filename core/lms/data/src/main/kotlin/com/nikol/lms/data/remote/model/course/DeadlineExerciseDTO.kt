package com.nikol.lms.data.remote.model.course


import com.nikol.lms.data.remote.model.task.TaskTypeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeadlineExerciseDTO(
    @SerialName("activity")
    val deadlineActivityDto: DeadlineActivityDTO,
    @SerialName("deadline")
    val deadline: String,
    @SerialName("id")
    val id: Int,
    @SerialName("maxScore")
    val maxScore: Double,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("type")
    val type: TaskTypeDto
)
