package com.michaelrmossman.kotlin.rainbows.viewmodel

import androidx.lifecycle.ViewModel
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow
import com.michaelrmossman.kotlin.rainbows.repository.RainbowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RainbowViewModel @Inject constructor(
    private val repository: RainbowRepository
) : ViewModel() {

    fun getRainbowGroups(
        effect: Int
    ) : Flow<List<Rainbow>> {
        return repository.getRainbowGroupsFlow(
            effect
        )
    }
}
