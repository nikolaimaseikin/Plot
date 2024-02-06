package com.example.plot.wavechart

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.dp


//TODO: Обработать граничные случаи. Что, если в массиве нет элементов и пр...
@Composable
fun Plotter(
    signal: List<Float>,
    sampleRate: Int,
    interpolation: Boolean,
    drawPoints: Boolean,
    onTransform: (zoomChange: Float, offsetChange: Offset) -> Unit,
    modifier: Modifier
){
    var zoom by remember { mutableStateOf(1f) }
    Canvas(modifier = modifier
        .transformable(state = rememberTransformableState { zoomChange, panChange, _ ->
            onTransform(zoomChange, panChange)
        },
            lockRotationOnZoomPan = true)
    ){
        val canvasWidth = size.width
        val canvasHeight = size.height
        val maxValue = if(signal.isNotEmpty()) signal.max() else 0f
        val scalingFactor = if(signal.isEmpty()) 0f else (signal.max() - signal.min()) / canvasHeight
        val pointsPerPx = (signal.size / canvasWidth)
        val samplingPeriod: Float = 1 / sampleRate.toFloat()
        val deltaTimePerPx: Float = pointsPerPx * samplingPeriod

        for(index in signal.indices){
            if(drawPoints){
                //Отображение точки данных
                drawCircle(
                    color = Color.Blue,
                    radius = 2f,
                    center = Offset(
                        x = (index * samplingPeriod) / deltaTimePerPx,
                        y = (maxValue - signal[index]) / scalingFactor
                    )
                )
            }
            if(interpolation){
                //Отображение прямой линии, соединяющей 2 соседние точки
                if(index < signal.size - 1){
                    drawLine(
                        start = Offset(
                            x = (index * samplingPeriod) / deltaTimePerPx,
                            y = (maxValue - signal[index]) / scalingFactor
                        ),
                        end = Offset(
                            x = ((index+1) * samplingPeriod) / deltaTimePerPx,
                            y = (maxValue - signal[index + 1]) / scalingFactor
                        ),
                        color = Color.Blue,
                        strokeWidth = 2f
                    )
                }
            }
        }
        //Отображение линии 0 амплитуды
        drawLine(
            start = Offset(x = 0f , y = (canvasHeight / 2) ),
            end = Offset(x = canvasWidth, y = (canvasHeight / 2)),
            color = Color.Green,
            strokeWidth = 1.dp.toPx()
        )
    }
}