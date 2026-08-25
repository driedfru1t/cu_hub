package com.nikol.lms.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class LongreadWithExercises(
    @Embedded val longread: LongreadEntity,
    @Relation(parentColumn = "id", entityColumn = "longreadId")
    val exercises: List<ExerciseEntity>
)

data class ThemeWithLongreads(
    @Embedded val theme: CourseThemeEntity,
    @Relation(entity = LongreadEntity::class, parentColumn = "id", entityColumn = "themeId")
    val longreads: List<LongreadWithExercises>
)

data class CourseOverviewWithThemes(
    @Embedded val course: CourseOverviewEntity,
    @Relation(entity = CourseThemeEntity::class, parentColumn = "id", entityColumn = "courseId")
    val themes: List<ThemeWithLongreads>
)

