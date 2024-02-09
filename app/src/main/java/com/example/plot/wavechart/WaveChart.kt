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
//TODO: Объединить разбросанные параметры по классу в датаклассы (поля должны быть объеденины в группы). Например timeScale и timeOffset можно объединить в xAxisState
//TODO: Существует проблема, связанная с отрисовкой большого количкства точек на пиксель(перерисовкой одного и того же пикселя) это приводит к лишней работе и тормозам.
//TODO: Необходимо реализовать механизм проверки, был ли отрисован пиксель или нет.
//TODO: Не отрисовывать точки (currentIndex + pointPerPx) ?
//TODO: Привязать метки осей y к offset. зная цену деления одного пикселя по уровню и расстояние между
// offset и координатой y линии сетки можно определить величину (offset - grid.y) * levelPerPx
//Если точна вышла за границы по Y, то её учитываем, но не отображаем
//Добавить кнопки "вернуться к исходному масштабу", freeze X, freeze Y

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
    //Смещение центра по оси X
    var timeOffset by remember {
        mutableStateOf(0f)
    }
    //Смещение по оси Y
    var levelOffset by remember {
        mutableStateOf(0f)
    }
    var subSignalListToPlotting by remember {
        mutableStateOf(signal)
    }
    var centerIndex by remember {
        mutableStateOf(signal.size / 2f)
    }
    //Чувствительность отклика на scrolling по оси X
    val timeSensitivity = subSignalListToPlotting.size.toFloat() / 1000
    //Чувствительность отклика на scrolling по оси Y
    var levelSensitivity = roundedLevelScale * 0.005f
    //Расчёт значения центрального элемента выборки из массива точек
    centerIndex -= timeOffset * timeSensitivity
    centerIndex = centerIndex
        .coerceAtLeast(windowSize / 2f)
        .coerceAtMost(((signal.size - 1) - windowSize / 2f))
    timeOffset = 0f
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
                    levelScale = roundedLevelScale,
                    levelOffset = levelOffset,
                    onTransform = { zoomChange, offsetChange ->
                        selectAxis(
                            offsetChange,
                            onXAxisSelected = {
                                realTimeScale *= 1 / zoomChange
                                roundedTimeScale = getRoundedScale(realTimeScale)
                            },
                            onYAxisSelected = {
                                realLevelScale *= 1 / zoomChange
                                roundedLevelScale = getRoundedScale(realLevelScale)
                                levelSensitivity = roundedLevelScale * 0.005f
                            }
                        )
                        //Offset изменяется для каждой оси при возникновении события onTransform()
                        timeOffset += offsetChange.x
                        levelOffset += offsetChange.y * levelSensitivity
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
    //TODO: После детектирования события запускаем Watchdog таймер.
    // Он будет сбрасываться после каждого нового последующего события,
    // тем самым фиксируя тип события по одной из осей. Если же событий пользователяне поступало некоторое время,
    // то таймер сбрасывает фиксированное состояние и при  последующих попытках pointerInput заново определяется тип оси
    if (offset.x.absoluteValue >= offset.y.absoluteValue){
        onXAxisSelected()
    }
    else{
        onYAxisSelected()
    }
}



