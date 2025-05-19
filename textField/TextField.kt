package com.solutions.billnest.ui.composables.textField

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solutions.billnest.R
import com.solutions.billnest.util.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * @Created by akash on 21-11-2024.
 * Know more about author at https://akash.cloudemy.in
 */
@Composable
fun TextFieldWithLabel(
    textState: TextFieldValue,
    placeHolder: String,
    label: String,
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
    onValueChanged: (TextFieldValue) -> Unit = {}
) {
    Column(modifier = modifier.wrapContentHeight()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 5.dp, top = 16.dp)
        )
        BasicIconTextField(
            placeHolder = placeHolder,
            leadingIcon = leadingIcon,
            leadingIconId = leadingIconId,
            textState = textState,
            supportingText = supportingText,
            prefix = prefix,
            supportingTextColor = supportingTextColor,
            isError = isError,
            suffix = suffix,
            keyboardType = keyboardType,
            visualTransformation = visualTransformation,
            onValueChanged = onValueChanged
        )
    }
}

@Composable
fun BasicIconTextField(
    textState: TextFieldValue,
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
    onValueChanged: (TextFieldValue) -> Unit = {}
) {

    var textTransformation by remember(visualTransformation) { mutableStateOf(visualTransformation) }

    OutlinedTextField(modifier = modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50)),
        value = textState,
        singleLine = true,
        supportingText = supportingText?.let {
            {
                Text(supportingText,
                    color = supportingTextColor
                        ?: (MaterialTheme.colorScheme.primaryContainer.takeIf { isError == false }
                            ?: MaterialTheme.colorScheme.error))
            }
        },
        isError = isError ?: (supportingText != null),
        prefix = prefix?.let { { Text(prefix, color = MaterialTheme.colorScheme.outline) } },
        onValueChange = onValueChanged,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        placeholder = {
            Text(
                text = placeHolder,
                color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = .45f)
            )
        },
        leadingIcon = (leadingIconId != null || leadingIcon != null).takeIf { it }?.let {
            {
                Row(modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        if (leadingIcon != null) Image(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .size(18.dp),
                            imageVector = leadingIcon,  // material icon
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            contentDescription = "custom_text_field"
                        )
                        if (leadingIconId != null) Image(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .size(18.dp),
                            painter = painterResource(leadingIconId),  // material icon
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            contentDescription = "custom_text_field"
                        )
                        Canvas(
                            modifier = Modifier.height(24.dp)
                        ) {
                            // Allows you to draw a line between two points (p1 & p2) on the canvas.
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.0F
                            )
                        }
                    })
            }
        },
        trailingIcon = if (visualTransformation == PasswordVisualTransformation()) {
            {
                IconButton(onClick = {
                    textTransformation =
                        if (textTransformation == PasswordVisualTransformation()) VisualTransformation.None
                        else PasswordVisualTransformation()
                }) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(if (textTransformation == PasswordVisualTransformation()) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),  // material icon
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = "custom_text_field"
                    )
                }
            }
        } else suffix,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = RoundedCornerShape(50),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
        visualTransformation = textTransformation)
}

@Composable
fun TextFieldWithLabel(
    value: String,
    placeHolder: String,
    label: String,
    leadingIcon: ImageVector? = null,
    @DrawableRes leadingIconId: Int? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    prefix: String? = null,
    readOnly: Boolean? = null,
    capitalization: KeyboardCapitalization? = null,
    supportingTextColor: Color? = null,
    isError: Boolean? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChanged: (String) -> Unit = {}
) {
    Column(modifier = modifier.wrapContentHeight()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 5.dp, top = 16.dp)
        )
        BasicIconTextField(
            placeHolder = placeHolder,
            leadingIcon = leadingIcon,
            leadingIconId = leadingIconId,
            value = value,
            readOnly = readOnly,
            supportingText = supportingText,
            prefix = prefix,
            supportingTextColor = supportingTextColor,
            isError = isError,
            capitalization = capitalization,
            suffix = suffix,
            keyboardType = keyboardType,
            visualTransformation = visualTransformation,
            onValueChanged = onValueChanged
        )
    }
}

@Composable
fun BasicIconTextField(
    value: String,
    placeHolder: String,
    leadingIcon: ImageVector? = null,
    @DrawableRes leadingIconId: Int? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    prefix: String? = null,
    readOnly: Boolean? = null,
    supportingTextColor: Color? = null,
    isError: Boolean? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChanged: (String) -> Unit = {}
) {

    var textTransformation by remember(visualTransformation) { mutableStateOf(visualTransformation) }

    OutlinedTextField(modifier = modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50)),
        value = value,
        singleLine = true,
        readOnly = readOnly ?: false,
        supportingText = supportingText?.let {
            {
                Text(supportingText,
                    color = supportingTextColor
                        ?: (MaterialTheme.colorScheme.primaryContainer.takeIf { isError == false }
                            ?: MaterialTheme.colorScheme.error))
            }
        },
        isError = isError ?: (supportingText != null),
        prefix = prefix?.let { { Text(prefix, color = MaterialTheme.colorScheme.outline) } },
        onValueChange = onValueChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization ?: KeyboardCapitalization.Sentences
        ),
        placeholder = {
            Text(
                text = placeHolder,
                color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = .45f)
            )
        },
        leadingIcon = (leadingIconId != null || leadingIcon != null).takeIf { it }?.let {
            {
                Row(modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        if (leadingIcon != null) Image(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .size(18.dp),
                            imageVector = leadingIcon,  // material icon
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            contentDescription = "custom_text_field"
                        )
                        if (leadingIconId != null) Image(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .size(18.dp),
                            painter = painterResource(leadingIconId),  // material icon
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            contentDescription = "custom_text_field"
                        )
                        Canvas(
                            modifier = Modifier.height(24.dp)
                        ) {
                            // Allows you to draw a line between two points (p1 & p2) on the canvas.
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.0F
                            )
                        }
                    })
            }
        },
        trailingIcon = if (visualTransformation == PasswordVisualTransformation()) {
            {
                IconButton(onClick = {
                    textTransformation =
                        if (textTransformation == PasswordVisualTransformation()) VisualTransformation.None
                        else PasswordVisualTransformation()
                }) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(if (textTransformation == PasswordVisualTransformation()) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),  // material icon
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = "custom_text_field"
                    )
                }
            }
        } else suffix,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledTextColor = LocalTextStyle.current.color,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        shape = RoundedCornerShape(50),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
        visualTransformation = textTransformation)
}


@Composable
fun DisabledTextField(
    modifier: Modifier = Modifier,
    selectedItem: String? = null,
    error: String? = null,
    placeholder: String,
    prefixIcon: ImageVector? = null,
    suffixIcon: ImageVector? = null,
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null
) {
    OutlinedTextField(value = selectedItem ?: "",
        onValueChange = { },
        readOnly = true,
        enabled = false,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50)),
        singleLine = true,
        placeholder = { Text(text = placeholder) },
        leadingIcon = prefix ?: prefixIcon?.let {
            {
                Row(modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .size(18.dp),
                            imageVector = prefixIcon,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "dropdown_icon"
                        )
                        Canvas(modifier = Modifier.height(24.dp)) {
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.0F
                            )
                        }
                    })
            }
        },
        trailingIcon = suffix ?: suffixIcon?.let {
            {
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .size(18.dp),
                    imageVector = suffixIcon,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "dropdown_icon"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant.takeIf { error == null }
                ?: MaterialTheme.colorScheme.error,
            disabledTextColor = LocalTextStyle.current.color,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = RoundedCornerShape(32.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp))
}


@Composable
fun <T> IconDropdown(
    modifier: Modifier = Modifier,
    selectedItem: String?,
    label: String? = null,
    items: List<T>,
    placeholder: String,
    error: String? = null,
    prefixIcon: ImageVector? = null,
    dropdownPadding: Int = 32,
    getDisplayTitle: ((T) -> String?)? = null,
    onItemSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = modifier) {
        label?.let {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 5.dp, top = 16.dp)
            )
        }
        DisabledTextField(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable {
                    if (items.isEmpty()) {
                        context.showToast("No items found")
                    } else {
                        expanded = true
                    }
                },
            selectedItem = selectedItem,
            error = error,
            prefixIcon = prefixIcon,
            suffixIcon = Icons.Default.KeyboardArrowDown,
            placeholder = placeholder,
        )
        error?.let {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 5.dp, start = 16.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(LocalConfiguration.current.screenWidthDp.dp - dropdownPadding.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            items.forEach { item ->
                DropdownMenuItem(modifier = Modifier,
                    text = { Text(text = getDisplayTitle?.invoke(item) ?: item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    })
            }
        }
    }

}

@Composable
fun IconDropdown(
    selectedItem: String?,
    label: String? = null,
    placeholder: String,
    prefixIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    error: String? = null,
    onTap: (() -> Unit),
) {
    Column(modifier = modifier.wrapContentHeight()) {
        label?.let {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 5.dp, top = 16.dp)
            )
        }
        DisabledTextField(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable {
                    onTap.invoke()
                },
            error = error,
            selectedItem = selectedItem,
            placeholder = placeholder,
            prefixIcon = prefixIcon,
            suffixIcon = Icons.Default.KeyboardArrowDown,
        )
        error?.let {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 5.dp, start = 16.dp)
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogField(
    label: String,
    modifier: Modifier = Modifier,
    selectedDate: String? = null,
    placeholder: String? = null,
    maxDate: Date? = null,
    minDate: Date? = null,
    error: String? = null,
    format: String = "dd MMM yyyy",
    onDateSelected: ((String) -> Unit)
) {
    val sdf = remember { SimpleDateFormat(format, Locale.getDefault()) }
    val initialSelectedDateMillis = selectedDate?.let {
        sdf.parse(it)?.let { date ->
            // Shift parsed date to noon (12:00 PM) to avoid timezone issues
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return if (maxDate != null && minDate != null) utcTimeMillis <= maxDate.time && minDate.time <= utcTimeMillis
                    else if (maxDate != null) utcTimeMillis <= maxDate.time
                    else if (minDate != null) minDate.time <= utcTimeMillis
                    else true
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val minYear = minDate?.let {
                        Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
                    }
                    val maxYear = maxDate?.let {
                        Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
                    }

                    return when {
                        minYear != null && maxYear != null -> year in minYear..maxYear
                        minYear != null -> year >= minYear
                        maxYear != null -> year <= maxYear
                        else -> true
                    }
                }
            })
    var showDatePicker by remember { mutableStateOf(false) }
    IconDropdown(
        selectedItem = selectedDate,
        label = label,
        onTap = {
            showDatePicker = true
        },
        placeholder = placeholder ?: label,
        prefixIcon = Icons.Default.DateRange,
        error = error,
        modifier = modifier
    )

    if (showDatePicker)
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
        confirmButton = {
            TextButton(onClick = {
                showDatePicker = false
                val date = datePickerState.selectedDateMillis?.let { Date(it) }
                date?.let { onDateSelected.invoke(sdf.format(it)) }
            }) { Text("Ok") }
        },
        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }) {
        DatePicker(state = datePickerState)
    }
}