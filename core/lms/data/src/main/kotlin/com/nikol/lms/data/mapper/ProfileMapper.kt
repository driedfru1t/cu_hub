package com.nikol.lms.data.mapper

import com.nikol.lms.data.remote.model.student.CurrentStudentResponseDto
import com.nikol.lms.data.remote.model.student.StudyLevelDto
import com.nikol.lms.domain.model.LmsProfile
import com.nikol.lms.domain.model.StudyLevel

fun StudyLevelDto.toDomain(): StudyLevel = when (this) {
    StudyLevelDto.NONE -> StudyLevel.NONE
    StudyLevelDto.BACHELOR -> StudyLevel.BACHELOR
    StudyLevelDto.MASTER -> StudyLevel.MASTER
    StudyLevelDto.DPO -> StudyLevel.DPO
    StudyLevelDto.DPO_MASTER -> StudyLevel.DPO_MASTER
}

fun CurrentStudentResponseDto.toDomain(): LmsProfile = LmsProfile(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    universityEmail = universityEmail,
    timeAccount = timeAccount,
    studyStartYear = studyStartYear,
    studyLevel = studyLevelDto.toDomain(),
    lateDaysBalance = lateDaysBalance
)