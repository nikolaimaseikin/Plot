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
import kotlin.math.roundToInt

//TODO: Рефакторинг. Добавить слой ViewModel и настроить взаимодействие через event
@Composable
fun WaveChart(signal: List<Float>,
              sampleRate: Int,
              modifier: Modifier
){
    var chartHeight by remember { mutableStateOf(0) }
    var chartWidth by remember { mutableStateOf(0) }
    val onGloballyPositionedModifier = modifier.onGloballyPositioned {
        chartHeight = (it.size.height / Resources.getSystem().displayMetrics.density ).roundToInt()
        chartWidth = (it.size.width / Resources.getSystem().displayMetrics.density).roundToInt()
    }
    //Масштаб и смещение графика для выделения участка сигнала из исходного списка точек для отображения
    var plotScale by remember {
        mutableStateOf(1f)
    }
    var plotOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var subSignalListToPlotting by remember {
        mutableStateOf(signal)
    }
    var centerIndex by remember {
        mutableStateOf(signal.size / 2f)
    }
    val windowSize = (signal.size / plotScale).coerceAtMost(signal.size.toFloat())
    centerIndex -= plotOffset.x * (subSignalListToPlotting.size.toFloat() / 1000)
    centerIndex = centerIndex
        .coerceAtLeast(windowSize / 2f)
        .coerceAtMost(((signal.size - 1) - windowSize / 2f))
    plotOffset = Offset.Zero
    val leftIndex = (centerIndex - (windowSize / 2f))
        .toInt()
        .coerceAtLeast(0)
        .coerceAtMost(signal.size - 1)
    val rightIndex = ((centerIndex + (windowSize / 2f)).roundToInt())
        .coerceAtLeast(0)
        .coerceAtMost(signal.size)
    Log.d("SubIndex", "Center: $centerIndex PlotScale: $plotScale WindowSize $windowSize Left: $leftIndex Right: $rightIndex")
    subSignalListToPlotting = if(signal.isNotEmpty()) signal.subList(leftIndex, rightIndex) else listOf()
    //Состояние параметров отображения оси X
    val xAxisData = XAxisData(
        signal = subSignalListToPlotting,
        startIndex = leftIndex,
        sampleRate = sampleRate,
        steps = 6,
        offset = 30.dp
    )
    //Состояние параметров отображения оси Y
    val yAxisData = YAxisData(
        steps = 10,
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
                    onTransform = {zoomChange, offsetChange ->
                        plotScale = (plotScale * zoomChange).coerceAtLeast(1f)
                        plotOffset += offsetChange
                    },
                    modifier = Modifier
                        .height(chartHeight.dp - xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                )
                XAxis(axisData = xAxisData,
                    modifier = Modifier
                        .height(xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                        //.border(width = 1.dp, color = Color.Red)
                )
            }
        }
    }
}

