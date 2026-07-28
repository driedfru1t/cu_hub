package com.nikol.lms.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nikol.lms.domain.model.ParticipationType

@Entity(tableName = "course_summaries")
data class CourseSummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "publish_date")
    val publishDate: String?,

    @ColumnInfo(name = "published_at")
    val publishedAt: String?,

    @Embedded(prefix = "settings_")
    val settings: CourseSettingsEntity,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int?,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "participation_type")
    val participationType: ParticipationType,

    @ColumnInfo(name = "all_order_index")
    val allOrderIndex: Int,

    @ColumnInfo(name = "category_order_index")
    val categoryOrderIndex: Int
)

data class CourseSettingsEntity(
    @ColumnInfo(name = "skill_level")
    val skillLevel: String,

    @ColumnInfo(name = "is_skill_level_enabled")
    val isSkillLevelEnabled: Boolean,

    @ColumnInfo(name = "syllabus_url")
    val syllabusUrl: String?,

    @ColumnInfo(name = "time_channel_url")
    val timeChannelUrl: String?
)