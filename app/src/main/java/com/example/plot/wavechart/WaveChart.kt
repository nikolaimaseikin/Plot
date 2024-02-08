package com.example.plot.wavechart

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.example.plot.wavechart.axis.XAxis
import com.example.plot.wavechart.axis.XAxisData
import com.example.plot.wavechart.axis.YAxis
import com.example.plot.wavechart.axis.YAxisData
import com.example.plot.wavechart.util.checkSignalChange
import com.example.plot.wavechart.util.getRoundedScale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

//TODO: Есть вариант разделить сигнал и конфигурацию осей
//TODO: Рефакторинг. Добавить слой ViewModel и настроить взаимодействие через event

@Composable
fun WaveChart(signal: List<Float>,
              sampleRate: Int,
              xGridSteps: Int,
              yGridSteps: Int,
              modifier: Modifier
){

    var chartHeight by remember { mutableStateOf(0) }
    var chartWidth by remember { mutableStateOf(0) }
    val onGloballyPositionedModifier = modifier.onGloballyPositioned {
        chartHeight = (it.size.height / Resources.getSystem().displayMetrics.density ).roundToInt()
        chartWidth = (it.size.width / Resources.getSystem().displayMetrics.density).roundToInt()
    }
    val samplingPeriod = 1 / sampleRate.toFloat()
    var currentSignal by remember {
        mutableStateOf(signal)
    }
    //Масштаб сетки по оси X (time grid)
    var realTimeScale by remember {
        mutableStateOf((currentSignal.size.toFloat() / xGridSteps) * samplingPeriod)
    }
    var roundedTimeScale by remember {
        mutableStateOf(getRoundedScale(realTimeScale))
    }
    //Масштаб сетки по оси Y (level grid)
    var realLevelScale by remember {
        mutableStateOf(if(signal.isNotEmpty()) (signal.max() - signal.min()) / yGridSteps else 0f)
    }
    var roundedLevelScale by remember {
        mutableStateOf(getRoundedScale(realLevelScale))
    }
    //Проверка изменения входного сигнала
    //После рекомпозиции элемента приводит в исходное состояние масштаб сеток
    checkSignalChange(currentSignal, signal) {
        currentSignal = signal
        realTimeScale = (signal.size.toFloat() / xGridSteps) * samplingPeriod
        roundedTimeScale = getRoundedScale(realTimeScale)
        realLevelScale = if(signal.isNotEmpty()) (signal.max() - signal.min()) / yGridSteps else 0f
        roundedLevelScale = getRoundedScale(realLevelScale)
    }
    var timeWindowSize = roundedTimeScale * xGridSteps
    var windowSize = (timeWindowSize / samplingPeriod).coerceAtMost(signal.size.toFloat())
    var plotOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var subSignalListToPlotting by remember {
        mutableStateOf(signal)
    }
    var centerIndex by remember {
        mutableStateOf(signal.size / 2f)
    }

    //Чувствительность отклика на scrolling по оси X
    val sensitive = subSignalListToPlotting.size.toFloat() / 1000

    //Расчёт значения центрального элемента выборки из массива точек
    centerIndex -= plotOffset.x * sensitive
    centerIndex = centerIndex
        .coerceAtLeast(windowSize / 2f)
        .coerceAtMost(((signal.size - 1) - windowSize / 2f))
    plotOffset = Offset.Zero
    //Рассчёт индексов окна выборки данных
    val leftIndex = (centerIndex - (windowSize / 2f))
        .toInt()
        .coerceAtLeast(0)
        .coerceAtMost(signal.size - 1)
    val rightIndex = ((centerIndex + (windowSize / 2f)).roundToInt())
        .coerceAtLeast(0)
        .coerceAtMost(signal.size)
    //Обновления списка точек для отображения на графике
    subSignalListToPlotting = if(signal.isNotEmpty()) signal.subList(leftIndex, rightIndex) else listOf()
    //Параметры конфигурации оси X
    val xAxisData = XAxisData(
        startIndex = leftIndex,
        steps = xGridSteps,
        offset = 30.dp
    )
    //Параметры конфигурации оси Y
    val yAxisData = YAxisData(
        steps = yGridSteps,
        minValue = if (subSignalListToPlotting.isNullOrEmpty()) 0f else subSignalListToPlotting.min(),
        maxValue = if (subSignalListToPlotting.isNullOrEmpty()) 0f else subSignalListToPlotting.max(),
        offset = 30.dp
    )
    Surface(modifier = modifier
        .then(onGloballyPositionedModifier)
       //.border(width = 1.dp, color = Color.Magenta)
    ) {
        Row {
            YAxis(axisData = yAxisData,
                modifier = Modifier
                    .width(yAxisData.offset)
                    .height(chartHeight.dp - xAxisData.offset)
                    //.border(width = 1.dp, color = Color.Red)
            )
            Column {
                Plotter(
                    signal = subSignalListToPlotting,
                    sampleRate = sampleRate,
                    interpolation = true,
                    drawPoints = false,
                    drawXAxisGrid = true,
                    drawYAxisGrid = true,
                    numberOfXAxisGridSteps = xAxisData.steps,
                    numberOfYAxisGridSteps = yAxisData.steps,
                    timeScale = roundedTimeScale,
                    onTransform = {zoomChange, offsetChange ->
                        //TODO: Я остановился на реализации фичи стабильного детектирования событий по осям
                        selectAxis(
                            offsetChange,
                            onXAxisSelected = {
                                realTimeScale *= 1 / zoomChange
                                roundedTimeScale = getRoundedScale(realTimeScale)
                                plotOffset += offsetChange //time offset
                            },
                            onYAxisSelected = {
                                realLevelScale *= 1 / zoomChange
                                roundedLevelScale = getRoundedScale(realLevelScale)
                                //TODO: levelOffset by remember {mutableStateOf(0f)}
                            }
                        )
                    },
                    modifier = Modifier
                        .height(chartHeight.dp - xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                )
                XAxis(signal = subSignalListToPlotting,
                    sampleRate = sampleRate,
                    axisData = xAxisData,
                    modifier = Modifier
                        .height(xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                        //.border(width = 1.dp, color = Color.Red)
                )

            }
        }
    }
}

fun selectAxis(
    offset: Offset,
    onXAxisSelected: () -> Unit,
    onYAxisSelected: () -> Unit
){
    if (offset.x.absoluteValue >= offset.y.absoluteValue){
        onXAxisSelected()
    }
    else{
        onYAxisSelected()
    }
}



