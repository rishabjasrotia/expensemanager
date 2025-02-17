package com.naveenapps.expensemanager.core.designsystem.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.naveenapps.expensemanager.core.common.utils.toDoubleOrNullWithLocale
import com.naveenapps.expensemanager.core.designsystem.ui.utils.UiText

@Composable
fun ClickableTextField(
    label: Int,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isPressed) {
        if (isPressed) {
            onClick.invoke()
            focusManager.clearFocus(force = true)
        }
    }

    OutlinedTextField(
        modifier = modifier,
        interactionSource = interactionSource,
        value = value,
        singleLine = true,
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "",
                )
            }
        } else {
            null
        },
        label = {
            Text(text = stringResource(id = label))
        },
        onValueChange = {},
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun StringTextField(
    value: String,
    errorMessage: UiText?,
    onValueChange: ((String) -> Unit)?,
    label: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    singleLine: Boolean = true,
) {
    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier,
        value = value,
        singleLine = singleLine,
        label = {
            Text(text = stringResource(id = label))
        },
        onValueChange = {
            onValueChange?.invoke(it)
        },
        keyboardOptions = keyboardOptions,
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            { Text(text = errorMessage.asString(context)) }
        } else {
            null
        },
    )
}

@Composable
fun DecimalTextField(
    value: String,
    errorMessage: UiText?,
    onValueChange: ((String) -> Unit)?,
    leadingIconText: String?,
    label: Int,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier,
        value = value,
        singleLine = true,
        leadingIcon = if (leadingIconText != null) {
            {
                Text(
                    text = leadingIconText,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        label = {
            Text(text = stringResource(id = label))
        },
        onValueChange = {
            val formatString = it.toDoubleOrNullWithLocale()
            if (formatString != null) {
                onValueChange?.invoke(formatString.toString())
            } else {
                onValueChange?.invoke(value)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus(force = true)
            },
        ),
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            { Text(text = errorMessage.asString(context)) }
        } else {
            null
        },
    )
}
