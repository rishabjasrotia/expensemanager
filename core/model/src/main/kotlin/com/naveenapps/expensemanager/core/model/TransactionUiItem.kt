package com.naveenapps.expensemanager.core.model

import com.naveenapps.expensemanager.core.common.utils.toCompleteDateWithDate


data class TransactionUiState(
    val date: String,
    val amountTextColor: Int,
    val totalAmount: Amount,
    val transactions: List<TransactionUiItem>,
    val isLastItem: Boolean = false
)

data class TransactionUiItem(
    val id: String,
    val amount: Amount = Amount(0.0),
    val notes: String?,
    val categoryName: String,
    val transactionType: TransactionType,
    val categoryBackgroundColor: String,
    val categoryIcon: String,
    val date: String,
    val fromAccountName: String,
    val fromAccountIcon: String,
    val fromAccountColor: String,
    val toAccountName: String? = null,
    val toAccountIcon: String? = null,
    val toAccountColor: String? = null,
)


fun Transaction.toTransactionUIModel(amount: Amount): TransactionUiItem {
    return TransactionUiItem(
        this.id,
        amount,
        this.notes,
        this.category.name,
        this.type,
        this.category.iconBackgroundColor,
        this.category.iconName,
        date = this.createdOn.toCompleteDateWithDate(),
        fromAccountName = this.fromAccount.name,
        fromAccountIcon = this.fromAccount.iconName,
        fromAccountColor = this.fromAccount.iconBackgroundColor,
        toAccountName = this.toAccount?.name,
        toAccountIcon = this.toAccount?.iconName,
        toAccountColor = this.toAccount?.iconBackgroundColor,
    )
}