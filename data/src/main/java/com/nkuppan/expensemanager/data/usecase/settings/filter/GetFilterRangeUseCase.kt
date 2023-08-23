package com.nkuppan.expensemanager.data.usecase.settings.filter

import com.nkuppan.expensemanager.core.model.FilterType
import com.nkuppan.expensemanager.data.repository.SettingsRepository
import javax.inject.Inject

class GetFilterRangeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(filterType: FilterType): List<Long> {
        return settingsRepository.getFilterRange(filterType)
    }
}