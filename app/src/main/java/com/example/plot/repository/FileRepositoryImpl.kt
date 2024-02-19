package com.example.plot.repository

import android.content.Context
import android.net.Uri

class FileRepositoryImpl: FileRepository {
    private val AXIS_VAL_SIZE = 4
    private val SHIFT_BETWEEN_AXIS = 12
    private var document:ByteArray? = null

    override suspend fun loadDocument(context: Context, uri: Uri) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            document = inputStream.readBytes()
        }
    }

    override fun getHeaderLength(): Int {
        return ByteArrayConverter.getIntNumberFromByteArray(document, 0, 4)
    }

    override suspend fun getAxisData(axis: Axis): List<Float>{
        //TODO:добавить проверку на null документ или проверку на несоответствие формату .gtr
        var axisData = mutableListOf<Float>()
        try{
            for(i in (getHeaderLength()+ axis.shift + 4) until (document?.size ?: 0) step SHIFT_BETWEEN_AXIS){
                axisData.add(
                    ByteArrayConverter.getFloatNumberFromByteArray(
                        document, i, i + AXIS_VAL_SIZE
                    )
                )
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return axisData
    }
}