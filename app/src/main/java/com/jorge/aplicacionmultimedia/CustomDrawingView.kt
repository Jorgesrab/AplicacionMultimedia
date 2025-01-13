package com.jorge.aplicacionmultimedia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.TranslateAnimation
import kotlin.concurrent.fixedRateTimer

class CustomDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = 8f
    }

    private var currentColorIndex = 0
    private val colors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN
    )

    private var animationOffset = 0f

    init {
        // Configurar una animación de traslación lateral para el grupo de figuras
        val translateAnimation = TranslateAnimation(0f, 100f, 0f, 0f).apply {
            duration = 1000 // Duración de 1 segundo
            repeatCount = TranslateAnimation.INFINITE
            repeatMode = TranslateAnimation.REVERSE
        }
        startAnimation(translateAnimation)

        // Cambiar el color de las figuras cada segundo
        fixedRateTimer("colorTimer", initialDelay = 0, period = 1000) {
            currentColorIndex = (currentColorIndex + 1) % colors.size
            postInvalidate() // Redibujar la vista
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Configurar el Paint para dibujar figuras
        paint.textSize = 40f

        // Dibujar el rectángulo
        paint.color = colors[currentColorIndex]
        canvas.drawRect(100f + animationOffset, 100f, 400f + animationOffset, 300f, paint)

        // Dibujar el círculo
        paint.color = colors[(currentColorIndex + 1) % colors.size]
        canvas.drawCircle(600f + animationOffset, 200f, 100f, paint)

        // Dibujar la línea
        paint.color = colors[(currentColorIndex + 2) % colors.size]
        canvas.drawLine(100f + animationOffset, 400f, 600f + animationOffset, 400f, paint)
    }
}
