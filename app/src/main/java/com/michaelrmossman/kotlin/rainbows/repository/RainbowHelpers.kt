package com.michaelrmossman.kotlin.rainbows.repository

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.rainbows.dao.RainbowDao
import com.michaelrmossman.kotlin.rainbows.database.GROUP_NUMBER_COLUMN
import com.michaelrmossman.kotlin.rainbows.database.RAINBOWS_TABLE_NAME
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow
import com.michaelrmossman.kotlin.rainbows.utils.blue
import com.michaelrmossman.kotlin.rainbows.utils.debug
import com.michaelrmossman.kotlin.rainbows.utils.rainbowGroupNum
import com.michaelrmossman.kotlin.rainbows.utils.green
import com.michaelrmossman.kotlin.rainbows.utils.ledCurrentOff
import com.michaelrmossman.kotlin.rainbows.utils.policeGroupNum
import com.michaelrmossman.kotlin.rainbows.utils.red
import com.michaelrmossman.kotlin.rainbows.utils.rgbGroupNum
import com.michaelrmossman.kotlin.rainbows.utils.sqlDanceQueryLimit
import com.michaelrmossman.kotlin.rainbows.utils.sqlRainbowQueryLimit
import com.michaelrmossman.kotlin.rainbows.utils.sqlUnionMultiplier

object RainbowHelpers {

    fun getEmptyRainbowGroup(): List<Rainbow> {
        val group = mutableListOf<Rainbow>()
        val colours = listOf(
            red, green, blue
        )
        colours.forEach { colour ->
            for (i in 1..3) {
                group.add(
                    Rainbow(
                        groupNum = 0,
                        ledNum = i,
                        ledCurrent = ledCurrentOff,
                        rgbValue = colour
                    )
                )
            }
        }
        return group
    }

    fun getRainbowGroupsQuery(
        dao: RainbowDao,
        effect: Int
    ) : SimpleSQLiteQuery {
        val args: Array<Any?> = arrayOfNulls(
            when (effect) {
                in 2..3 -> sqlUnionMultiplier // 33
                else -> 1
            }
        )

        args[0] = when (effect) {
            3 -> rgbGroupNum           // (Val: 5)
            0 -> rainbowGroupNum         // (Val: 2)
            else -> policeGroupNum // 1-2 (Val: 4)
        }

        val sb = StringBuilder()
        sb.append("SELECT * FROM $RAINBOWS_TABLE_NAME")
        sb.append(" ")
        when (effect) {
            1 -> {
                sb.append("WHERE $GROUP_NUMBER_COLUMN < ?") // Note: LT
                sb.append(" ") // Limit must come AFTER order by random
                sb.append("ORDER BY RANDOM() LIMIT $sqlDanceQueryLimit")
            }
            else -> {
                sb.append("WHERE $GROUP_NUMBER_COLUMN = ?") // 2-3
                if (effect == 0) {
                    sb.append(" ")
                    sb.append("LIMIT $sqlRainbowQueryLimit") // 1500
                }
            }
        }

        if (effect > 1) {
            val numRows =  when (effect) {
                2 -> dao.getRainbowGroupCount(policeGroupNum)   // (Val: 4)
                else -> dao.getRainbowGroupCount(rgbGroupNum) // 3 (Val: 5)
            }

            for (i in 1 until sqlUnionMultiplier) {
                args[i] = when (effect) {
                    2 -> policeGroupNum   // (Val: 4)
                    else -> rgbGroupNum // 3 (Val: 5)
                }

                sb.append(" ")
                sb.append("UNION ALL")
                sb.append(" ")
                sb.append("SELECT * FROM $RAINBOWS_TABLE_NAME")
                sb.append(" ")
                sb.append("WHERE $GROUP_NUMBER_COLUMN = ?")

                /* In the case of both Police and RBG groups, the last
                   row is a "blank" which calls for a Thread.sleep()
                   in MainActivity. We need to EXCLUDE this last row
                   so that setLightsOff() can be called at the end */
                if (i == sqlUnionMultiplier.minus(1)) {
                    // LIMIT clause should come after UNION ALL not before
                    val limit = numRows.times(sqlUnionMultiplier).minus(1)
                    sb.append(" ")
                    sb.append("LIMIT $limit")
                }
            }
        }

        if (debug) { Log.d("QUERY", sb.toString()) }

        return SimpleSQLiteQuery(sb.toString(), args)
    }
}
