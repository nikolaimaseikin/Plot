package com.example.plot.wavechart.axis


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@OptIn(ExperimentalTextApi::class)
@Composable
fun XAxis(
    axisData: XAxisData, 
    modifier: Modifier = Modifier.height(15.dp)
    /*.border(width = 1.dp, color = Color.Red)*/){
    val textMeasurer = rememberTextMeasurer()
    Row(modifier = modifier){
        Canvas(modifier = modifier) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val period = 1f / axisData.sampleRate
            val startTime = axisData.startIndex * period
            //Расчёт количества точек в выборке
            val deltaTimePerPx = (axisData.signal.size / canvasWidth) * period
            drawLine(
                start = Offset(x = 0f, y = 0f ),
                end = Offset(x = canvasWidth, y = 0f ),
                color = Color.Black,
                strokeWidth = 3f
            )
            for(i in 0 until axisData.steps){
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format("%.3f",  startTime + i * (canvasWidth / axisData.steps) * deltaTimePerPx),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black
                    ),
                    topLeft = Offset(
                        x = i * (canvasWidth / axisData.steps),
                        y = 0f
                    )
                )
                drawLine(
                    start = Offset(x = i * (canvasWidth / axisData.steps), y = 0f ),
                    end = Offset(x = i * (canvasWidth / axisData.steps), y = 10f ),
                    color = Color.Black,
                    strokeWidth = 3f
                )
            }
        }
    }
}