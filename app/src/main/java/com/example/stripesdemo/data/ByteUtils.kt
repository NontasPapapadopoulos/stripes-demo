package com.example.stripesdemo.data

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.Arrays

/**
 * 字节的转换
 * Created by jerry on 2017/4/27.
 */
object ByteUtils {
    //inputstream转byte[]
    fun stream2Bytes(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var n = 0
        try {
            while (-1 != input.read(buffer).also { n = it }) {
                output.write(buffer, 0, n)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return output.toByteArray()
    }

    //将字节数组转换为short类型，即统计字符串长度
    fun bytes2Short2(b: ByteArray): Short {
        return (b[1].toInt() and 0xff shl 8 or (b[0].toInt() and 0xff)).toShort()
    }

    /**
     * 以字符串表示形式返回字节数组的内容
     *
     * @param bytes 字节数组
     * @return 字符串形式的 <tt>bytes</tt>
     * [01, fe, 08, 35, f1, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
     */
    fun toHexString(bytes: ByteArray?): String {
        if (bytes == null) return "null"
        val iMax = bytes.size - 1
        if (iMax == -1) return "[]"
        val b = StringBuilder()
        b.append('[')
        var i = 0
        while (true) {
            b.append(String.format("%02x", bytes[i].toInt() and 0xFF))
            if (i == iMax) return b.append(']').toString()
            b.append(", ")
            i++
        }
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param bytes
     * @return  01FE0835F1000000000000000000000000000000
     */
    fun bytes2HexStr(bytes: ByteArray?): String? {
        if (bytes == null) {
            return null
        }
        val b = StringBuilder()
        for (i in bytes.indices) {
            b.append(String.format("%02x", bytes[i].toInt() and 0xFF))
        }
        return b.toString()
    }

    fun hexStr2Bytes(str: String?): ByteArray? {
        if (str == null) {
            return null
        }
        if (str.length == 0) {
            return ByteArray(0)
        }
        val byteArray = ByteArray(str.length / 2)
        for (i in byteArray.indices) {
            val subStr = str.substring(2 * i, 2 * i + 2)
            byteArray[i] = subStr.toInt(16).toByte()
        }
        return byteArray
    }

    //3.short转换为byte数组
    fun short2Bytes(value: Short): ByteArray {
        val data = ByteArray(2)
        data[0] = (value.toInt() shr 8 and 0xff).toByte()
        data[1] = (value.toInt() and 0xFF).toByte()
        return data
    }

    /**
     * 将int转化成byte[]
     *
     * @param res 要转化的整数
     * @return 对应的byte[]
     */
    fun int2byte(res: Int): ByteArray {
        val targets = ByteArray(4)
        targets[0] = (res and 0xff).toByte() // 最低位
        targets[1] = (res shr 8 and 0xff).toByte() // 次低位
        targets[2] = (res shr 16 and 0xff).toByte() // 次高位
        targets[3] = (res ushr 24).toByte() // 最高位,无符号右移。
        return targets
    }

    /**
     * 将byte[]转化成int
     *
     * @param res 要转化的byte[]
     * @return 对应的整数
     */
    fun byte2int(res: ByteArray): Int {
        return res[0].toInt() and 0xff or (res[1].toInt() shl 8 and 0xff00) or (res[2]
            .toInt() shl 24 ushr 8) or (res[3].toInt() shl 24)
    }

    /**
     * 以字节数组的形式返回指定的布尔值
     *
     * @param data 一个布尔值
     * @return 长度为 1 的字节数组
     */
    fun getBytes(data: Boolean): ByteArray {
        val bytes = ByteArray(1)
        bytes[0] = (if (data) 1 else 0).toByte()
        return bytes
    }

    /**
     * 以字节数组的形式返回指定的 16 位有符号整数值
     *
     * @param data 要转换的数字
     * @return 长度为 2 的字节数组
     */
    fun getBytes(data: Short): ByteArray {
        val bytes = ByteArray(2)
        if (isLittleEndian) {
            bytes[0] = (data.toInt() and 0xff).toByte()
            bytes[1] = (data.toInt() and 0xff00 shr 8).toByte()
        } else {
            bytes[1] = (data.toInt() and 0xff).toByte()
            bytes[0] = (data.toInt() and 0xff00 shr 8).toByte()
        }
        return bytes
    }

    /**
     * 以字节数组的形式返回指定的 Unicode 字符值
     *
     * @param data 要转换的字符
     * @return 长度为 2 的字节数组
     */
    fun getBytes(data: Char): ByteArray {
        val bytes = ByteArray(2)
        if (isLittleEndian) {
            bytes[0] = data.code.toByte()
            bytes[1] = (data.code shr 8).toByte()
        } else {
            bytes[1] = data.code.toByte()
            bytes[0] = (data.code shr 8).toByte()
        }
        return bytes
    }

    /**
     * 以字节数组的形式返回指定的 32 位有符号整数值
     *
     * @param data 要转换的数字
     * @return 长度为 4 的字节数组
     */
    fun getBytes(data: Int): ByteArray {
        val bytes = ByteArray(4)
        if (isLittleEndian) {
            bytes[0] = (data and 0xff).toByte()
            bytes[1] = (data and 0xff00 shr 8).toByte()
            bytes[2] = (data and 0xff0000 shr 16).toByte()
            bytes[3] = (data and -0x1000000 shr 24).toByte()
        } else {
            bytes[3] = (data and 0xff).toByte()
            bytes[2] = (data and 0xff00 shr 8).toByte()
            bytes[1] = (data and 0xff0000 shr 16).toByte()
            bytes[0] = (data and -0x1000000 shr 24).toByte()
        }
        return bytes
    }

    /**
     * 以字节数组的形式返回指定的 64 位有符号整数值
     *
     * @param data 要转换的数字
     * @return 长度为 8 的字节数组
     */
    fun getBytes(data: Long): ByteArray {
        val bytes = ByteArray(8)
        if (isLittleEndian) {
            bytes[0] = (data and 0xffL).toByte()
            bytes[1] = (data shr 8 and 0xffL).toByte()
            bytes[2] = (data shr 16 and 0xffL).toByte()
            bytes[3] = (data shr 24 and 0xffL).toByte()
            bytes[4] = (data shr 32 and 0xffL).toByte()
            bytes[5] = (data shr 40 and 0xffL).toByte()
            bytes[6] = (data shr 48 and 0xffL).toByte()
            bytes[7] = (data shr 56 and 0xffL).toByte()
        } else {
            bytes[7] = (data and 0xffL).toByte()
            bytes[6] = (data shr 8 and 0xffL).toByte()
            bytes[5] = (data shr 16 and 0xffL).toByte()
            bytes[4] = (data shr 24 and 0xffL).toByte()
            bytes[3] = (data shr 32 and 0xffL).toByte()
            bytes[2] = (data shr 40 and 0xffL).toByte()
            bytes[1] = (data shr 48 and 0xffL).toByte()
            bytes[0] = (data shr 56 and 0xffL).toByte()
        }
        return bytes
    }

    /**
     * 以字节数组的形式返回指定的单精度浮点值
     *
     * @param data 要转换的数字
     * @return 长度为 4 的字节数组
     */
    fun getBytes(data: Float): ByteArray {
        return getBytes(java.lang.Float.floatToIntBits(data))
    }

    /**
     * 以字节数组的形式返回指定的双精度浮点值
     *
     * @param data 要转换的数字
     * @return 长度为 8 的字节数组
     */
    fun getBytes(data: Double): ByteArray {
        return getBytes(java.lang.Double.doubleToLongBits(data))
    }

    /**
     * 将指定字符串中的所有字符编码为一个字节序列
     *
     * @param data 包含要编码的字符的字符串
     * @return 一个字节数组，包含对指定的字符集进行编码的结果
     */
    fun getBytes(data: String): ByteArray {
        return data.toByteArray(Charset.forName("UTF-8"))
    }

    /**
     * 将指定字符串中的所有字符编码为一个字节序列
     *
     * @param data        包含要编码的字符的字符串
     * @param charsetName 字符集编码
     * @return 一个字节数组，包含对指定的字符集进行编码的结果
     */
    fun getBytes(data: String, charsetName: String?): ByteArray {
        return data.toByteArray(Charset.forName(charsetName))
    }

    /**
     * 返回由字节数组转换来的布尔值
     *
     * @param bytes 字节数组
     * @return 布尔值
     */
    fun toBoolean(bytes: ByteArray): Boolean {
        return if (bytes[0].toInt() == 0) false else true
    }

    /**
     * 返回由字节数组中的指定的一个字节转换来的布尔值
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 布尔值
     */
    fun toBoolean(bytes: ByteArray, startIndex: Int): Boolean {
        return toBoolean(copyFrom(bytes, startIndex, 1))
    }

    /**
     * 返回由字节数组转换来的 16 位有符号整数
     *
     * @param bytes 字节数组
     * @return 由两个字节构成的 16 位有符号整数
     */
    fun toShort(bytes: ByteArray): Short {
        return if (isLittleEndian) {
            (0xff and bytes[0].toInt() or (0xff00 and (bytes[1].toInt() shl 8))).toShort()
        } else {
            (0xff and bytes[1].toInt() or (0xff00 and (bytes[0].toInt() shl 8))).toShort()
        }
    }

    /**
     * 返回由字节数组中的指定的两个字节转换来的 16 位有符号整数
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由两个字节构成的 16 位有符号整数
     */
    fun toShort(bytes: ByteArray, startIndex: Int): Short {
        return toShort(copyFrom(bytes, startIndex, 2))
    }

    /**
     * 返回由字节数组转换来的 Unicode 字符
     *
     * @param bytes 字节数组
     * @return 由两个字节构成的字符
     */
    fun toChar(bytes: ByteArray): Char {
        return if (isLittleEndian) {
            (0xff and bytes[0].toInt() or (0xff00 and (bytes[1].toInt() shl 8))).toChar()
        } else {
            (0xff and bytes[1].toInt() or (0xff00 and (bytes[0].toInt() shl 8))).toChar()
        }
    }

    /**
     * 返回由字节数组中的指定的两个字节转换来的 Unicode 字符
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由两个字节构成的字符
     */
    fun toChar(bytes: ByteArray, startIndex: Int): Char {
        return toChar(copyFrom(bytes, startIndex, 2))
    }

    /**
     * 返回由字节数组转换来的 32 位有符号整数
     *
     * @param bytes 字节数组
     * @return 由四个字节构成的 32 位有符号整数
     */
    fun toInt(bytes: ByteArray): Int {
        return if (isLittleEndian) {
            (0xff and bytes[0].toInt()
                    or (0xff00 and (bytes[1].toInt() shl 8))
                    or (0xff0000 and (bytes[2].toInt() shl 16))
                    or (-0x1000000 and (bytes[3].toInt() shl 24)))
        } else {
            (0xff and bytes[3].toInt()
                    or (0xff00 and (bytes[2].toInt() shl 8))
                    or (0xff0000 and (bytes[1].toInt() shl 16))
                    or (-0x1000000 and (bytes[0].toInt() shl 24)))
        }
    }

    /**
     * 返回由字节数组中的指定的四个字节转换来的 32 位有符号整数
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由四个字节构成的 32 位有符号整数
     */
    fun toInt(bytes: ByteArray, startIndex: Int): Int {
        return toInt(copyFrom(bytes, startIndex, 4))
    }

    /**
     * 返回由字节数组转换来的 64 位有符号整数
     *
     * @param bytes 字节数组
     * @return 由八个字节构成的 64 位有符号整数
     */
    fun toLong(bytes: ByteArray): Long {
        return if (isLittleEndian) {
            (0xffL and bytes[0].toLong()
                    or (0xff00L and (bytes[1].toLong() shl 8))
                    or (0xff0000L and (bytes[2].toLong() shl 16))
                    or (0xff000000L and (bytes[3].toLong() shl 24))
                    or (0xff00000000L and (bytes[4].toLong() shl 32))
                    or (0xff0000000000L and (bytes[5].toLong() shl 40))
                    or (0xff000000000000L and (bytes[6].toLong() shl 48))
                    or (-0x100000000000000L and (bytes[7].toLong() shl 56)))
        } else {
            (0xffL and bytes[7].toLong()
                    or (0xff00L and (bytes[6].toLong() shl 8))
                    or (0xff0000L and (bytes[5].toLong() shl 16))
                    or (0xff000000L and (bytes[4].toLong() shl 24))
                    or (0xff00000000L and (bytes[3].toLong() shl 32))
                    or (0xff0000000000L and (bytes[2].toLong() shl 40))
                    or (0xff000000000000L and (bytes[1].toLong() shl 48))
                    or (-0x100000000000000L and (bytes[0].toLong() shl 56)))
        }
    }

    /**
     * 返回由字节数组中的指定的八个字节转换来的 64 位有符号整数
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由八个字节构成的 64 位有符号整数
     */
    fun toLong(bytes: ByteArray, startIndex: Int): Long {
        return toLong(copyFrom(bytes, startIndex, 8))
    }

    /**
     * 返回由字节数组转换来的单精度浮点数
     *
     * @param bytes 字节数组
     * @return 由四个字节构成的单精度浮点数
     */
    fun toFloat(bytes: ByteArray): Float {
        return java.lang.Float.intBitsToFloat(toInt(bytes))
    }

    /**
     * 返回由字节数组中的指定的四个字节转换来的单精度浮点数
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由四个字节构成的单精度浮点数
     */
    fun toFloat(bytes: ByteArray, startIndex: Int): Float {
        return java.lang.Float.intBitsToFloat(toInt(copyFrom(bytes, startIndex, 4)))
    }

    /**
     * 返回由字节数组转换来的双精度浮点数
     *
     * @param bytes 字节数组
     * @return 由八个字节构成的双精度浮点数
     */
    fun toDouble(bytes: ByteArray): Double {
        return java.lang.Double.longBitsToDouble(toLong(bytes))
    }

    /**
     * 返回由字节数组中的指定的八个字节转换来的双精度浮点数
     *
     * @param bytes      字节数组
     * @param startIndex 起始下标
     * @return 由八个字节构成的双精度浮点数
     */
    fun toDouble(bytes: ByteArray, startIndex: Int): Double {
        return java.lang.Double.longBitsToDouble(toLong(copyFrom(bytes, startIndex, 8)))
    }

    /**
     * 返回由字节数组转换来的字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    fun toString(bytes: ByteArray?): String {
        return String(bytes!!, Charset.forName("UTF-8"))
    }

    /**
     * 返回由字节数组转换来的字符串
     *
     * @param bytes       字节数组
     * @param charsetName 字符集编码
     * @return 字符串
     */
    fun toString(bytes: ByteArray?, charsetName: String?): String {
        return String(bytes!!, Charset.forName(charsetName))
    }
    // --------------------------------------------------------------------------------------------
    /**
     * 数组拷贝。
     *
     * @param src 字节数组。
     * @param off 起始下标。
     * @param len 拷贝长度。
     * @return 指定长度的字节数组。
     */
    private fun copyFrom(src: ByteArray, off: Int, len: Int): ByteArray {
        // return Arrays.copyOfRange(src, off, off + len);
        val bits = ByteArray(len)
        var i = off
        var j = 0
        while (i < src.size && j < len) {
            bits[j] = src[i]
            i++
            j++
        }
        return bits
    }

    /**
     * 判断 CPU Endian 是否为 Little
     *
     * @return 判断结果
     */
    private val isLittleEndian: Boolean
        private get() = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN

    /**
     * 拼接两个字节数组
     * @return
     */
    fun concat(first: ByteArray, second: ByteArray): ByteArray {
        val result = Arrays.copyOf(first, first.size + second.size)
        System.arraycopy(second, 0, result, first.size, second.size)
        return result
    }
}