package com.example.plot.wavechart

import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt
//TODO: Исправить отображение осей
//TODO: Реализовать масштабирование по оси Y
//TODO: Продумать граничные случаи функций и реализовать обработку исключений
//TODO: Реализовать отображение курсора

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

fun getSubSignalList(signal: List<Float>, centerIndex: Int, zoom: Float): List<Float> {
    if(signal.isEmpty()){
        return listOf()
    }
    val windowSize = (signal.size / zoom)
        .coerceAtLeast(0f)
        .coerceAtMost(signal.size.toFloat())
    val leftIndex: Int = (centerIndex - (windowSize / 2f))
        .roundToInt()
        .coerceAtLeast(0)
        .coerceAtMost(signal.size - 1)
    val rightIndex: Int = ((centerIndex + (windowSize / 2f)).roundToInt() - 1)
        .coerceAtLeast(0)
        .coerceAtMost(signal.size - 1)
    val subSignalListToPlotting = signal.subList(leftIndex, rightIndex)
    Log.d("SubSignal", "WindowSise: $windowSize Left: $leftIndex Right: $rightIndex Zoom: $zoom Center: $centerIndex")
    return subSignalListToPlotting
}