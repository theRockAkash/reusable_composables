package com.solutions.billnest.ui.composables.textField

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * @Created by akash on 09-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */

class FormFieldState(
    val key: String,
    val validator: () -> String?
) {
    var error: String? by mutableStateOf(null)

    fun validate(): Boolean {
        val errorMessage = validator()
        error = errorMessage
        return errorMessage == null
    }
}

class FormState {
    private val fields = mutableMapOf<String, FormFieldState>()

    fun registerField(field: FormFieldState) {
        fields[field.key] = field
    }

    fun validate(): Boolean {
        return fields.values.map { it.validate() }.all { it }
    }
}