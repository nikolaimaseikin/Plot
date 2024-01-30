package com.example.plot.wavechart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun Plot(signal: List<Float>,
         onZoom: (zoomChange: Float) -> Unit,
         onOffset: (offsetChange: Offset) -> Unit,
         modifier: Modifier){
    Canvas(modifier = modifier
        //.border(width = 1.dp, color = Color.Red)
        /*.graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offset.x
            translationY = 0f
        } */
        .transformable(state = rememberTransformableState { zoomChange, panChange, _ ->
            onZoom(zoomChange)
            onOffset(panChange)
        },
            lockRotationOnZoomPan = true)
    ){
        val canvasWidth = size.width
        val canvasHeight = size.height
        val k = (signal.max() - signal.min()) / canvasHeight
        //Разбиение массива данных на массив максимумов и минимумов, равных количеству столбцов
        val plotData: MutableList<Set<Float>> = getColumnsList(signal, canvasWidth)
        //Вывод линии 0 амплитуды
        drawLine(
            start = Offset(x = 0.dp.toPx(), y = (canvasHeight / 2) ),
            end = Offset(x = canvasWidth.dp.toPx(), y = (canvasHeight / 2)),
            color = Color.Green,
            strokeWidth = 1.dp.toPx()
        )
        //Отрисовка столбцов в Canvas
        for(i in 0 until plotData.size){
            val yStart = ((canvasHeight / 2) - plotData[i].elementAt(1) / k)
            val yEnd = ((canvasHeight / 2) - plotData[i].elementAt(0)  / k)
            drawLine(
                start = Offset(x = i.dp.toPx(), y = yStart),
                end = Offset(x = i.dp.toPx(), y = yEnd),
                color = Color.Blue,
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

