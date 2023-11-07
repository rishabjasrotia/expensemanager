package com.nkuppan.expensemanager.domain.usecase.settings.daterange

import com.nkuppan.expensemanager.domain.model.DateRangeType
import com.nkuppan.expensemanager.domain.repository.DateRangeFilterRepository
import com.nkuppan.expensemanager.domain.usecase.transaction.GroupType
import javax.inject.Inject

class GetTransactionGroupTypeUseCase @Inject constructor(
    private val dateRangeFilterRepository: DateRangeFilterRepository
) {

    suspend operator fun invoke(dateRangeType: DateRangeType): GroupType {
        return dateRangeFilterRepository.getTransactionGroupType(dateRangeType)
    }
}