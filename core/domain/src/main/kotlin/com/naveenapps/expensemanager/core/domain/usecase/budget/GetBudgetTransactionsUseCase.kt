package com.naveenapps.expensemanager.core.domain.usecase.budget

import com.naveenapps.expensemanager.core.common.utils.fromMonthAndYear
import com.naveenapps.expensemanager.core.common.utils.toMonthAndYear
import com.naveenapps.expensemanager.core.model.Budget
import com.naveenapps.expensemanager.core.model.CategoryType
import com.naveenapps.expensemanager.core.model.Resource
import com.naveenapps.expensemanager.core.model.Transaction
import com.naveenapps.expensemanager.core.repository.AccountRepository
import com.naveenapps.expensemanager.core.repository.CategoryRepository
import com.naveenapps.expensemanager.core.repository.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import org.joda.time.DateTime
import javax.inject.Inject

class GetBudgetTransactionsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(budget: Budget): Resource<List<Transaction>> {
        val accounts: List<String> = if (budget.isAllAccountsSelected) {
            accountRepository.getAccounts().firstOrNull()?.map { it.id } ?: emptyList()
        } else {
            budget.accounts
        }

        val categories: List<String> = if (budget.isAllCategoriesSelected) {
            categoryRepository.getCategories().firstOrNull()?.map { it.id } ?: emptyList()
        } else {
            budget.categories
        }

        val date = budget.selectedMonth.fromMonthAndYear()
        val startDayOfMonth = DateTime(date).withDayOfMonth(1)
            .withTimeAtStartOfDay().millis
        val endDayOfMonth = DateTime()
            .run {
                return@run dayOfMonth().withMaximumValue().plusDays(1)
                    .withTimeAtStartOfDay().millis
            }

        val transaction = transactionRepository.getFilteredTransaction(
            accounts = accounts,
            categories = categories,
            transactionType = CategoryType.entries.map { it.ordinal }.toList(),
            startDate = startDayOfMonth,
            endDate = endDayOfMonth,
        ).firstOrNull()?.filter {
            it.createdOn.toMonthAndYear() == budget.selectedMonth
        }

        return Resource.Success(transaction ?: emptyList())
    }
}
