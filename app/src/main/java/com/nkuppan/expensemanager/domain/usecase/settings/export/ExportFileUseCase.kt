package com.nkuppan.expensemanager.domain.usecase.settings.export

import com.nkuppan.expensemanager.domain.model.DateRangeType
import com.nkuppan.expensemanager.domain.model.ExportFileType
import com.nkuppan.expensemanager.domain.model.Resource
import com.nkuppan.expensemanager.domain.repository.ExportRepository
import com.nkuppan.expensemanager.domain.usecase.transaction.GetExportTransactionsUseCase
import com.nkuppan.expensemanager.presentation.account.list.AccountUiModel
import javax.inject.Inject

class ExportFileUseCase @Inject constructor(
    private val exportRepository: ExportRepository,
    private val getExportTransactionsUseCase: GetExportTransactionsUseCase
) {

    suspend operator fun invoke(
        exportFileType: ExportFileType,
        uri: String?,
        dateRangeType: DateRangeType,
        accounts: List<AccountUiModel>,
        isAllAccountsSelected: Boolean
    ): Resource<String?> {

        return when (
            val transactions = getExportTransactionsUseCase.invoke(
                dateRangeType,
                accounts.map { it.id },
                isAllAccountsSelected
            )
        ) {
            is Resource.Error -> {
                transactions
            }

            is Resource.Success -> {
                when (exportFileType) {
                    ExportFileType.CSV -> {
                        exportRepository.createCsvFile(uri, transactions.data)
                    }

                    ExportFileType.PDF -> {
                        exportRepository.createPdfFile(uri, transactions.data)
                    }
                }
            }
        }
    }
}