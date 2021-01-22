package com.example.bapplusplus

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation

class RouletteView: View {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }


    // 룰렛 그리기 위한 오브젝트들
    val pnt = Paint()
    val pntText = Paint()
    val rect = RectF()
    val radius: Float = 400f


    // 화면 중앙 좌표값
    val midX: Float = context.resources.displayMetrics.widthPixels.toFloat() / 2
    //    val midY: Float = context.resources.displayMetrics.heightPixels.toFloat() / 2
//    val midX: Float = 500f
    val midY: Float = radius


    var numberOfItems = 1
    var itemListCopy: ArrayList<GetRouletteItemInfo> = ArrayList()

    fun setRoulette(gri: ArrayList<GetRouletteItemInfo>) {
        itemListCopy = gri
        numberOfItems = itemListCopy.size
    }

    // todo: color list 만들기
    var colorList = mutableListOf<Int>(R.color.colorCorn, R.color.colorYellowCrayola, R.color.colorRajah, R.color.colorOutrageousOrange, R.color.colorParadisePink, R.color.colorRedishPink, R.color.colorNewOrange, R.color.colorBlindOrange, R.color.colorMacAndCheese, R.color.colorWarmYellow)

    var startAngle = 0f
    var sweepAngle = 360f / numberOfItems
    var tempString = ""

    var currentAngle = 0f
        set(value) {
            field = value
            invalidate()
        }

    private fun Float.applyAngle(): Float {
        val v = this + currentAngle
        return if (v > 360) v - 360 else v
    }

    // rotate function
    fun rotate(view: View, fromDegree: Float, toDegree: Float) {
        var rotate = RotateAnimation(fromDegree, toDegree,
            RotateAnimation.ABSOLUTE, midX,
            RotateAnimation.ABSOLUTE, midY)

        rotate.setInterpolator(DecelerateInterpolator())
        rotate.duration = 3000
        rotate.fillAfter = true

        view.startAnimation(rotate)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        sweepAngle = 360f / numberOfItems

        pntText.textSize = 50f
        pntText.color = Color.BLACK

        rect.set(midX - radius, midY - radius, midX + radius, midY + radius)

        canvas?.save()

        for(i in 0 until numberOfItems) {
            pnt.color = resources.getColor(colorList.get(i))
            startAngle = sweepAngle * i

            canvas?.rotate(startAngle.applyAngle(), midX, midY)
            canvas?.drawArc(rect, 0f, sweepAngle, true, pnt)

//            canvas?.rotate(90 + sweepAngle/2f, midX, midY)
//            canvas?.drawText((i+1).toString(), midX - 20f, midY - 300f, pntText) //
//            canvas?.restore()
//            canvas?.save()

            if(itemListCopy[i].RestTitle!!.length > 4) {
                tempString = itemListCopy[i].RestTitle!!.slice(IntRange(0, 4)) + ".."
            }
            else {
                tempString = itemListCopy[i].RestTitle!!
            }

            canvas?.rotate(sweepAngle/2f, midX, midY)
            canvas?.drawText(tempString, midX + 100f, midY + 20f, pntText) //
            canvas?.restore()
            canvas?.save()
        }
    }

}