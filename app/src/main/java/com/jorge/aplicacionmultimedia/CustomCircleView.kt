package com.jorge.aplicacionmultimedia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.MAGENTA
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar el círculo
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 2f
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Guardar el estado del canvas y rotar el texto
        canvas.save()
        canvas.rotate(-30f, centerX, centerY) // Rotar el texto 30 grados hacia la izquierda

        // Dibujar el texto "Jorge" inclinado dentro del círculo
        canvas.drawText("Jorge", centerX, centerY + (textPaint.textSize / 3), textPaint)

        // Restaurar el estado original del canvas
        canvas.restore()
    }
}
