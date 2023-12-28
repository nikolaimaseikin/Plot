package com.example.plot.chart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Plot(pointList: List<Float>){
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
        for(i in 0..canvasWidth.toInt() - 1){
            drawLine(
                start = Offset(x = i.dp.toPx(), y = (canvasHeight / 2) - plotData[i].elementAt(1) * 200),
                end = Offset(x = i.dp.toPx(), y = (canvasHeight / 2) - plotData[i].elementAt(0) * 200),
                color = Color.Blue,
                strokeWidth = 1.dp.toPx() // instead of 5.dp.toPx() , you can also pass 5f
            )
        }
    }
}