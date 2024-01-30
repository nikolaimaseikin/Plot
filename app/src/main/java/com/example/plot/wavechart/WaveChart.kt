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

//TODO: Рефакторинг
@Composable
fun WaveChart(signal: List<Float>,
              sampleRate: Int,
              modifier: Modifier
){
    var chartHeight by remember { mutableStateOf(0) }
    var chartWidth by remember { mutableStateOf(0) }
    //Получаем размеры окна для построения графика и сохраняем в state
    val onGloballyPositionedModifier = modifier.onGloballyPositioned {
        chartHeight = (it.size.height / Resources.getSystem().displayMetrics.density ).roundToInt()
        chartWidth = (it.size.width / Resources.getSystem().displayMetrics.density).roundToInt()
    }
    //Состояния масштаба и смещения графика для выделения участка сигнала из списка (subList)
    var plotScale by remember { mutableStateOf(1f) }
    var plotOffset by remember { mutableStateOf(Offset.Zero) }
    var subSignalListToPlotting by remember { mutableStateOf(signal) }
    subSignalListToPlotting = getSubSignalList(signal, plotScale, plotOffset)
    //Состояние параметров отображения оси X
    val maxXAxisValue = signal.size * (1 / sampleRate.toFloat())
    val xAxisData = XAxisData(signal = subSignalListToPlotting, sampleRate = sampleRate, steps = 6, offset = 30.dp)
    //Состояние параметров отображения оси Y
    val yAxisData = YAxisData(
        steps = 10,
        minValue = subSignalListToPlotting.min(),
        maxValue = subSignalListToPlotting.max(),
        offset = 30.dp
    )
    Surface(modifier = modifier
        .then(onGloballyPositionedModifier)
       //.border(width = 1.dp, color = Color.Magenta)
    ) {
        Log.d("transform", "Zoom: $plotScale Offset: $plotOffset")
        Row {
            YAxis(axisData = yAxisData,
                modifier = Modifier
                    .width(yAxisData.offset)
                    .height(chartHeight.dp - xAxisData.offset)
                    //.border(width = 1.dp, color = Color.Red)
            )
            Column {
                PlotPoints(
                    signal = subSignalListToPlotting,
                    sampleRate = sampleRate,
                    interpolation = true,
                    drawPoints = false,
                    onZoom = {zoomChange ->
                        plotScale *= zoomChange
                    },
                    onOffset = {offsetChange ->
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

