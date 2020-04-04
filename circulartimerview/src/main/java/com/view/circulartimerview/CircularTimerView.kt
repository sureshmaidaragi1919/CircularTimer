package com.view.circulartimerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

class CircularTimerView : View {
    private var progressBarPaint: Paint? = null
    private var progressBarBackgroundPaint: Paint? = null
    private var backgroundPaint: Paint? = null
    private var textPaint: Paint? = null
    private var mRadius = 0f
    private val mArcBounds = RectF()
    var drawUpto = 0f

    constructor(context: Context?) : super(context) { // create the Paint and set its color
    }

    private var progressColor = 0
    private var progressBackgroundColor = 0
    private var backgroundColor = 0
    private var strokeWidthDimension = 0f
    private var backgroundWidth = 0f
    private var roundedCorners = false
    private var maxValue = 0f
    private var progressTextColor = Color.BLACK
    private var textSize = 18f
    private var text: String? = ""
    private var suffix: String? = ""
    private var prefix: String? = ""
    private var isClockwise = true
    private var startingAngle = 270
    var defStyleAttr = 0
    private var circularTimerListener: CircularTimerListener? = null
    private var countDownTimer: CountDownTimer? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.defStyleAttr = defStyleAttr
        initPaints(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initPaints(context, attrs)
    }

    private fun initPaints(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircularTimerView, defStyleAttr, 0)
        progressColor = ta.getColor(R.styleable.CircularTimerView_progressColor, Color.BLUE)
        backgroundColor = ta.getColor(R.styleable.CircularTimerView_backgroundColor, Color.GRAY)
        progressBackgroundColor = ta.getColor(R.styleable.CircularTimerView_progressBackgroundColor, Color.GRAY)
        strokeWidthDimension = ta.getFloat(R.styleable.CircularTimerView_strokeWidthDimension, 10f)
        backgroundWidth = ta.getFloat(R.styleable.CircularTimerView_backgroundWidth, 10f)
        roundedCorners = ta.getBoolean(R.styleable.CircularTimerView_roundedCorners, false)
        maxValue = ta.getFloat(R.styleable.CircularTimerView_maxValue, 100f)
        progressTextColor = ta.getColor(R.styleable.CircularTimerView_progressTextColor, Color.BLACK)
        textSize = ta.getDimension(R.styleable.CircularTimerView_textSize, 18f)
        suffix = ta.getString(R.styleable.CircularTimerView_suffix)
        prefix = ta.getString(R.styleable.CircularTimerView_prefix)
        text = ta.getString(R.styleable.CircularTimerView_progressText)
        isClockwise = ta.getBoolean(R.styleable.CircularTimerView_isClockwise, true)
        startingAngle = ta.getInt(R.styleable.CircularTimerView_startingPoint, 270)
        progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressBarPaint!!.style = Paint.Style.FILL
        progressBarPaint!!.color = progressColor
        progressBarPaint!!.style = Paint.Style.STROKE
        progressBarPaint!!.strokeWidth = strokeWidthDimension * resources.displayMetrics.density
        if (roundedCorners) {
            progressBarPaint!!.strokeCap = Paint.Cap.ROUND
        } else {
            progressBarPaint!!.strokeCap = Paint.Cap.BUTT
        }
        val pc = String.format("#%06X", 0xFFFFFF and progressColor)
        progressBarPaint!!.color = Color.parseColor(pc)
        progressBarBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressBarBackgroundPaint!!.style = Paint.Style.FILL
        progressBarBackgroundPaint!!.color = progressBackgroundColor
        progressBarBackgroundPaint!!.style = Paint.Style.STROKE
        progressBarBackgroundPaint!!.strokeWidth = backgroundWidth * resources.displayMetrics.density
        progressBarBackgroundPaint!!.strokeCap = Paint.Cap.SQUARE
        val bc = String.format("#%06X", 0xFFFFFF and progressBackgroundColor)
        progressBarBackgroundPaint!!.color = Color.parseColor(bc)
        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint!!.style = Paint.Style.FILL
        backgroundPaint!!.color = backgroundColor
        val bcfill = String.format("#%06X", 0xFFFFFF and backgroundColor)
        backgroundPaint!!.color = Color.parseColor(bcfill)
        ta.recycle()
        textPaint = TextPaint()
        textPaint?.setColor(progressTextColor)
        val c = String.format("#%06X", 0xFFFFFF and progressTextColor)
        textPaint?.setColor(Color.parseColor(c))
        textPaint?.setTextSize(textSize)
        textPaint?.setAntiAlias(true)
        //paint.setAntiAlias(true);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = Math.min(w, h) / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(w, h)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val mouthInset = mRadius / 3
        canvas.drawCircle(mRadius, mRadius, mouthInset * 2, backgroundPaint!!)
        mArcBounds[mouthInset, mouthInset, mRadius * 2 - mouthInset] = mRadius * 2 - mouthInset
        canvas.drawArc(mArcBounds, 0f, 360f, false, progressBarBackgroundPaint!!)
        if (isClockwise) {
            canvas.drawArc(mArcBounds, startingAngle.toFloat(), drawUpto / getMaxValue() * 360, false, progressBarPaint!!)
        } else {
            canvas.drawArc(mArcBounds, startingAngle.toFloat(), drawUpto / getMaxValue() * -360, false, progressBarPaint!!)
        }
        if (TextUtils.isEmpty(suffix)) {
            suffix = ""
        }
        if (TextUtils.isEmpty(prefix)) {
            prefix = ""
        }
        val drawnText = prefix + text + suffix
        if (!TextUtils.isEmpty(text)) {
            val textHeight = textPaint!!.descent() + textPaint!!.ascent()
            canvas.drawText(drawnText, (width - textPaint!!.measureText(drawnText)) / 2.0f, (width - textHeight) / 2.0f, textPaint!!)
        }
    }

    override fun onDetachedFromWindow() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        super.onDetachedFromWindow()
    }

    var progress: Float
        get() = drawUpto
        set(f) {
            drawUpto = f
            invalidate()
        }

    val progressPercentage: Float
        get() = drawUpto / getMaxValue() * 100

    fun setProgressColor(color: Int) {
        progressColor = color
        progressBarPaint!!.color = color
        invalidate()
    }

    fun setProgressColor(color: String?) {
        progressBarPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        backgroundPaint!!.color = color
        invalidate()
    }

    fun setBackgroundColor(color: String?) {
        backgroundPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    fun setProgressBackgroundColor(color: Int) {
        progressBackgroundColor = color
        progressBarBackgroundPaint!!.color = color
        invalidate()
    }

    fun setProgressBackgroundColor(color: String?) {
        progressBarBackgroundPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    fun getMaxValue(): Float {
        return maxValue
    }

    fun setMaxValue(max: Float) {
        maxValue = max
        invalidate()
    }

    fun setStrokeWidthDimension(width: Float) {
        strokeWidthDimension = width
        invalidate()
    }

    fun getStrokeWidthDimension(): Float {
        return strokeWidthDimension
    }

    fun setBackgroundWidth(width: Float) {
        backgroundWidth = width
        invalidate()
    }

    fun getBackgroundWidth(): Float {
        return backgroundWidth
    }

    fun setText(progressText: String?) {
        text = progressText
        invalidate()
    }

    fun getText(): String? {
        return text
    }

    fun setTextColor(color: String?) {
        textPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    var textColor: Int
        get() = progressTextColor
        set(color) {
            progressTextColor = color
            textPaint!!.color = color
            invalidate()
        }

    fun setSuffix(suffix: String?) {
        this.suffix = suffix
        invalidate()
    }

    fun getSuffix(): String? {
        return suffix
    }

    fun getPrefix(): String? {
        return prefix
    }

    fun setPrefix(prefix: String?) {
        this.prefix = prefix
        invalidate()
    }

    fun getClockwise(): Boolean {
        return isClockwise
    }

    fun setClockwise(clockwise: Boolean) {
        isClockwise = clockwise
        invalidate()
    }

    fun getStartingAngle(): Int {
        return startingAngle
    }

    /**
     * @param startingAngle 270 for Top
     * 0 for Right
     * 90 for Bottom
     * 180 for Left
     */
    fun setStartingAngle(startingAngle: Int) {
        this.startingAngle = startingAngle
        invalidate()
    }

    /**
     * Use this method to initialize Timer, default interval time is 1second, you can use other method to define interval
     *
     * @param circularTimerListener Pass your listener to listen ticks and provide data and to listen finish call
     * @param time                  time in long, e.g 1,2,3,4 or any long digit
     * @param timeFormatEnum        Format to define whether the given long time number is milli, second, minute, hour or day
     */
    fun setCircularTimerListener(circularTimerListener: CircularTimerListener, time: Long, timeFormatEnum: TimeFormatEnum?) {
        this.circularTimerListener = circularTimerListener
        var timeInMillis: Long = 0
        val intervalDuration: Long = 1000
        when (timeFormatEnum) {
            TimeFormatEnum.MILLIS -> timeInMillis = time
            TimeFormatEnum.SECONDS -> timeInMillis = time * 1000
            TimeFormatEnum.MINUTES -> timeInMillis = time * 1000 * 60
            TimeFormatEnum.HOUR -> timeInMillis = time * 1000 * 60 * 60
            TimeFormatEnum.DAY -> timeInMillis = time * 1000 * 60 * 60 * 24
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        val maxTime = timeInMillis
        countDownTimer = object : CountDownTimer(maxTime, intervalDuration) {
            override fun onTick(l: Long) {
                val percentTimeCompleted = (maxTime - l) / maxTime.toDouble()
                drawUpto = (maxValue * percentTimeCompleted).toFloat()
                text = circularTimerListener.updateDataOnTick(l)
                invalidate()
            }

            override fun onFinish() {
                val percentTimeCompleted = 1.0
                drawUpto = (maxValue * percentTimeCompleted).toFloat()
                circularTimerListener.onTimerFinished()
                invalidate()
            }
        }
    }

    /**
     * Use this method to initialize Timer, default interval time is 1second, you can use other method to define interval
     *
     * @param circularTimerListener Pass your listener to listen ticks and provide data and to listen finish call
     * @param time                  time in long, e.g 1,2,3,4 or any long digit
     * @param timeFormatEnum        Format to define whether the given long time number is milli, second, minute, hour or day
     */
    fun setCircularTimerListener(circularTimerListener: CircularTimerListener, time: Long, timeFormatEnum: TimeFormatEnum?, timeinterval: Long) {
        this.circularTimerListener = circularTimerListener
        var timeInMillis: Long = 0
        when (timeFormatEnum) {
            TimeFormatEnum.MILLIS -> timeInMillis = time
            TimeFormatEnum.SECONDS -> timeInMillis = time * 1000
            TimeFormatEnum.MINUTES -> timeInMillis = time * 1000 * 60
            TimeFormatEnum.HOUR -> timeInMillis = time * 1000 * 60 * 60
            TimeFormatEnum.DAY -> timeInMillis = time * 1000 * 60 * 60 * 24
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        val maxTime = timeInMillis
        countDownTimer = object : CountDownTimer(maxTime, timeinterval) {
            override fun onTick(l: Long) {
                val percentTimeCompleted = (maxTime - l) / maxTime.toDouble()
                drawUpto = (maxValue * percentTimeCompleted).toFloat()
                text = circularTimerListener.updateDataOnTick(l)
                invalidate()
            }

            override fun onFinish() {
                val percentTimeCompleted = 1.0
                drawUpto = (maxValue * percentTimeCompleted).toFloat()
                text = circularTimerListener.updateDataOnTick(0)
                circularTimerListener.onTimerFinished()
                invalidate()
            }
        }
    }

    fun startTimer(): Boolean {
        return if (countDownTimer == null) {
            false
        } else {
            countDownTimer!!.start()
            true
        }
    }

    fun stopTimer(): Boolean {
        return if (countDownTimer == null) {
            false
        } else {
            countDownTimer!!.cancel()
            true
        }
    }
}