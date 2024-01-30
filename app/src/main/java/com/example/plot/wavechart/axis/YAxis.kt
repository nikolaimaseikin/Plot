package com.example.plot.wavechart.axis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//TODO: Максимум и минимум данных по оси ищем по локальной выборке для рендеринга
@OptIn(ExperimentalTextApi::class)
@Composable
fun YAxis(axisData: YAxisData, modifier: Modifier){
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val axisStepValue = (axisData.maxValue - axisData.minValue) / axisData.steps
        for(i in 0..axisData.steps){
            drawText(
                textMeasurer = textMeasurer,
                text = String.format("%.2f", (axisData.steps - i) * axisStepValue + axisData.minValue),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black
                ),
                topLeft = Offset(
                    x = 0f,
                    y = i * (canvasHeight / axisData.steps)
                )
            )
            drawLine(
                start = Offset(x = canvasWidth - 10f, y = i * (canvasHeight / axisData.steps)),
                end = Offset(x = canvasWidth, y = i * (canvasHeight / axisData.steps)),
                color = Color.Black,
                strokeWidth = 3f
            )
        }
        drawLine(start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 3f
        )
    }
}