package com.example.plot.wavechart

import android.util.Log
import kotlin.math.roundToInt
//TODO: Column переделать в Dataclass
fun getColumnsList(signal: List<Float>, canvasWidth: Float): MutableList<Set<Float>>{
    var separateValue = (signal.size / canvasWidth).roundToInt()
    if(separateValue <= 1) separateValue = 2
    Log.d("Cnvs", "Width: $canvasWidth Sep: $separateValue")
    var columns: MutableList<Set<Float>> = mutableListOf()
    var leftIndex = 0
    var rightIndex = separateValue
    while(rightIndex < signal.size){
        val subList = signal.subList(leftIndex, rightIndex)
        columns.add(setOf(subList.min(), subList.max()))
        leftIndex += separateValue
        rightIndex += separateValue
    }
    Log.d("Cnvs", "Columns: $columns")
    return columns
}