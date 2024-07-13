package com.example.citybusfinder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.citybusfinder.sampledata.History
import kotlinx.coroutines.flow.Flow

@Dao
interface  HistoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertHistory(history : History)

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
       fun getAllHistory() : Flow<List<History>>

    @Query("DELETE FROM history_table")
    suspend fun clearAllHistory()
}