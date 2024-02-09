package com.example.plot.wavechart

import android.util.Log
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

@Composable
fun Plotter(
    signal: List<Float>,
    sampleRate: Int,
    interpolation: Boolean,
    drawPoints: Boolean,
    drawXAxisGrid: Boolean,
    drawYAxisGrid: Boolean,
    numberOfXAxisGridSteps: Int,
    numberOfYAxisGridSteps: Int,
    timeScale: Float,
    levelScale: Float,
    levelOffset: Float,
    onTransform: (zoomChange: Float, offsetChange: Offset) -> Unit,
    modifier: Modifier
){
    Canvas(modifier = modifier
        .transformable(state = rememberTransformableState { zoomChange, panChange, _ ->
            onTransform(zoomChange, panChange)
        },
            lockRotationOnZoomPan = true)
    ){
        val canvasWidth = size.width
        val canvasHeight = size.height
        val samplingPeriod: Float = 1 / sampleRate.toFloat()
        val deltaTimePerPx: Float = timeScale / (canvasWidth / numberOfXAxisGridSteps)
        val deltaLevelPerPx: Float = levelScale / (canvasHeight / numberOfYAxisGridSteps)
        val zeroLevel = ((canvasHeight / 2) * deltaLevelPerPx) + levelOffset

        //Отображение линий сетки по оси X
        if(drawXAxisGrid){
            val pxPerGridStep = canvasWidth / numberOfXAxisGridSteps
            for(i in 0 until numberOfXAxisGridSteps){
                drawLine(
                    start = Offset(x = i * pxPerGridStep, y = 0f ),
                    end = Offset(x = i * pxPerGridStep, y = canvasHeight ),
                    color = Color.Black,
                    alpha = 0.2f,
                    strokeWidth = 2f
                )
            }
        }
        //Отображение линий сетки по оси Y
        if(drawYAxisGrid){
            val pxPerGridStep = canvasHeight / numberOfYAxisGridSteps
            for(i in 0 until numberOfYAxisGridSteps){
                drawLine(
                    start = Offset(x = 0f, y = i * pxPerGridStep ),
                    end = Offset(x = canvasWidth, y = i * pxPerGridStep),
                    color = Color.Black,
                    alpha = 0.2f,
                    strokeWidth = 2f
                )
            }
        }
        //Отображение данных
        for(index in signal.indices){
            if(drawPoints){
                //Отображение точки данных
                drawCircle(
                    color = Color.Blue,
                    radius = 2f,
                    center = Offset(
                        x = (index * samplingPeriod) / deltaTimePerPx,
                        y = (zeroLevel - signal[index]) / deltaLevelPerPx
                    )
                )
            }
            if(interpolation){
                //Отображение прямой линии, соединяющей 2 соседние точки
                if(index < signal.size - 1){
                    drawLine(
                        start = Offset(
                            x = (index * samplingPeriod) / deltaTimePerPx,
                            y = (zeroLevel - signal[index]) /  deltaLevelPerPx
                        ),
                        end = Offset(
                            x = ((index+1) * samplingPeriod) / deltaTimePerPx,
                            y = (zeroLevel - signal[index + 1]) / deltaLevelPerPx
                        ),
                        color = Color.Blue,
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}