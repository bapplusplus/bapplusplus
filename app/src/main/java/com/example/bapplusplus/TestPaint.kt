package com.example.bapplusplus

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
//    color = textColor
//    if (textHeight == 0f) {
//        textHeight = textSize
//    } else {
//        textSize = textHeight
//    }
//}
//
//private val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//    style = Paint.Style.FILL
//    textSize = textHeight
//}
//
//private val shadowPaint = Paint(0).apply {
//    color = 0x101010
//    maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
//}

//override fun onDraw(canvas: Canvas) {
//    super.onDraw(canvas)
//
//    canvas.apply {
//        // Draw the shadow
//        drawOval(shadowBounds, shadowPaint)
//
//        // Draw the label text
//        drawText(data[mCurrentItem].mLabel, textX, textY, textPaint)
//
//        // Draw the pie slices
//        data.forEach {
//            piePaint.shader = it.mShader
//            drawArc(bounds,
//                360 - it.endAngle,
//                it.endAngle - it.startAngle,
//                true, piePaint)
//        }
//
//        // Draw the pointer
//        drawLine(textX, pointerY, pointerX, pointerY, textPaint)
//        drawCircle(pointerX, pointerY, pointerSize, mTextPaint)
//    }
//}

class TestPaint : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_paint)


    }
}