package com.example.plot.wavechart

import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt
//TODO: Column переделать в Dataclass
fun getColumnsList(signal: List<Float>, canvasWidth: Float): MutableList<Set<Float>>{
    var separateValue = (signal.size / canvasWidth).roundToInt()
    if(separateValue <= 1) separateValue = 2
    var columns: MutableList<Set<Float>> = mutableListOf()
    var leftIndex = 0
    var rightIndex = separateValue
    while(rightIndex < signal.size){
        val subList = signal.subList(leftIndex, rightIndex)
        //Log.d("Cnvs", "Separate: $separateValue SubList: $subList Left: $leftIndex Right: $rightIndex")
        columns.add(setOf(subList.min(), subList.max()))
        //Log.d("Cnvs", "min: ${subList.min()} max: ${subList.max()}")
        leftIndex += separateValue
        rightIndex += separateValue
    }
    //Log.d("Cnvs", "Columns: $columns")
    return columns
}

fun getSubSignalList(signal: List<Float>, zoom: Float, offset: Offset): List<Float> {
    val centerIndex = ((signal.size / 2) + (offset.x / zoom)).roundToInt()
    if(centerIndex >= 0 ) centerIndex else 0
    if(centerIndex < signal.size ) centerIndex else signal.size - 1
    val windowSize = (signal.size / zoom).roundToInt()
    val leftIndex: Int = centerIndex - (windowSize / 2f).roundToInt()
    if(leftIndex >= 0) leftIndex else 0
    val rightIndex: Int = centerIndex + (windowSize / 2f).roundToInt() - 1
    if(rightIndex >= 0 && rightIndex < signal.size) rightIndex else (signal.size - 1)
    val subList = signal.subList(leftIndex, rightIndex)
    Log.d("Signal", "$subList")
    return subList
}