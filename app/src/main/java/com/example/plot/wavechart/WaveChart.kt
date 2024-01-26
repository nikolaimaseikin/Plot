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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    //Получаем размеры окна для построения графика и сохраняем в states
    val onGloballyPositionedModifier = modifier.onGloballyPositioned {
        chartHeight = (it.size.height / Resources.getSystem().displayMetrics.density ).roundToInt()
        chartWidth = (it.size.width / Resources.getSystem().displayMetrics.density).roundToInt()
    }
    val zoom by remember{ mutableStateOf<Float>(1f) }
    //Состояние параметров отображения оси X
    val maxXAxisValue = signal.size * (1 / sampleRate.toFloat())
    val xAxisData = XAxisData(signal = signal, sampleRate = sampleRate, steps = 6, offset = 30.dp)
    //Состояние параметров отображения оси Y
    val yAxisData = YAxisData(steps = 10, minValue = signal.min(), maxValue = signal.max(),offset = 30.dp)


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
                Plot(signal = signal,
                    modifier = Modifier
                        .height(chartHeight.dp - xAxisData.offset)
                        .width(chartWidth.dp - yAxisData.offset)
                        //.border(width = 1.dp, color = Color.Yellow)
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

