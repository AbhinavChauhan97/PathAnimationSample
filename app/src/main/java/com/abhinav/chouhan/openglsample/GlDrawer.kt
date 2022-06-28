package com.abhinav.chouhan.openglsample

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.random.Random

class GlDrawer(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private val planePath = Path()

    private val random = Random(System.currentTimeMillis())
    private val pathMeasure = PathMeasure()
    private var distance = 0f
    private var tan = floatArrayOf(0f,0f)
    private var pos = floatArrayOf(0f, 0f)
    private var plane: ImageView


    private val planePathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
    }
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    init {
        setWillNotDraw(false)
        plane = ImageView(context).apply {
            layoutParams = LayoutParams(70,50)
            setImageResource(R.drawable.jet1)
        }
        addView(plane)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initPlanePath()
        pathMeasure.setPath(planePath, false)
        flyPlane()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            drawPath(planePath, planePathPaint)
            val planeX = pos[0]
            val planeY = pos[1]
            plane.x = planeX
            plane.y = planeY
            val degrees = atan2(tan[1], tan[0]) * 180.0 / Math.PI
            plane.rotation = degrees.toFloat() - 180
        }
    }



    private fun initPlanePath() {
        planePath.reset()
        var startX = randomX()
        var startY = randomY()
        repeat(4) {
            planePath.moveTo(startX, startY)
            val controlX = randomX()
            val controlY = randomY()
            val endX = randomX()
            val endY = randomY()
            planePath.quadTo(controlX, controlY, endX, endY)
            startX = endX
            startY = endY
        }
    }

    private fun flyPlane() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = (pathMeasure.length * 5).toLong()
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            distance = it.animatedValue as Float
            pathMeasure.getPosTan(distance * pathMeasure.length, pos, tan)
            invalidate()
        }
        valueAnimator.doOnEnd {
            if (pathMeasure.nextContour()) {
                flyPlane()
            }
        }
        valueAnimator.start()
    }

    private fun randomX() = random.nextInt(0, width).toFloat()
    private fun randomY() = random.nextInt(0, height).toFloat()


}

