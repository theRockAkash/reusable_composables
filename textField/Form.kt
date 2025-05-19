package com.solutions.billnest.ui.composables.textField

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * @Created by akash on 09-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */
@Composable
fun Form(
    formState: FormState,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(LocalFormState provides formState) {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
    }

}


val LocalFormState = compositionLocalOf<FormState> {
    error("FormState not provided")
}

@Composable
fun rememberFormState(): FormState = remember { FormState() }
