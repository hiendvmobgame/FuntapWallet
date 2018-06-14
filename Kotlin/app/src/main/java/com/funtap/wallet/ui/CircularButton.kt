package com.funtap.wallet.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

import com.funtap.wallet.R

/**
 * Created by Alejandro on 06/06/14.
 */

/**
 * A Google Plus like, circular button for Android.
 * See https://github.com/Alexrs95/CircularButton
 */
class CircularButton : android.support.v7.widget.AppCompatButton {

    private var mButtonPaint: Paint? = null
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    private var btnRadius: Int = 0
    private var buttonColor = Color.WHITE
    private var shadowColor = Color.GRAY

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun init(context: Context, attrs: AttributeSet?) {
        //        setScaleType(ScaleType.CENTER_INSIDE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        mButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mButtonPaint!!.style = Paint.Style.FILL

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircularButton)
            buttonColor = a.getColor(R.styleable.CircularButton_buttonColor, buttonColor)
            shadowColor = a.getColor(R.styleable.CircularButton_shadowColor, shadowColor)
            a.recycle()
        }
        setButtonColor(buttonColor)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, btnRadius - btnRadius * SHADOW_CONSTANT, mButtonPaint!!)
        super.onDraw(canvas)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
        btnRadius = Math.min(width, height) / 2

        //the shadow color is settled here because its dimension depends on the radius of the button
        setShadowColor(shadowColor)
    }


    fun setButtonColor(color: Int) {
        this.buttonColor = color
        mButtonPaint!!.color = buttonColor
        invalidate()
    }

    fun setShadowColor(color: Int) {
        this.shadowColor = color
        mButtonPaint!!.setShadowLayer(btnRadius * SHADOW_CONSTANT, 0f, 0f, shadowColor)
        invalidate()
    }

    fun getButtonColor(): Int {
        return buttonColor
    }

    override fun getShadowColor(): Int {
        return shadowColor
    }

    companion object {

        /**
         * The dimension of the shadow is a 15% of the radius of the button
         */
        private val SHADOW_CONSTANT = 0.15f
    }

}