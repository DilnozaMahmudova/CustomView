//package com.example.stepview
//
//import android.R
//
//import androidx.core.view.ViewCompat.setLayerType
//
//import android.os.Build
//
//import android.view.View.MeasureSpec
//
//import android.animation.ValueAnimator
//
//import android.annotation.TargetApi
//
//import android.animation.TypeEvaluator
//import android.animation.ValueAnimator.AnimatorUpdateListener
//import android.content.Context
//
//import android.content.res.TypedArray
//import android.graphics.*
//import android.view.MotionEvent
//import android.view.View
//import androidx.core.view.ViewCompat
//import kotlin.math.roundToInt
//
//
///**
// * Created by Mahmudova Dilnoza on 9/15/2021.
// * QQB
// * icebear03051999@gmail.com
// */
//class Speed : View {
//        private var maxSpeed = DEFAULT_MAX_SPEED
//        var speed = 0.0
//            set(speed) {
//                var speed = speed
//                require(speed >= 0) { "Non-positive value specified as a speed." }
//                if (speed > maxSpeed) speed = maxSpeed
//                field = speed
//                invalidate()
//            }
//        private var defaultColor: Int = Color.rgb(180, 180, 180)
//        private var majorTickStep = DEFAULT_MAJOR_TICK_STEP
//        private var minorTicks = DEFAULT_MINOR_TICKS
//        private var labelConverter: LabelConverter? = null
//        private val ranges: MutableList<ColoredRange> = ArrayList()
//        private var backgroundPaint: Paint? = null
//        private var backgroundInnerPaint: Paint? = null
//        private var maskPaint: Paint? = null
//        private var needlePaint: Paint? = null
//        private var ticksPaint: Paint? = null
//        private var txtPaint: Paint? = null
//        private var colorLinePaint: Paint? = null
//        private var mMask: Bitmap? = null
//
//        constructor(context: Context?) : super(context) {
//            init()
//        }
//
//        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//            val attributes: TypedArray = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.SpeedometerView,
//                0, 0
//            )
//            speed = try {
//                // read attributes
//                setMaxSpeed(
//                    attributes.getFloat(
//                        R.styleable.SpeedometerView_maxSpeed,
//                        DEFAULT_MAX_SPEED.toFloat()
//                    ).toDouble()
//                )
//                attributes.getFloat(R.styleable.SpeedometerView_speed, 0f).toDouble()
//            } finally {
//                attributes.recycle()
//            }
//            init()
//        }
//
//        fun getMaxSpeed(): Double {
//            return maxSpeed
//        }
//
//        fun setMaxSpeed(maxSpeed: Double) {
//            require(maxSpeed > 0) { "Non-positive value specified as max speed." }
//            this.maxSpeed = maxSpeed
//            invalidate()
//        }
//
//        @TargetApi(11)
//        fun setSpeed(progress: Double, duration: Long, startDelay: Long): ValueAnimator {
//            var progress = progress
//            require(progress > 0) { "Non-positive value specified as a speed." }
//            if (progress > maxSpeed) progress = maxSpeed
//            val va = ValueAnimator.ofObject(
//                { fraction, startValue, endValue -> startValue + fraction * (endValue - startValue) },
//                java.lang.Double.valueOf(speed),
//                java.lang.Double.valueOf(progress)
//            )
//            va.duration = duration
//            va.startDelay = startDelay
//            va.addUpdateListener { animation ->
//                val value = animation.animatedValue as Double
//                if (value != null) speed = value
//                Log.d(
//                    TAG,
//                    "setSpeed(): onAnumationUpdate() -> value = $value"
//                )
//            }
//            va.start()
//            return va
//        }
//
//        @TargetApi(11)
//        fun setSpeed(progress: Double, animate: Boolean): ValueAnimator {
//            return setSpeed(progress, 1500, 200)
//        }
//
//        fun getDefaultColor(): Int {
//            return defaultColor
//        }
//
//        fun setDefaultColor(defaultColor: Int) {
//            this.defaultColor = defaultColor
//            invalidate()
//        }
//
//        fun getMajorTickStep(): Double {
//            return majorTickStep
//        }
//
//        fun setMajorTickStep(majorTickStep: Double) {
//            require(majorTickStep > 0) { "Non-positive value specified as a major tick step." }
//            this.majorTickStep = majorTickStep
//            invalidate()
//        }
//
//        fun getMinorTicks(): Int {
//            return minorTicks
//        }
//
//        fun setMinorTicks(minorTicks: Int) {
//            this.minorTicks = minorTicks
//            invalidate()
//        }
//
//        fun getLabelConverter(): LabelConverter? {
//            return labelConverter
//        }
//
//        fun setLabelConverter(labelConverter: LabelConverter?) {
//            this.labelConverter = labelConverter
//            invalidate()
//        }
//
//        fun clearColoredRanges() {
//            ranges.clear()
//            invalidate()
//        }
//
//        fun addColoredRange(begin: Double, end: Double, color: Int) {
//            var begin = begin
//            var end = end
//            require(begin < end) { "Incorrect number range specified!" }
//            if (begin < -5.0 / 160 * maxSpeed) begin = -5.0 / 160 * maxSpeed
//            if (end > maxSpeed * (5.0 / 160 + 1)) end = maxSpeed * (5.0 / 160 + 1)
//            ranges.add(ColoredRange(color, begin, end))
//            invalidate()
//        }
//
//        protected fun onDraw(canvas: Canvas) {
//            super.onDraw(canvas)
//
//            // Clear canvas
//            canvas.drawColor(Color.TRANSPARENT)
//
//            // Draw Metallic Arc and background
//            drawBackground(canvas)
//
//            // Draw Ticks and colored arc
//            drawTicks(canvas)
//
//            // Draw Needle
//            drawNeedle(canvas)
//        }
//
//        protected fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
//            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
//            var width: Int
//            var height: Int
//
//            //Measure Width
//            width = if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
//                //Must be this size
//                widthSize
//            } else {
//                -1
//            }
//
//            //Measure Height
//            height = if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
//                //Must be this size
//                heightSize
//            } else {
//                -1
//            }
//            if (height >= 0 && width >= 0) {
//                width = Math.min(height, width)
//                height = width / 2
//            } else if (width >= 0) {
//                height = width / 2
//            } else if (height >= 0) {
//                width = height * 2
//            } else {
//                width = 0
//                height = 0
//            }
//
//            //MUST CALL THIS
//            setMeasuredDimension(width, height)
//        }
//
//        private fun drawNeedle(canvas: Canvas) {
//            val oval: RectF = getOval(canvas, 1f)
//            val radius = oval.width() * 0.35f
//            val angle = 10 + (speed / getMaxSpeed() * 160).toFloat()
//            canvas.drawLine(
//                (oval.centerX() + 0),
//                (oval.centerY() - 0),
//                (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * radius).toFloat(),
//                (oval.centerY() - Math.sin(angle / 180 * Math.PI) * radius).toFloat(),
//                needlePaint
//            )
//            val smallOval: RectF = getOval(canvas, 0.2f)
//            canvas.drawArc(smallOval, 180, 180, true, backgroundPaint)
//        }
//
//        private fun drawTicks(canvas: Canvas) {
//            val availableAngle = 160f
//            val majorStep = (majorTickStep / maxSpeed * availableAngle).toFloat()
//            val minorStep = majorStep / (1 + minorTicks)
//            val majorTicksLength = 30f
//            val minorTicksLength = majorTicksLength / 2
//            val oval: RectF = getOval(canvas, 1f)
//            val radius = oval.width() * 0.35f
//            var currentAngle = 10f
//            var curProgress = 0.0
//            while (currentAngle <= 170) {
//                canvas.drawLine(
//                    (oval.centerX() + Math.cos((180 - currentAngle) / 180 * Math.PI) * (radius - majorTicksLength / 2)).toFloat(),
//                    (oval.centerY() - Math.sin(currentAngle / 180 * Math.PI) * (radius - majorTicksLength / 2)).toFloat(),
//                    (oval.centerX() + Math.cos((180 - currentAngle) / 180 * Math.PI) * (radius + majorTicksLength / 2)).toFloat(),
//                    (oval.centerY() - Math.sin(currentAngle / 180 * Math.PI) * (radius + majorTicksLength / 2)).toFloat(),
//                    ticksPaint
//                )
//                for (i in 1..minorTicks) {
//                    val angle = currentAngle + i * minorStep
//                    if (angle >= 170 + minorStep / 2) {
//                        break
//                    }
//                    canvas.drawLine(
//                        (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * radius).toFloat(),
//                        (oval.centerY() - Math.sin(angle / 180 * Math.PI) * radius).toFloat(),
//                        (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * (radius + minorTicksLength)).toFloat(),
//                        (oval.centerY() - Math.sin(angle / 180 * Math.PI) * (radius + minorTicksLength)).toFloat(),
//                        ticksPaint
//                    )
//                }
//                if (labelConverter != null) {
//                    canvas.save()
//                    canvas.rotate(180 + currentAngle, oval.centerX(), oval.centerY())
//                    val txtX = oval.centerX() + radius + majorTicksLength / 2 + 8
//                    val txtY = oval.centerY()
//                    canvas.rotate(+90, txtX, txtY)
//                    canvas.drawText(
//                        labelConverter!!.getLabelFor(curProgress, maxSpeed),
//                        txtX,
//                        txtY,
//                        txtPaint
//                    )
//                    canvas.restore()
//                }
//                currentAngle += majorStep
//                curProgress += majorTickStep
//            }
//            val smallOval: RectF = getOval(canvas, 0.7f)
//            colorLinePaint.setColor(defaultColor)
//            canvas.drawArc(smallOval, 185, 170, false, colorLinePaint)
//            for (range in ranges) {
//                colorLinePaint.setColor(range.color)
//                canvas.drawArc(
//                    smallOval,
//                    (190 + range.begin / maxSpeed * 160).toFloat(),
//                    ((range.end - range.begin) / maxSpeed * 160).toFloat(), false, colorLinePaint
//                )
//            }
//        }
//
//        private fun getOval(canvas: Canvas, factor: Float): RectF {
//            val oval: RectF
//            val canvasWidth: Int = canvas.getWidth() - getPaddingLeft() - getPaddingRight()
//            val canvasHeight: Int = canvas.getHeight() - getPaddingTop() - getPaddingBottom()
//            oval = if (canvasHeight * 2 >= canvasWidth) {
//                RectF(0, 0, canvasWidth * factor, canvasWidth * factor)
//            } else {
//                RectF(0, 0, canvasHeight * 2 * factor, canvasHeight * 2 * factor)
//            }
//            oval.offset(
//                (canvasWidth - oval.width()) / 2 + getPaddingLeft(),
//                (canvasHeight * 2 - oval.height()) / 2 + getPaddingTop()
//            )
//            return oval
//        }
//
//        private fun getOval(w: Float, h: Float): RectF {
//            val oval: RectF
//            val canvasWidth: Float = w - getPaddingLeft() - getPaddingRight()
//            val canvasHeight: Float = h - getPaddingTop() - getPaddingBottom()
//            oval = if (canvasHeight * 2 >= canvasWidth) {
//                RectF(0, 0, canvasWidth, canvasWidth)
//            } else {
//                RectF(0, 0, canvasHeight * 2, canvasHeight * 2)
//            }
//            return oval
//        }
//
//        private fun drawBackground(canvas: Canvas) {
//            val oval: RectF = getOval(canvas, 1f)
//            canvas.drawArc(oval, 180, 180, true, backgroundPaint)
//            val innerOval: RectF = getOval(canvas, 0.9f)
//            canvas.drawArc(innerOval, 180, 180, true, backgroundInnerPaint)
//            val mask = Bitmap.createScaledBitmap(
//                mMask!!,
//                (oval.width() * 1.1).toInt(), (oval.height() * 1.1).toInt() / 2, true
//            )
//            canvas.drawBitmap(
//                mask,
//                oval.centerX() - oval.width() * 1.1f / 2,
//                oval.centerY() - oval.width() * 1.1f / 2,
//                maskPaint
//            )
//        }
//
//        private fun init() {
//            if (Build.VERSION.SDK_INT >= 11 && !isInEditMode()) {
//                setLayerType(View.LAYER_TYPE_HARDWARE, null)
//            }
//            backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            backgroundPaint.setStyle(Paint.Style.FILL)
//            backgroundPaint.setColor(Color.rgb(127, 127, 127))
//            backgroundInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            backgroundInnerPaint.setStyle(Paint.Style.FILL)
//            backgroundInnerPaint.setColor(Color.rgb(150, 150, 150))
//            txtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            txtPaint.setColor(Color.WHITE)
//            txtPaint.setTextSize(18)
//            txtPaint.setTextAlign(Paint.Align.CENTER)
//            mMask = BitmapFactory.decodeResource(getResources(), R.drawable.spot_mask)
//            mMask = Bitmap.createBitmap(mMask, 0, 0, mMask.getWidth(), mMask.getHeight() / 2)
//            maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            maskPaint.setDither(true)
//            ticksPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            ticksPaint.setStrokeWidth(3.0f)
//            ticksPaint.setStyle(Paint.Style.STROKE)
//            ticksPaint.setColor(defaultColor)
//            colorLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            colorLinePaint.setStyle(Paint.Style.STROKE)
//            colorLinePaint.setStrokeWidth(5)
//            colorLinePaint.setColor(defaultColor)
//            needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            needlePaint.setStrokeWidth(5)
//            needlePaint.setStyle(Paint.Style.STROKE)
//            needlePaint.setColor(Color.argb(200, 255, 0, 0))
//        }
//
//        interface LabelConverter {
//            fun getLabelFor(progress: Double, maxProgress: Double): String?
//        }
//
//        class ColoredRange(var color: Int, var begin: Double, var end: Double)
//        companion object {
//            private val TAG = Speed::class.java.simpleName
//            const val DEFAULT_MAX_SPEED = 100.0
//            const val DEFAULT_MAJOR_TICK_STEP = 20.0
//            const val DEFAULT_MINOR_TICKS = 1
//        }
//    }
//class Sss : View() {
//    var cx: Context? = null
//    var width = 0f
//    var height = 0f
//    var center_x = 0f
//    var center_y = 0f
//    val oval = RectF()
//    val touchArea = RectF()
//    var sweep = 0f
//    var left = 0f
//    var right = 0f
//    var percentage = 0
//
//    fun ArcProgress(context: Context?) {
//        super(context)
//        cx = context
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        setBackgroundColor(-0xf1422)
//        width = getWidth().toFloat()
//        height = getHeight().toFloat()
//        val radius: Float
//        radius = if (width > height) {
//            height / 3
//        } else {
//            width / 3
//        }
//        val paint = Paint()
//        paint.isAntiAlias = true
//        paint.color = -0x2d374a
//        paint.setStrokeWidth(35)
//        paint.style = Paint.Style.STROKE
//        center_x = width / 2
//        center_y = height / 2
//        left = center_x - radius
//        val top = center_y - radius
//        right = center_x + radius
//        val bottom = center_y + radius
//        oval[left, top, right] = bottom
//
//        //this is the background arc, it remains constant
//        canvas.drawArc(oval, 180F, 180F, false, paint)
//        paint.setStrokeWidth(10F)
//        paint.color = -0x1fadb3
//        //this is the red arc whichhas its sweep argument manipulated by on touch
//        canvas.drawArc(oval, 180F, sweep, false, paint)
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_MOVE) {
//            val xPosition = event.x
//            val yPosition = event.y
//            if (oval.contains(xPosition, yPosition)) {
//                val x = xPosition - left
//                val s = x * 100
//                val b = s / oval.width()
//                percentage = b.roundToInt()
//                sweep = 180 / 100.0f * percentage.toFloat()
//                invalidate()
//            } else {
//                if (xPosition < left) {
//                    percentage = 0
//                    sweep = 180 / 100.0f * percentage.toFloat()
//                    invalidate()
//                }
//                if (xPosition > right) {
//                    percentage = 100
//                    sweep = 180 / 100.0f * percentage.toFloat()
//                    invalidate()
//                }
//            }
//        }
//        return true
//    }
//}
//
