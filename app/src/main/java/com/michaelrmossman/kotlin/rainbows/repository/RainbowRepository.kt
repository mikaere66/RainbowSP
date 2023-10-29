package com.michaelrmossman.kotlin.rainbows.repository

import com.michaelrmossman.kotlin.rainbows.dao.RainbowDao
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow
import com.michaelrmossman.kotlin.rainbows.repository.RainbowHelpers.getEmptyRainbowGroup
import com.michaelrmossman.kotlin.rainbows.repository.RainbowHelpers.getRainbowGroupsQuery
import com.michaelrmossman.kotlin.rainbows.utils.rainbowsStartId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RainbowRepository @Inject constructor(
    private val dao: RainbowDao
) {

    fun getRainbowGroupsFlow(
        effect: Int
    ) : Flow<List<Rainbow>> {
        return dao.getRainbowGroupsFlow(
            getRainbowGroupsQuery(dao, effect)
        ).catch {
            emit(getEmptyRainbowGroup())
        }.onEach { rainbows ->
            for (i in rainbows.indices) {
                rainbows[i].id = i.plus(rainbowsStartId)
            }
        }
    }
}
