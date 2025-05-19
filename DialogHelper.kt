package com.solutions.billnest.util

/**
 * @Created by akash on 20-03-2025.
 * Know more about author at https://akash.cloudemy.in
 */


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.solutions.billnest.R
import com.solutions.billnest.ui.composables.OutlineButton
import com.solutions.billnest.ui.composables.RoundedButton
import com.solutions.billnest.ui.theme.AppTheme


/**
 * @Created by akash on 10-09-2024.
 * Know more about author at https://akash.cloudemy.in
 */
class DialogHelper {

    companion object {
        fun showCustomDialog(
            context: Context,
            lifecycleOwner: LifecycleOwner,
            cancellable: Boolean = true,
            onDismissed: (error: Boolean) -> Unit = {},//error
            content: @Composable (Dialog) -> Unit
        ) {
            kotlin.runCatching {
                // Inflate the custom layout
                val view = LayoutInflater.from(context).inflate(R.layout.premium_dialog, null)
                view.setViewTreeLifecycleOwner(lifecycleOwner)
                val compose: ComposeView = (view.findViewById(R.id.compose_view))
                // Create and show the dialog
                val dialog = Dialog(context)
                dialog.setContentView(view)
                dialog.setCancelable(cancellable)

                dialog.window?.setBackgroundDrawable(
                    GradientDrawable().apply {
                        setColor(Color.TRANSPARENT)
                        cornerRadius =
                            16 * context.resources.displayMetrics.density // Convert dp to px
                    }
                )
                dialog.setCanceledOnTouchOutside(cancellable)
                compose.apply {
                    setViewTreeSavedStateRegistryOwner(context.activity())
                    setViewTreeLifecycleOwner(context.activity())
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        AppTheme { content.invoke(dialog) }

                    }
                }
                dialog.setOnCancelListener {
                    onDismissed.invoke(false)
                }
                dialog.show()
            }.onFailure {
                onDismissed.invoke(true)
            }

        }


    }
}

@Composable
fun ConfirmationDialog(
    message: String,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    positiveButton:String?=null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {

        // Title
        Text(
            text = "Confirmation",
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
        // Notice Text
        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlineButton("Cancel", modifier = Modifier.weight(1f)) { onCancel.invoke() }
            RoundedButton(positiveButton?:"Confirm",  modifier = Modifier.weight(1f)) { onSubmit.invoke() }
        }
    }

}