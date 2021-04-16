package com.hardrelice.pixivzh.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundImageView(context: Context): AppCompatImageView(context) {
    private val path = Path()

    constructor(context: Context, attributeSet: AttributeSet? = null, defAttrStyle: Int = 0) : this(context)

    override fun onDraw(canvas: Canvas?) {
        //四个圆角
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = 12f
        path.moveTo(radius, 0f)
        path.lineTo(width - radius, 0f)
        path.quadTo(width, 0f, width.toFloat(), radius)
        path.lineTo(width, (height - radius).toFloat())
        path.quadTo(width, height, width - radius, height)
        path.lineTo(radius, height)
        path.quadTo(0f, height, 0f, height - radius)
        path.lineTo(0f, radius)
        path.quadTo(0f, 0f, radius, 0f)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }

}