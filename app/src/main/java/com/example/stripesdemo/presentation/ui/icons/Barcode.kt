package com.example.stripesdemo.presentation.ui.icons

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val StripesIcons.Barcode: ImageVector
    get() {
        if (_barcode != null) {
            return _barcode!!
        }
        _barcode = ImageVector.Builder(
            name = "StripesIcons.Barcode", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(40.0f, 760.0f)
                lineTo(40.0f, 200.0f)
                lineTo(125.0f, 200.0f)
                lineTo(125.0f, 760.0f)
                lineTo(40.0f, 760.0f)
                close()
                moveTo(160.0f, 760.0f)
                lineTo(160.0f, 200.0f)
                lineTo(240.0f, 200.0f)
                lineTo(240.0f, 760.0f)
                lineTo(160.0f, 760.0f)
                close()
                moveTo(280.0f, 760.0f)
                lineTo(280.0f, 200.0f)
                lineTo(320.0f, 200.0f)
                lineTo(320.0f, 760.0f)
                lineTo(280.0f, 760.0f)
                close()
                moveTo(400.0f, 760.0f)
                lineTo(400.0f, 200.0f)
                lineTo(480.0f, 200.0f)
                lineTo(480.0f, 760.0f)
                lineTo(400.0f, 760.0f)
                close()
                moveTo(520.0f, 760.0f)
                lineTo(520.0f, 200.0f)
                lineTo(640.0f, 200.0f)
                lineTo(640.0f, 760.0f)
                lineTo(520.0f, 760.0f)
                close()
                moveTo(680.0f, 760.0f)
                lineTo(680.0f, 200.0f)
                lineTo(720.0f, 200.0f)
                lineTo(720.0f, 760.0f)
                lineTo(680.0f, 760.0f)
                close()
                moveTo(800.0f, 760.0f)
                lineTo(800.0f, 200.0f)
                lineTo(920.0f, 200.0f)
                lineTo(920.0f, 760.0f)
                lineTo(800.0f, 760.0f)
                close()
            }
        }
            .build()

        return _barcode!!
    }

private var _barcode: ImageVector? = null