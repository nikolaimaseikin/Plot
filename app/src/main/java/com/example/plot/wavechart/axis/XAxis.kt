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
    signal: List<Float>,
    sampleRate: Int,
    axisData: XAxisData, 
    modifier: Modifier = Modifier.height(15.dp)
    /*.border(width = 1.dp, color = Color.Red)*/){
    val textMeasurer = rememberTextMeasurer()
    Row(modifier = modifier){
        Canvas(modifier = modifier) {
            val canvasWidth = size.width
            //Рисование горизонтальной линии оси X
            drawLine(
                start = Offset(x = 0f, y = 0f ),
                end = Offset(x = canvasWidth, y = 0f ),
                color = Color.Black,
                strokeWidth = 3f
            )
        }
    }
}