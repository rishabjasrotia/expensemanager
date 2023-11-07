package com.nkuppan.expensemanager.domain.usecase.transaction

import com.nkuppan.expensemanager.domain.model.AverageData
import com.nkuppan.expensemanager.domain.model.DateRangeType
import com.nkuppan.expensemanager.domain.model.WholeAverageData
import com.nkuppan.expensemanager.domain.model.isExpense
import com.nkuppan.expensemanager.domain.model.isIncome
import com.nkuppan.expensemanager.domain.usecase.settings.currency.GetCurrencyUseCase
import com.nkuppan.expensemanager.domain.usecase.settings.daterange.GetDateRangeUseCase
import com.nkuppan.expensemanager.ui.utils.getCurrency
import com.nkuppan.expensemanager.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.util.Calendar
import javax.inject.Inject

class GetAverageDataUseCase @Inject constructor(
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val getTransactionWithFilterUseCase: GetTransactionWithFilterUseCase,
    private val getDateRangeUseCase: GetDateRangeUseCase,
    private val dispatcher: AppCoroutineDispatchers,
) {
    fun invoke(): Flow<WholeAverageData> {
        return combine(
            getCurrencyUseCase.invoke(),
            getDateRangeUseCase.invoke(),
            getTransactionWithFilterUseCase.invoke(),
        ) { currency, dateRangeModel, transactions ->
            val incomeAmount =
                transactions?.filter { it.type.isIncome() }?.sumOf { it.amount } ?: 0.0
            val expenseAmount =
                transactions?.filter { it.type.isExpense() }?.sumOf { it.amount } ?: 0.0

            val ranges = dateRangeModel.dateRanges

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = ranges[0]

            val weeksPerMonth = when (dateRangeModel.type) {
                DateRangeType.TODAY,
                DateRangeType.THIS_WEEK,
                DateRangeType.THIS_MONTH -> {
                    calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
                }

                DateRangeType.CUSTOM,
                DateRangeType.ALL,
                DateRangeType.THIS_YEAR -> {
                    calendar.getActualMaximum(Calendar.WEEK_OF_YEAR)
                }
            }

            val daysPerMonth = when (dateRangeModel.type) {
                DateRangeType.TODAY,
                DateRangeType.THIS_WEEK,
                DateRangeType.THIS_MONTH -> {
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                }

                DateRangeType.CUSTOM,
                DateRangeType.ALL,
                DateRangeType.THIS_YEAR -> {
                    calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                }
            }

            val monthsPerYear = when (dateRangeModel.type) {
                DateRangeType.TODAY,
                DateRangeType.THIS_WEEK,
                DateRangeType.THIS_MONTH -> {
                    1
                }

                DateRangeType.CUSTOM,
                DateRangeType.ALL,
                DateRangeType.THIS_YEAR -> {
                    calendar.getActualMaximum(Calendar.MONTH) + 1
                }
            }

            WholeAverageData(
                expenseAverageData = AverageData(
                    perDay = getCurrency(currency, (expenseAmount / daysPerMonth)),
                    perWeek = getCurrency(currency, (expenseAmount / weeksPerMonth)),
                    perMonth = getCurrency(currency, (expenseAmount / monthsPerYear)),
                ),
                incomeAverageData = AverageData(
                    perDay = getCurrency(currency, (incomeAmount / daysPerMonth)),
                    perWeek = getCurrency(currency, (incomeAmount / weeksPerMonth)),
                    perMonth = getCurrency(currency, (incomeAmount / monthsPerYear)),
                )
            )
        }.flowOn(dispatcher.computation)
    }
}