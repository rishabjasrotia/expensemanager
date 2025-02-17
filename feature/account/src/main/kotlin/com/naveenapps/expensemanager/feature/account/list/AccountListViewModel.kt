package com.naveenapps.expensemanager.feature.account.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveenapps.expensemanager.core.common.utils.UiState
import com.naveenapps.expensemanager.core.domain.usecase.account.GetAllAccountsUseCase
import com.naveenapps.expensemanager.core.domain.usecase.settings.currency.GetCurrencyUseCase
import com.naveenapps.expensemanager.core.domain.usecase.settings.currency.GetFormattedAmountUseCase
import com.naveenapps.expensemanager.core.model.AccountUiModel
import com.naveenapps.expensemanager.core.model.toAccountUiModel
import com.naveenapps.expensemanager.core.navigation.AppComposeNavigator
import com.naveenapps.expensemanager.core.navigation.ExpenseManagerScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    getAllAccountsUseCase: GetAllAccountsUseCase,
    getCurrencyUseCase: GetCurrencyUseCase,
    private val getFormattedAmountUseCase: GetFormattedAmountUseCase,
    private val appComposeNavigator: AppComposeNavigator,
) : ViewModel() {

    private val _accounts = MutableStateFlow<UiState<List<AccountUiModel>>>(UiState.Loading)
    val accounts = _accounts.asStateFlow()

    init {
        combine(
            getCurrencyUseCase.invoke(),
            getAllAccountsUseCase.invoke(),
        ) { currency, accounts ->
            _accounts.value = if (accounts.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(
                    accounts.map {
                        it.toAccountUiModel(
                            getFormattedAmountUseCase.invoke(
                                it.amount,
                                currency,
                            ),
                        )
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    fun openCreateScreen(accountId: String?) {
        appComposeNavigator.navigate(
            ExpenseManagerScreens.AccountCreate.createRoute(accountId ?: ""),
        )
    }

    fun closePage() {
        appComposeNavigator.popBackStack()
    }
}
