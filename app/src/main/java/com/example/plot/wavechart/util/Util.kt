package com.example.plot.wavechart.util

import kotlin.math.floor

fun getRoundedScale(value: Float): Float {
    var value = value
    var multiplyer: Int = 1
    if (value > 0){
        while (value * multiplyer  < 100){
            multiplyer *= 10
        }
    }
    return floor(value * multiplyer) / multiplyer
}

fun checkSignalChange(currentSignal: List<Float>, newSignal: List<Float>, onChange: () -> Unit){
    if(currentSignal != newSignal){
        onChange()
    }
}