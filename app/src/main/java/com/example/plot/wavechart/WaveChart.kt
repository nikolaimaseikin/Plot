package com.example.plot.wavechart

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.plot.cursor.CursorMode
import com.example.plot.wavechart.axis.XAxis
import com.example.plot.wavechart.axis.XAxisData
import com.example.plot.wavechart.axis.YAxis
import com.example.plot.wavechart.axis.YAxisData
import com.example.plot.wavechart.cursor.CursorData
import com.example.plot.wavechart.util.checkSignalChange
import com.example.plot.wavechart.util.getRoundedScale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


//TODO: Рефакторинг. Добавить слой ViewModel и настроить взаимодействие через event
//TODO: Объединить разбросанные параметры по классу в датаклассы (поля должны быть объеденины в группы). Например timeScale и timeOffset можно объединить в xAxisState
//TODO: Существует проблема, связанная с отрисовкой большого количкства точек на пиксель(перерисовкой одного и того же пикселя) это приводит к лишней работе и тормозам.
//TODO: Необходимо реализовать механизм проверки, был ли отрисован пиксель или нет.
//TODO: Не отрисовывать точки (currentIndex + pointPerPx) ?
//TODO: Привязать метки осей y к offset. зная цену деления одного пикселя по уровню и расстояние между
// offset и координатой y линии сетки можно определить величину (offset - grid.y) * levelPerPx
//Если точка вышла за границы по Y, то её учитываем, но не отображаем
//Добавить кнопки "вернуться к исходному масштабу", freeze X, freeze Y
//TODO: Реализовать смену размерностей сеток и округление  (с, мс, мкс)...
// Получить текущее значение ращмерности по уровню можно из gtr файла
//TODO: Реализовать курсоры (отслеживание длительности нажатия)

@Composable
fun WaveChart(signal: List<Float>,
              sampleRate: Int,
              xGridSteps: Int,
              yGridSteps: Int,
              reset: Boolean,
              startCursor: CursorMode,
              endCursor: CursorMode,
              onReset: () -> Unit,
              modifier: Modifier
){
    var plotSize by remember {
        mutableStateOf(IntSize(0, 0))
    }
    var startCursorPosition by remember {
        mutableStateOf(0f)
    }
    var endCursorPosition by remember {
        mutableStateOf(0f)
    }
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
    //Проверка изменения входного сигнала
    //После рекомпозиции элемента приводит в исходное состояние масштаб сеток
    checkSignalChange(currentSignal, signal) {
        currentSignal = signal
        realTimeScale = (signal.size.toFloat() / xGridSteps) * samplingPeriod
        roundedTimeScale = getRoundedScale(realTimeScale)
        realLevelScale = if(signal.isNotEmpty()) (signal.max() - signal.min()) / yGridSteps else 0f
        roundedLevelScale = getRoundedScale(realLevelScale)
        levelOffset = 0f
    }
    //Проверка команды возврата в исходное состояние
    if(reset){
        realTimeScale = (signal.size.toFloat() / xGridSteps) * samplingPeriod
        roundedTimeScale = getRoundedScale(realTimeScale)
        realLevelScale = if(signal.isNotEmpty()) (signal.max() - signal.min()) / yGridSteps else 0f
        roundedLevelScale = getRoundedScale(realLevelScale)
        levelOffset = 0f
        onReset()
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
        offset = 30.dp
    )
    var startCursorData = CursorData(startCursor, startCursorPosition)
    var endCursorData = CursorData(endCursor, endCursorPosition)
    Surface(modifier = modifier
        .then(onGloballyPositionedModifier)
       //.border(width = 1.dp, color = Color.Magenta)
    ) {
        Row {
            YAxis(axisData = yAxisData,
                levelScale = roundedLevelScale,
                levelOffset = levelOffset,
                modifier = Modifier
                    .width(yAxisData.offset)
                    .height(chartHeight.dp - xAxisData.offset)
                    //.border(width = 1.dp, color = Color.Red)
            )
            Column {
                Plotter(
                    signal = subSignalListToPlotting,
                    startIndex = leftIndex,
                    sampleRate = sampleRate,
                    drawZeroLevel = true,
                    interpolation = true,
                    drawPoints = false,
                    drawXAxisGrid = true,
                    drawYAxisGrid = true,
                    numberOfXAxisGridSteps = xAxisData.steps,
                    numberOfYAxisGridSteps = yAxisData.steps,
                    timeScale = roundedTimeScale,
                    levelScale = roundedLevelScale,
                    levelOffset = levelOffset,
                    startCursor = startCursorData,
                    endCursor = endCursorData,
                    onTransform = { zoomChange, offsetChange ->
                        selectTransformMode(
                            startCursor,
                            endCursor,
                            onAxisTransform = {
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
                            onCursorTransform = {
                                if(startCursor == CursorMode.MOVE){
                                    startCursorPosition = (startCursorPosition + offsetChange.x)
                                        .coerceAtLeast(0f)
                                        .coerceAtMost(plotSize.width.toFloat())
                                }
                                if(endCursor == CursorMode.MOVE){
                                    endCursorPosition = (endCursorPosition + offsetChange.x)
                                        .coerceAtLeast(0f)
                                        .coerceAtMost(plotSize.width.toFloat())
                                }
                                Log.d("Cursor", "$startCursorPosition $endCursorPosition")
                            }
                        )
                    },
                    getSize = {
                        plotSize = it
                    },
                    modifier = Modifier
                        .height(chartHeight.dp - xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                        //.border(width = 1.dp, color = Color.Red)
                )
                XAxis(
                    timeScale = roundedTimeScale,
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

fun selectTransformMode(
    startCursor: CursorMode,
    endCursor: CursorMode,
    onCursorTransform: () -> Unit,
    onAxisTransform: () -> Unit
){
    if(startCursor == CursorMode.MOVE || endCursor == CursorMode.MOVE){
        onCursorTransform()
    }
    else{
        onAxisTransform()
    }
}



