package com.example.plot.wavechart.axis


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalTextApi::class)
@Composable
fun XAxis(modifier: Modifier){
    val textMeasurer = rememberTextMeasurer()
    Row(modifier = modifier){
        Canvas(modifier = modifier
            .fillMaxWidth()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawLine(
                start = Offset(x = 0.dp.toPx(), y = 0f ),
                end = Offset(x = canvasWidth.dp.toPx(), y = 0f ),
                color = Color.Black,
                strokeWidth = 2.dp.toPx() /* instead of 5.dp.toPx(), you can also pass 5f) */
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "123",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black
                ),
                topLeft = Offset(
                    x = center.x,
                    y = center.y
                )
            )
        }
    }
}