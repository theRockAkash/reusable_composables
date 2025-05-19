package com.solutions.billnest.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Created by akash on 19-03-2025.
 * Know more about author at https://akash.cloudemy.in
 */

@Composable
fun TextDivider(text: String, modifier: Modifier = Modifier, bgColor:Color?=null, fontSize: TextUnit=14.sp, fontWeight: FontWeight? = null, textColor: Color?=null) {
    val colorBackground =bgColor?: MaterialTheme.colorScheme.background
    val outlineColor = MaterialTheme.colorScheme.outline
    val textMeasurer= rememberTextMeasurer()
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = textColor?:outlineColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .drawBehind {
                val width = size.width
                val height = size.height / 2
                val strokeWidth = 1.dp.toPx()
                val textWidth = textMeasurer.measure(text, style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)).size.width.toFloat() + 32.dp.toPx()
                // Approximate width of "Or", adjust as needed
                val textHeight = size.height  // Text height to cover the line
                // Draw fading horizontal line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            textColor?:outlineColor,
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = width
                    ),
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = strokeWidth
                )

                // Draw a rectangle to cover the line behind text
                val rectStartX = (width - textWidth) / 2  // Centering the rect
                drawRect(
                    color = colorBackground,
                    topLeft = Offset(rectStartX, 0f),
                    size = Size(textWidth, textHeight) // Cover text area
                )
            }
    )
}