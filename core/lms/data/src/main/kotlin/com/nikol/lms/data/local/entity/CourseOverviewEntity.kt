package com.nikol.lms.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.LongreadType
import com.nikol.lms.domain.model.PublicationState

@Entity(tableName = "courses")
data class CourseOverviewEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val isArchived: Boolean,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?,
    @Embedded(prefix = "settings_") val settings: CourseSettingsEmbedded
)

data class CourseSettingsEmbedded(
    val skillLevel: CourseSkillLevel,
    val isSkillLevelEnabled: Boolean,
    val syllabusUrl: String?,
    val timeChannelUrl: String?
)

@Entity(
    tableName = "course_themes",
    foreignKeys = [
        ForeignKey(
            entity = CourseOverviewEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class CourseThemeEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val name: String,
    val order: Int,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?
)

@Entity(
    tableName = "longreads",
    foreignKeys = [
        ForeignKey(
            entity = CourseThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["themeId"])]
)
data class LongreadEntity(
    @PrimaryKey val id: Int,
    val themeId: Int,
    val type: LongreadType,
    val name: String,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?
)

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = LongreadEntity::class,
            parentColumns = ["id"],
            childColumns = ["longreadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["longreadId"])]
)
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val longreadId: Int,
    val name: String,
    val maxScore: Double,
    @Embedded(prefix = "activity_") val activity: ActivityDefinitionEmbedded,
    val deadline: String
)

data class ActivityDefinitionEmbedded(
    val id: Int,
    val name: String,
    val weight: Double
)