package com.michaelrmossman.kotlin.rainbows.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.rainbows.database.GROUP_NUMBER_COLUMN
import com.michaelrmossman.kotlin.rainbows.database.RAINBOWS_TABLE_NAME
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow
import kotlinx.coroutines.flow.Flow

@Dao
interface RainbowDao {

    @Query("SELECT COUNT (*)" +
           " " +
           "FROM $RAINBOWS_TABLE_NAME" +
           " " +
           "WHERE $GROUP_NUMBER_COLUMN" +
           " " +
           "= :groupNum")
    fun getRainbowGroupCount(
        groupNum: Int
    ) : Int

    @RawQuery(
        observedEntities = [Rainbow::class]
    )
    fun getRainbowGroupsFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<Rainbow>>
}
