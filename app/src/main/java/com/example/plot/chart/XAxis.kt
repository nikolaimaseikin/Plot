package com.example.plot.chart


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalTextApi::class)
@Composable
fun XAxis(){
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = Modifier
        .fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawLine(
            start = Offset(x = 0.dp.toPx(), y = canvasHeight / 2 ),
            end = Offset(x = canvasWidth.dp.toPx(), y = canvasHeight / 2 ),
            color = Color.Red,
            strokeWidth = 1.dp.toPx() /* instead of 5.dp.toPx() , you can also pass 5f) */
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "123",
            style = TextStyle(
                fontSize = 150.sp,
                color = Color.Black,
                background = Color.Red.copy(alpha = 0.2f)
            ),
            topLeft = Offset(
                x = center.x,
                y = center.y
            )
        )
    }
}