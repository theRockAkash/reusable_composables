package com.solutions.billnest.ui.composables.textField

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * @Created by akash on 09-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */

@Composable
fun FormTextField(
    label: String,
    placeHolder: String,
    key: String? = null,
    value: String = "",
    validator: () -> String? = { null },
    capitalization: KeyboardCapitalization? = null,
    leadingIcon: ImageVector? = null,
    @DrawableRes leadingIconId: Int? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    prefix: String? = null,
    supportingTextColor: Color? = null,
    isError: Boolean? = null,
    readOnly: Boolean? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChanged: (String) -> Unit = {}

) {
    val formState = LocalFormState.current
    val fieldState = remember { FormFieldState(key ?: placeHolder, validator) }

    LaunchedEffect(fieldState) {
        formState.registerField(fieldState)
    }

    TextFieldWithLabel(
        modifier = modifier,
        label = label,
        placeHolder = placeHolder,
        leadingIcon = leadingIcon,
        leadingIconId = leadingIconId,
        value = value,
        readOnly = readOnly,
        supportingText = fieldState.error ?: supportingText,
        prefix = prefix,
        supportingTextColor = supportingTextColor,
        isError = (isError.takeIf { fieldState.error == null }) ?: (fieldState.error != null),
        suffix = suffix,
        keyboardType = keyboardType,
        capitalization = capitalization,
        visualTransformation = visualTransformation,
        onValueChanged = {
            fieldState.error = null // clear error while typing
            onValueChanged.invoke(it)
        })
}

@Composable
fun FormDropDownField(
    placeHolder: String,
    selectedItem: String? = null,
    label: String? = null,
    key: String? = null,
    validator: () -> String? = { null },
    prefixIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit),
) {
    val formState = LocalFormState.current
    val fieldState = remember { FormFieldState(key ?: placeHolder, validator) }

    LaunchedEffect(fieldState) {
        formState.registerField(fieldState)
    }
    IconDropdown(
        modifier = modifier,
        label = label,
        placeholder = placeHolder,
        prefixIcon = prefixIcon,
        selectedItem = selectedItem,
        error = fieldState.error,
        onTap = {
            fieldState.error = null // clear error while typing
            onTap.invoke()
        })
}

@Composable
fun <T> FormDropDownField(
    key: String? = null,
    validator: () -> String? = { null },
    modifier: Modifier = Modifier,
    selectedItem: String?,
    label: String? = null,
    items: List<T>,
    placeHolder: String,
    error: String? = null,
    prefixIcon: ImageVector? = null,
    dropdownPadding: Int = 32,
    getDisplayTitle: ((T) -> String?)?=null,
    onItemSelected: (T) -> Unit,
) {
    val formState = LocalFormState.current
    val fieldState = remember { FormFieldState(key ?: placeHolder, validator) }

    LaunchedEffect(fieldState) {
        formState.registerField(fieldState)
    }
    IconDropdown(
        modifier = modifier,
        selectedItem = selectedItem,
        label = label,
        items = items,
        placeholder = placeHolder,
        error = error,
        prefixIcon = prefixIcon,
        dropdownPadding = dropdownPadding,
        getDisplayTitle = getDisplayTitle,
        onItemSelected = onItemSelected,
    )
}

@Composable
fun FormTextField(
    key: String? = null,
    value: String,
    validator: () -> String? = { null },
    placeHolder: String,
    leadingIcon: ImageVector? = null,
    @DrawableRes leadingIconId: Int? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    prefix: String? = null,
    supportingTextColor: Color? = null,
    isError: Boolean? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChanged: (String) -> Unit = {}
) {
    val formState = LocalFormState.current
    val fieldState = remember { FormFieldState(key ?: placeHolder, validator) }

    LaunchedEffect(fieldState) {
        formState.registerField(fieldState)
    }

    BasicIconTextField(
        modifier = modifier,
        placeHolder = placeHolder,
        leadingIcon = leadingIcon,
        leadingIconId = leadingIconId,
        value = value,
        supportingText = fieldState.error ?: supportingText,
        prefix = prefix,
        supportingTextColor = supportingTextColor,
        isError = (isError.takeIf { fieldState.error == null }) ?: (fieldState.error != null),
        suffix = suffix,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
        onValueChanged = {
            fieldState.error = null // clear error while typing
            onValueChanged.invoke(it)
        })
}
