package com.nikol.lms.data.local

import androidx.room.TypeConverter
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.LongreadType
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.lms.domain.model.PublicationState

class DatabaseConverters {
    @TypeConverter
    fun fromPublicationState(state: PublicationState): String = state.name

    @TypeConverter
    fun toPublicationState(state: String): PublicationState = PublicationState.valueOf(state)

    @TypeConverter
    fun fromCourseSkillLevel(level: CourseSkillLevel): String = level.name

    @TypeConverter
    fun toCourseSkillLevel(level: String): CourseSkillLevel = CourseSkillLevel.valueOf(level)

    @TypeConverter
    fun fromLongreadType(type: LongreadType): String = type.name

    @TypeConverter
    fun toLongreadType(type: String): LongreadType = LongreadType.valueOf(type)

    @TypeConverter
    fun fromParticipationType(participationType: ParticipationType) = participationType.name

    @TypeConverter
    fun toParticipationType(participationType: String) = ParticipationType.valueOf(participationType)
}