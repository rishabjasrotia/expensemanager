package com.naveenapps.expensemanager.feature.account.create

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naveenapps.expensemanager.core.designsystem.components.IconAndColorComponent
import com.naveenapps.expensemanager.core.designsystem.ui.components.AppDialog
import com.naveenapps.expensemanager.core.designsystem.ui.components.DecimalTextField
import com.naveenapps.expensemanager.core.designsystem.ui.components.StringTextField
import com.naveenapps.expensemanager.core.designsystem.ui.components.TopNavigationBarWithDeleteAction
import com.naveenapps.expensemanager.core.designsystem.ui.theme.ExpenseManagerTheme
import com.naveenapps.expensemanager.core.designsystem.ui.utils.UiText
import com.naveenapps.expensemanager.core.model.AccountType
import com.naveenapps.expensemanager.core.model.Amount
import com.naveenapps.expensemanager.feature.account.R

@Composable
fun AccountCreateScreen() {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: AccountCreateViewModel = hiltViewModel()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AppDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmation = {
                viewModel.deleteAccount()
                showDeleteDialog = false
            },
            dialogTitle = stringResource(id = R.string.delete),
            dialogText = stringResource(id = R.string.delete_item_message),
            positiveButtonText = stringResource(id = R.string.delete),
            negativeButtonText = stringResource(id = R.string.cancel),
        )
    }

    val showDelete by viewModel.showDelete.collectAsState(null)

    val accountCreated by viewModel.message.collectAsState(null)
    if (accountCreated != null) {
        LaunchedEffect(key1 = "completed", block = {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.account_create_success),
            )
        })
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopNavigationBarWithDeleteAction(
                title = stringResource(id = R.string.accounts),
                showDelete = showDelete,
            ) {
                if (it == 1) {
                    viewModel.closePage()
                } else {
                    showDeleteDialog = true
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::saveOrUpdateAccount) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "",
                )
            }
        },
    ) { innerPadding ->

        val name by viewModel.name.collectAsState()
        val nameErrorMessage by viewModel.nameErrorMessage.collectAsState()
        val currentBalance by viewModel.currentBalance.collectAsState()
        val currentBalanceErrorMessage by viewModel.currentBalanceErrorMessage.collectAsState()
        val creditLimit by viewModel.creditLimit.collectAsState()
        val creditLimitErrorMessage by viewModel.creditLimitErrorMessage.collectAsState()
        val currencyIcon by viewModel.currencyIcon.collectAsState()
        val colorValue by viewModel.colorValue.collectAsState()
        val iconValue by viewModel.icon.collectAsState()
        val selectedAccountType by viewModel.accountType.collectAsState()
        val availableCreditLimit by viewModel.availableCreditLimit.collectAsState()
        val availableCreditLimitColor by viewModel.availableCreditLimitColor.collectAsState()

        AccountCreateScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            selectedColor = colorValue,
            selectedIcon = iconValue,
            name = name,
            nameErrorMessage = nameErrorMessage,
            currentBalance = currentBalance,
            currentBalanceErrorMessage = currentBalanceErrorMessage,
            creditLimit = creditLimit,
            creditLimitErrorMessage = creditLimitErrorMessage,
            selectedAccountType = selectedAccountType,
            onAccountTypeChange = viewModel::setAccountType,
            currencyIcon = currencyIcon,
            onNameChange = viewModel::setNameChange,
            onCurrentBalanceChange = viewModel::setCurrentBalanceChange,
            onCreditLimitChange = viewModel::setCreditLimitChange,
            availableCreditLimit = availableCreditLimit,
            availableCreditLimitColor = availableCreditLimitColor,
            openColorPicker = viewModel::setColorValue,
            openIconPicker = viewModel::setIcon,
        )
    }
}

@Composable
private fun AccountCreateScreen(
    onAccountTypeChange: ((AccountType) -> Unit),
    modifier: Modifier = Modifier,
    selectedAccountType: AccountType = AccountType.REGULAR,
    name: String = "",
    nameErrorMessage: UiText? = null,
    currentBalance: String = "",
    currentBalanceErrorMessage: UiText? = null,
    currencyIcon: String? = null,
    selectedColor: String = "#000000",
    selectedIcon: String = "account_balance",
    openIconPicker: ((String) -> Unit)? = null,
    openColorPicker: ((Int) -> Unit)? = null,
    onNameChange: ((String) -> Unit)? = null,
    onCurrentBalanceChange: ((String) -> Unit)? = null,
    creditLimit: String = "",
    creditLimitErrorMessage: UiText? = null,
    onCreditLimitChange: ((String) -> Unit)? = null,
    availableCreditLimit: Amount? = null,
    @ColorRes availableCreditLimitColor: Int = com.naveenapps.expensemanager.core.common.R.color.green_500,
) {
    Column(modifier = modifier) {
        AccountTypeSelectionView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            selectedAccountType = selectedAccountType,
            onAccountTypeChange = onAccountTypeChange,
        )

        StringTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .fillMaxWidth(),
            value = name,
            errorMessage = nameErrorMessage,
            onValueChange = onNameChange,
            label = R.string.account_name,
        )

        IconAndColorComponent(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth(),
            selectedColor = selectedColor,
            selectedIcon = selectedIcon,
            onColorSelection = openColorPicker,
            onIconSelection = openIconPicker,
        )

        DecimalTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth(),
            value = currentBalance,
            errorMessage = currentBalanceErrorMessage,
            onValueChange = onCurrentBalanceChange,
            leadingIconText = currencyIcon,
            label = R.string.current_balance,
        )

        if (selectedAccountType == AccountType.CREDIT) {
            DecimalTextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth(),
                value = creditLimit,
                errorMessage = creditLimitErrorMessage,
                onValueChange = onCreditLimitChange,
                leadingIconText = currencyIcon,
                label = R.string.credit_limit,
            )
        }
        if (availableCreditLimit != null) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .background(
                        color = colorResource(id = availableCreditLimitColor).copy(.1f),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.available_balance),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = availableCreditLimit.amountString ?: "",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )
    }
}

@Preview
@Composable
private fun AccountCreateStatePreview() {
    ExpenseManagerTheme {
        AccountCreateScreen(
            onAccountTypeChange = {},
        )
    }
}
