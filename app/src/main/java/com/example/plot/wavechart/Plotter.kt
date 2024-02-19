package com.example.plot.wavechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.plot.cursor.CursorMode
import com.example.plot.wavechart.cursor.CursorData
import kotlin.math.roundToInt

@OptIn(ExperimentalTextApi::class)
@Composable
fun Plotter(
    signal: List<Float>,
    startIndex: Int,
    sampleRate: Int,
    drawZeroLevel: Boolean,
    interpolation: Boolean,
    drawPoints: Boolean,
    drawXAxisGrid: Boolean,
    drawYAxisGrid: Boolean,
    numberOfXAxisGridSteps: Int,
    numberOfYAxisGridSteps: Int,
    timeScale: Float,
    levelScale: Float,
    levelOffset: Float,
    startCursor: CursorData,
    endCursor: CursorData,
    onTransform: (zoomChange: Float, offsetChange: Offset) -> Unit,
    getSize: (width: IntSize) -> Unit,
    modifier: Modifier
){
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier
        .clipToBounds()
        .onGloballyPositioned {
            getSize(it.size)
        }
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
            //Отрисовка точек данных с учётом интерполяции промежутков между точками прямой линией
            if(interpolation){
                if(index < signal.size - 1){
                    drawLine(
                        start = Offset(
                            x = (index * samplingPeriod) / deltaTimePerPx,
                            y = (zeroLevel - signal[index]) / deltaLevelPerPx
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
        //Рисование линии 0 данных
        if(drawZeroLevel){
            drawLine(
                start = Offset(
                    x = 0f,
                    y = zeroLevel / deltaLevelPerPx
                ),
                end = Offset(
                    x = canvasWidth,
                    y = zeroLevel / deltaLevelPerPx
                ),
                color = Color.Green,
                strokeWidth = 2f
            )
        }
        //Отрисовка линий курсоров
        if(startCursor.mode == CursorMode.VISIBLE || startCursor.mode == CursorMode.MOVE){
            drawLine(
                start = Offset(
                    x = startCursor.cursorPosition,
                    y = 0f
                ),
                end = Offset(
                    x = startCursor.cursorPosition,
                    y = canvasHeight
                ),
                color = Color.Red,
                strokeWidth = 2f
            )
        }
        if(endCursor.mode == CursorMode.VISIBLE || endCursor.mode == CursorMode.MOVE){
            drawLine(
                start = Offset(
                    x = endCursor.cursorPosition,
                    y = 0f
                ),
                end = Offset(
                    x = endCursor.cursorPosition,
                    y = canvasHeight
                ),
                color = Color.Magenta,
                strokeWidth = 2f
            )
        }
        //Отрисовка области данных курсоров
        if(startCursor.mode != CursorMode.INVISIBLE || endCursor.mode != CursorMode.INVISIBLE){
            val startCursorTime = (startIndex * samplingPeriod) +
                    (startCursor.cursorPosition * deltaTimePerPx)
            val endCursorTime = (startIndex * samplingPeriod) +
                    (endCursor.cursorPosition * deltaTimePerPx)
            val deltaTime = endCursorTime - startCursorTime
            val frequency = 1 / deltaTime
            val startCursorLevel = signal[((startCursor.cursorPosition * deltaTimePerPx) / samplingPeriod)
                .roundToInt()
                .coerceAtMost(signal.size)]
            val endCursorLevel = signal[((endCursor.cursorPosition * deltaTimePerPx) / samplingPeriod)
                .roundToInt()
                .coerceAtMost(signal.size)]
            drawText(
                textMeasurer = textMeasurer,
                text = String.format(
                        "A->T=%.4f\n" +
                        "A->V=%.4f\n" +
                        "B->T=%.4f\n" +
                        "B->V=%.4f\n" +
                        "Δt=%.4f\n" +
                        "1/Δt=%.2f\n" +
                        "ΔV=%.4f",
                    startCursorTime,
                    startCursorLevel,
                    endCursorTime,
                    endCursorLevel,
                    deltaTime,
                    frequency,
                    endCursorLevel - startCursorLevel
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                ),
                topLeft = Offset(
                    x = canvasWidth - 350f,
                    y = 10f
                )
            )
        }
    }
}