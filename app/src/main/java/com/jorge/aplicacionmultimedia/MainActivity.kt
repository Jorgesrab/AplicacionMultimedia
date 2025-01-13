package com.jorge.aplicacionmultimedia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ocultar solo la barra de navegación (modo inmersivo) en Android 11 y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars()) // Oculta solo la barra de navegación
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Implementación para versiones anteriores a Android 11
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }


        // Cargar GIFs en los ImageViews
        Glide.with(this).asGif().load(R.raw.drawing).into(findViewById(R.id.ivGif1))
        Glide.with(this).asGif().load(R.raw.sound_wave).into(findViewById(R.id.ivGif2))
        Glide.with(this).asGif().load(R.raw.video_channel).into(findViewById(R.id.ivGif3))

        // Configurar navegación entre actividades
        findViewById<TextView>(R.id.tvOption1).setOnClickListener {
            startActivity(Intent(this, DrawingActivity::class.java))
        }
        findViewById<TextView>(R.id.tvOption2).setOnClickListener {
            startActivity(Intent(this, SoundActivity::class.java))
        }
        findViewById<TextView>(R.id.tvOption3).setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }
    }
}
