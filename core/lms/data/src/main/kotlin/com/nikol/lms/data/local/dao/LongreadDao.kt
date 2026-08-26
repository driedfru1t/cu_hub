package com.nikol.lms.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nikol.lms.data.local.entity.Longread

@Dao
interface LongreadDao {

    @Query("SELECT id, themeId, name FROM longreads WHERE themeId = :themeId")
    suspend fun getShortLongread(themeId: Int): List<Longread>
}