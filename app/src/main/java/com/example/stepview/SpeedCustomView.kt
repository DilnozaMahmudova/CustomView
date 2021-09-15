package com.example.stepview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet

import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt


/**
 * Created by Mahmudova Dilnoza on 9/15/2021.
 * QQB
 * icebear03051999@gmail.com
 */
class SpeedCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint: TextPaint
    var width = 0f
    private var height = 0f
    private var center_x = 0f
    private val CENTER = 0.5f
    var center_y = 0f
    val oval = RectF()

    val touchArea = RectF()
    var sweep = 0f
    var left = 0f
    var right = 0f
    var percentage = 0
    val startX: Float = CENTER
    val startY: Float = CENTER + 0.2f

    init {

        paint.textAlign = Paint.Align.CENTER
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textAlign = Paint.Align.CENTER
//        applyStyles(context, attrs, defStyle)


    }

    //    private fun applyStyles(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
//        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomStepView)
//        stepCount = ta.getInt(R.styleable.CustomStepView_stepCount, 4)
//        selectCircleColor = ta.getColor(R.styleable.CustomStepView_sv_selectedCircleColor, 0)
//        doneCircleRadius =
//            ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_doneCircleRadius, 0)
//        doneCircleColor = ta.getColor(R.styleable.CustomStepView_sv_doneCircleColor, 0)
//        textSize = ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_textSize, 20)
//        selectedCircleRadius =
//            ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_selectedCircleRadius, 0)
//        lineSize = ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_lineSize, 10)
//        ta.recycle()
//    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        drawText(canvas, -1, textValue, startX, startY, paint)
        val radius: Float = if (width > height) {
            height / 3
        } else {
            width / 3
        }


        paint.isAntiAlias = true
        paint.color = -0x2d374a
        paint.strokeWidth = 35F
        paint.style = Paint.Style.STROKE
        center_x = width / 2
        center_y = height / 2
        left = center_x - radius
        val top = center_y - radius
        right = center_x + radius
        val bottom = center_y + radius
        oval[left, top, right] = bottom

        //this is the background arc, it remains constant
        canvas.drawArc(oval, 180F, 180F, false, paint)
        paint.strokeWidth = 10F
        paint.color = -0x1fadb3
        //this is the red arc whichhas its sweep argument manipulated by on touch
        canvas.drawArc(oval, 180F, sweep, false, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            val xPosition = event.x
            val yPosition = event.y
            if (oval.contains(xPosition, yPosition)) {
                val x = xPosition - left
                val s = x * 100
                val b = s / oval.width()
                percentage = b.roundToInt()
                sweep = 180 / 100.0f * percentage.toFloat()
                invalidate()
            } else {
                if (xPosition < left) {
                    percentage = 0
                    sweep = 180 / 100.0f * percentage.toFloat()
                    invalidate()
                }
                if (xPosition > right) {
                    percentage = 100
                    sweep = 180 / 100.0f * percentage.toFloat()
                    invalidate()
                }
            }
        }
        return true
    }
    fun drawSpacedText(
        canvas: Canvas,
        text: String,
        left: Float,
        top: Float,
        paint: Paint,
        spacingPx: Float
    ) {
        var currentLeft = left
        for (element in text) {
            val c = element.toString() + ""
            canvas.drawText(c, currentLeft, top, paint)
            currentLeft += spacingPx
            currentLeft += paint.measureText(c)
        }
    }
    private fun drawText(canvas:Canvas,tick:Int, value:String, x:Float, y:Float, paint:Paint) {

        val magnifier = 100f
        canvas.drawText(value, x * magnifier + textWidth, y * magnifier + textHeight, paint)
    }
}
