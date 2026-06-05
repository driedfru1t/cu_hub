package com.nikol.lms.data.remote.model.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StudyLevelDto {
    @SerialName("none")
    NONE,
    @SerialName("bachelor")
    BACHELOR,
    @SerialName("master")
    MASTER,
    @SerialName("dpo")
    DPO,
    @SerialName("dpoMaster")
    DPO_MASTER
}