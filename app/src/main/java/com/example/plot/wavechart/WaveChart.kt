package com.example.plot.wavechart

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import com.example.plot.wavechart.axis.XAxis
import com.example.plot.wavechart.axis.YAxis

@Composable
fun WaveChart(pointList: List<Float>, modifier: Modifier){
    Surface(modifier = modifier/*.border(width = 2.dp, color = Color.Magenta)*/) {
        Row {
            YAxis()
            Column {
                Plot(pointList = pointList, modifier = Modifier.padding(20.dp))
                XAxis(modifier = Modifier.fillMaxHeight())
            }
        }
    }
}