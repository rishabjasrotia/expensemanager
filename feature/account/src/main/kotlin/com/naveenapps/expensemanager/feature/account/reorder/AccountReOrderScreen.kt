package com.naveenapps.expensemanager.feature.account.reorder

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naveenapps.expensemanager.core.designsystem.components.dragGestureHandler
import com.naveenapps.expensemanager.core.designsystem.components.rememberDragDropListState
import com.naveenapps.expensemanager.core.designsystem.ui.components.IconAndBackgroundView
import com.naveenapps.expensemanager.core.designsystem.ui.components.TopNavigationBar
import com.naveenapps.expensemanager.core.designsystem.ui.utils.ItemSpecModifier
import com.naveenapps.expensemanager.core.model.Account
import com.naveenapps.expensemanager.feature.account.R
import kotlinx.coroutines.Job

@Composable
fun AccountReOrderScreen(
    viewModel: AccountReOrderViewModel = hiltViewModel(),
) {
    AccountReOrderScaffoldView(
        accounts = viewModel.accounts,
        showActionButton = viewModel.showActionButton,
        backPress = viewModel::closePage,
        saveChanges = viewModel::saveChanges,
        onMove = viewModel::swap,
    )
}

@Composable
private fun AccountReOrderScaffoldView(
    accounts: List<Account>,
    showActionButton: Boolean,
    onMove: (Int, Int) -> Unit,
    saveChanges: () -> Unit,
    backPress: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopNavigationBar(
                title = stringResource(R.string.accounts_re_order),
                onClick = backPress,
            )
        },
        floatingActionButton = {
            if (showActionButton) {
                FloatingActionButton(onClick = saveChanges) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "",
                    )
                }
            }
        },
    ) { innerPadding ->
        ReOrderContent(
            accounts = accounts,
            onMove = onMove,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun ReOrderContent(
    accounts: List<Account>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val overscrollJob = remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)

    LazyColumn(
        modifier = modifier
            .dragGestureHandler(coroutineScope, dragDropListState, overscrollJob),
        state = dragDropListState.getLazyListState(),
    ) {
        itemsIndexed(accounts) { index, account ->
            val displacementOffset =
                if (index == dragDropListState.getCurrentIndexOfDraggedListItem()) {
                    dragDropListState.elementDisplacement.takeIf { it != 0f }
                } else {
                    null
                }
            AccountReOrderItem(
                modifier = Modifier
                    .then(ItemSpecModifier)
                    .graphicsLayer {
                        translationY = displacementOffset ?: 0f
                    },
                name = account.name,
                icon = account.storedIcon.name,
                iconBackgroundColor = account.storedIcon.backgroundColor,
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
            )
        }
    }
}

@Composable
fun AccountReOrderItem(
    name: String,
    icon: String,
    iconBackgroundColor: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconAndBackgroundView(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            icon = icon,
            iconBackgroundColor = iconBackgroundColor,
            name = name,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.CenterVertically),
            text = name,
            style = MaterialTheme.typography.bodyLarge,
        )
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            imageVector = Icons.Outlined.Reorder,
            contentDescription = null,
        )
    }
}
