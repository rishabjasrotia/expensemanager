package com.nkuppan.expensemanager.domain.usecase.settings.daterange

import com.nkuppan.expensemanager.domain.model.DateRangeFilterType
import com.nkuppan.expensemanager.domain.repository.DateRangeFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSelectedFilterNameAndDateRangeUseCase @Inject constructor(
    private val dateRangeFilterRepository: DateRangeFilterRepository,
    private val getDateRangeFilterTypeUseCase: GetDateRangeFilterTypeUseCase,
    private val getFilterRangeDateStringUseCase: GetFilterRangeDateStringUseCase,
) {

    operator fun invoke(): Flow<String> {
        return getDateRangeFilterTypeUseCase.invoke().map {
            val filterRangeText = if (it != DateRangeFilterType.ALL) {
                " (${getFilterRangeDateStringUseCase.invoke(it)})"
            } else {
                ""
            }
            val filterNameText = dateRangeFilterRepository.getDateRangeFilterRangeName(it)
            return@map "${filterNameText}${filterRangeText}"
        }
    }
}