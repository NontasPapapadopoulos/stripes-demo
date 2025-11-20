package com.example.stripesdemo.presentation

import kotlin.Throws
import com.google.zxing.WriterException
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.zxing.common.BitMatrix
import com.google.zxing.MultiFormatWriter
import com.google.zxing.BarcodeFormat
import android.graphics.PointF

object BarcodeUtils {
    @Throws(WriterException::class)
    fun createQrCode(macAddress: String?): Bitmap {
        val matrix = MultiFormatWriter().encode(macAddress, BarcodeFormat.QR_CODE, 300, 300)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until width) {
            for (x in 0 until height) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = -0x1000000 // black pixel
                } else {
                    pixels[y * width + x] = -0x1 // white pixel
                }
            }
        }
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmp
    }

    fun creatBarcode(
        contents: String?,
        desiredWidth: Int, desiredHeight: Int
    ): Bitmap? {
        var resultBitmap: Bitmap? = null
        val marginW = 20
        val barcodeFormat = BarcodeFormat.CODE_128
        resultBitmap = encodeAsBitmap(
            contents, barcodeFormat,
            desiredWidth, desiredHeight
        )
        return resultBitmap
    }

    internal fun encodeAsBitmap(
        contents: String?,
        format: BarcodeFormat?, desiredWidth: Int, desiredHeight: Int
    ): Bitmap {
        val WHITE = -0x1
        val BLACK = -0x1000000
        val writer = MultiFormatWriter()
        var result: BitMatrix? = null
        try {
            result = writer.encode(
                contents, format, desiredWidth,
                desiredHeight, null
            )
        } catch (e: WriterException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        val width = result!!.width
        val height = result.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    internal fun mixtureBitmap(
        first: Bitmap?, second: Bitmap?,
        fromPoint: PointF?
    ): Bitmap? {
        if (first == null || second == null || fromPoint == null) {
            return null
        }
        val marginW = 20
        val newBitmap = Bitmap.createBitmap(
            first.width + second.width + marginW,
            first.height + second.height, Bitmap.Config.ARGB_4444
        )
        val cv = Canvas(newBitmap)
        cv.drawBitmap(first, marginW.toFloat(), 0f, null)
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null)
        //cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore()
        return newBitmap
    }
}