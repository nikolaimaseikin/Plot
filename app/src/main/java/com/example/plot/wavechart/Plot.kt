package com.example.plot.wavechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Plot(pointList: List<Float>, modifier: Modifier){
    Canvas(modifier = Modifier.fillMaxSize()){
        val canvasWidth = size.width
        val canvasHeight = size.height
        val separateValue = pointList.size / canvasWidth
        val plotData: List<Set<Float>> = List(size = canvasWidth.toInt()){
            setOf(
                pointList.subList(
                    it * separateValue.toInt(),  it * separateValue.toInt() + separateValue.toInt())
                    .min(),
                pointList.subList(
                    it * separateValue.toInt(),  it * separateValue.toInt() + separateValue.toInt())
                    .max(),
            )
        }
        drawLine(
            start = Offset(x = 0.dp.toPx(), y = (canvasHeight / 2) ),
            end = Offset(x = canvasWidth.dp.toPx(), y = (canvasHeight / 2)),
            color = Color.Green,
            strokeWidth = 1.dp.toPx() // instead of 5.dp.toPx() , you can also pass 5f
        )
        for(i in 0..canvasWidth.toInt() - 1){
            drawLine(
                start = Offset(x = i.dp.toPx(), y = (canvasHeight / 2) - plotData[i].elementAt(1) * canvasHeight / 3),
                end = Offset(x = i.dp.toPx(), y = (canvasHeight / 2) - plotData[i].elementAt(0) * canvasHeight / 3),
                color = Color.Blue,
                strokeWidth = 1.dp.toPx() // instead of 5.dp.toPx() , you can also pass 5f
            )
        }
    }
}