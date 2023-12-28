package com.example.plot.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun YAxis(){
    Canvas(modifier = Modifier.fillMaxHeight().width(20.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawLine(start = Offset(x = 0.dp.toPx(), y = 0f),
            end = Offset(x = 0.dp.toPx(), y = canvasHeight),
            color = Color.Red,
            strokeWidth = 1.dp.toPx()
        )
    }
}