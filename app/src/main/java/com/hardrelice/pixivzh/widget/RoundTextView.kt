package com.hardrelice.pixivzh.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.hardrelice.pixivzh.R


/**
 * 支持圆角的TextView
 * Created by stephen on 2017/12/18.
 */
class RoundTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    fun setBackgroungColor(@ColorInt color: Int) {
        val myGrad = background as GradientDrawable
        myGrad.setColor(color)
    }

    init {
        val attributes: TypedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.RoundTextView, defStyleAttr, 0)
        val borderWidth =
            attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtv_border_width, 0)
        val borderColor =
            attributes.getColor(R.styleable.RoundTextView_rtv_border_color, Color.BLACK)
        val borderRadius = attributes.getDimension(R.styleable.RoundTextView_rtv_border_radius, 0f)
        val backgroundColor = attributes.getColor(R.styleable.RoundTextView_rtv_background, Color.WHITE)

        val borderTopLeftRadius = attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtv_border_top_left_radius,0).toFloat()
        val borderTopRightRadius = attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtv_border_top_right_radius,0).toFloat()
        val borderBottomLeftRadius = attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtv_border_bottom_left_radius,0).toFloat()
        val borderBottomRightRadius = attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtv_border_bottom_right_radius,0).toFloat()

        attributes.recycle()

        val gd = GradientDrawable() //创建drawable
        gd.setColor(backgroundColor)
//        gd.cornerRadius = borderRadius
        gd.cornerRadii = floatArrayOf(
            borderTopLeftRadius,
            borderTopLeftRadius,
            borderTopRightRadius,
            borderTopRightRadius,
            borderBottomRightRadius,
            borderBottomRightRadius,
            borderBottomLeftRadius,
            borderBottomLeftRadius
        )
        if (borderWidth > 0) {
            gd.setStroke(borderWidth, borderColor)
        }
        this.background = gd
    }
}