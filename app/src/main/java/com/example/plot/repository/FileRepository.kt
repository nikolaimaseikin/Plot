package com.example.plot.repository

import android.content.Context
import android.net.Uri

interface FileRepository {
    suspend fun loadDocument(context: Context, uri: Uri)
    fun getHeaderLength(): Int
    suspend fun getAxisData(axis: Axis): List<Float>
}