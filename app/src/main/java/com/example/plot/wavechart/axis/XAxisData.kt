package com.example.plot.wavechart.axis

import androidx.compose.ui.unit.Dp

data class XAxisData(
    val signal: List<Float>,
    val sampleRate: Int,
    val startIndex: Int,
    val steps: Int,
    val offset: Dp
)

