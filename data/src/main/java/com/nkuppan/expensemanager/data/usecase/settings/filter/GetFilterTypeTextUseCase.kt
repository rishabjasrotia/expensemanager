package com.nkuppan.expensemanager.data.usecase.settings.filter

import com.nkuppan.expensemanager.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFilterTypeTextUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<String> {
        return settingsRepository.getFilterType().map {
            settingsRepository.getFilterRangeValue(it)
        }
    }
}