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

    // Paint para dibujar las figuras, configurado con relleno y un grosor de línea de 8px
    private val paint = Paint().apply {
        style = Paint.Style.FILL // Estilo de relleno completo para las figuras
        strokeWidth = 8f // Grosor de las líneas
    }

    // Índice del color actual usado en las figuras
    private var currentColorIndex = 0

    // Lista de colores que se usan para cambiar el color de las figuras
    private val colors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN
    )

    // Offset de la animación lateral para desplazar las figuras
    private var animationOffset = 0f

    init {
        // Configurar una animación de traslación lateral (desplazamiento) para el grupo de figuras
        val translateAnimation = TranslateAnimation(0f, 100f, 0f, 0f).apply {
            duration = 1000 // Duración de 1 segundo para cada ciclo de la animación
            repeatCount = TranslateAnimation.INFINITE // La animación se repite indefinidamente
            repeatMode = TranslateAnimation.REVERSE // La animación invierte su dirección en cada ciclo
        }
        startAnimation(translateAnimation) // Iniciar la animación

        // Configurar un temporizador que cambia el color de las figuras cada segundo
        fixedRateTimer("colorTimer", initialDelay = 0, period = 1000) {
            currentColorIndex = (currentColorIndex + 1) % colors.size // Actualizar el índice del color
            postInvalidate() // Redibujar la vista con el nuevo color
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        // Dibujar el rectángulo con el color actual
        paint.color = colors[currentColorIndex]
        canvas.drawRect(100f + animationOffset, 100f, 400f + animationOffset, 300f, paint)

        // Dibujar el círculo con el siguiente color en la lista
        paint.color = colors[(currentColorIndex + 1) % colors.size]
        canvas.drawCircle(600f + animationOffset, 200f, 100f, paint)

        // Dibujar la línea con el segundo siguiente color en la lista
        paint.color = colors[(currentColorIndex + 2) % colors.size]
        canvas.drawLine(100f + animationOffset, 400f, 600f + animationOffset, 400f, paint)
    }
}
