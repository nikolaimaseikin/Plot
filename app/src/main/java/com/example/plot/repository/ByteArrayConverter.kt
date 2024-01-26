package com.example.plot.repository

import java.nio.ByteBuffer
import java.nio.ByteOrder


class ByteArrayConverter {
    companion object{
        private fun validateIndex(array: ByteArray, startIndex: Int, endIndex: Int): Boolean {
            if(!(startIndex < 0 || endIndex < 0 || startIndex >= endIndex || endIndex > array.size)) {
                return true
            }
            return false
        }

        private fun getByteBufferFromByteArray(
            array: ByteArray,
            startIndex: Int,
            endIndex: Int
        ): ByteBuffer? {
            if(validateIndex(array, startIndex, endIndex)){
                val buffer = ByteBuffer.wrap(array, startIndex, endIndex - startIndex)
                buffer.order(ByteOrder.LITTLE_ENDIAN)
                return buffer
            }
            return null
        }
        fun getFloatNumberFromByteArray(array: ByteArray?, startIndex: Int, endIndex: Int): Float {
            return getByteBufferFromByteArray(array!!, startIndex, endIndex)!!.float
        }

        fun getByteArrayFromFloat(value: Float): ByteArray {
            val byteArray = ByteArray(4)
            val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            buffer.putFloat(value)
            buffer.flip()
            buffer.get(byteArray)
            return byteArray
        }

        fun getIntNumberFromByteArray(array: ByteArray?, startIndex: Int, endIndex: Int): Int {
            return getByteBufferFromByteArray(array!!, startIndex, endIndex)!!.int
        }

        fun getByteArrayFromInt(value: Int): ByteArray {
            val byteArray = ByteArray(4)
            val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            buffer.putInt(value)
            buffer.flip()
            buffer.get(byteArray)
            return byteArray
        }

        fun getValueTypeFromByteArray(
            array: ByteArray?,
            startIndex: Int,
            endIndex: Int
        ): ValueType {
            val buffer = getByteBufferFromByteArray(array!!, startIndex, endIndex)
            val valueType: ValueType
            val value = buffer!!.int
            return if (value in 0..2) {
                valueType = when (value) {
                    0 -> {
                        ValueType.PEAK
                    }

                    1 -> {
                        ValueType.RMS
                    }

                    else -> {
                        ValueType.PP
                    }
                }
                valueType
            } else {
                ValueType.RMS //TODO: Можно выкинуть исключение, но протоколом гарантируется передача корректных значений
            }
        }

        fun getByteArrayFromValueType(valueType: ValueType): ByteArray {
            val byteArray = ByteArray(4)
            val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            when(valueType){
                ValueType.PEAK -> {
                    buffer.putInt(0)
                }
                ValueType.RMS -> {
                    buffer.putInt(1)
                }
                ValueType.PP -> {
                    buffer.putInt(2)
                }
            }
            buffer.flip()
            buffer.get(byteArray)
            return byteArray
        }

        fun getByteArrayFromThreshold(threshold: Boolean): ByteArray {
            val byteArray = ByteArray(2)
            val buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
            buffer.putShort(if(threshold) 1 else 0)
            buffer.flip()
            buffer.get(byteArray)
            return byteArray
        }

        fun getThresholdFromByteArray(array: ByteArray?, startIndex: Int, endIndex: Int): Boolean {
            return getByteBufferFromByteArray(array!!, startIndex, endIndex)!!.short == 1.toShort()
        }
    }
}