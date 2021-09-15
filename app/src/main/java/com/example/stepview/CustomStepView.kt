package com.example.stepview


import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View


/**
 * Created by Mahmudova Dilnoza on 9/15/2021.
 * QQB
 * icebear03051999@gmail.com
 */
class CustomStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val startStep = 0
    private var lineSize = 0
    private var stepCount: Int = 0
    private var selectCircleColor: Int = 0
    private var doneCircleRadius = 0
    private var doneCircleColor: Int = 0
    private var textSize = 0
    private var selectedCircleRadius = 0
    private var currentStep = startStep
    private var textPaint: TextPaint


    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textAlign = Paint.Align.CENTER
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textAlign = Paint.Align.CENTER
        applyStyles(context, attrs, defStyle)


    }

    private fun applyStyles(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomStepView)
        stepCount = ta.getInt(R.styleable.CustomStepView_stepCount, 4)
        selectCircleColor = ta.getColor(R.styleable.CustomStepView_sv_selectedCircleColor, 0)
        doneCircleRadius =
            ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_doneCircleRadius, 0)
        doneCircleColor = ta.getColor(R.styleable.CustomStepView_sv_doneCircleColor, 0)
        textSize = ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_textSize, 20)
        selectedCircleRadius =
            ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_selectedCircleRadius, 0)
        lineSize = ta.getDimensionPixelSize(R.styleable.CustomStepView_sv_lineSize, 10)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (currentStep < stepCount) {
            drawCircle(canvas)
        } else {
            currentStep = 0
            drawCircle(canvas)
        }
        drawLine(canvas)

    }

    private fun drawCircle(canvas: Canvas?) {
        if (currentStep == 0) {
            paint.color = doneCircleColor
            canvas!!.drawCircle(
                selectedCircleRadius.toFloat(),
                selectedCircleRadius.toFloat(),
                selectedCircleRadius.toFloat(),
                paint
            )

            for (i in 1 until stepCount) {
                paint.color = selectCircleColor
                canvas.drawCircle(
                    (i * (2 * selectedCircleRadius + lineSize) + selectedCircleRadius).toFloat(),
                    selectedCircleRadius.toFloat(), selectedCircleRadius.toFloat(), paint
                )

            }
        } else {
            paint.color = doneCircleColor
            canvas!!.drawCircle(
                (currentStep * (2 * selectedCircleRadius + lineSize) + selectedCircleRadius).toFloat(),
                selectedCircleRadius.toFloat(), selectedCircleRadius.toFloat(), paint
            )
            for (i in 0 until stepCount) {
                if (i != currentStep) {
                    paint.color = selectCircleColor
                    canvas.drawCircle(
                        (i * (2 * selectedCircleRadius + lineSize) + selectedCircleRadius).toFloat(),
                        selectedCircleRadius.toFloat(), selectedCircleRadius.toFloat(), paint
                    )
                }

            }
        }
    }

    private fun drawLine(canvas: Canvas?) {
        paint.color = selectCircleColor
        paint.strokeWidth = 10f;
        canvas!!.drawLine(
            0F,
            selectedCircleRadius.toFloat(),
            (8 * selectedCircleRadius + 3 * lineSize).toFloat(),
            (selectedCircleRadius).toFloat(),
            paint
        )

    }


    fun stepNextCurrent() {
        currentStep++
        this.invalidate()
    }


    fun interface OnClickStep {
        fun stepItem(currentStep: Int)
    }
}