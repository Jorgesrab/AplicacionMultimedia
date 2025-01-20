package com.jorge.aplicacionmultimedia

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class SoundActivity : AppCompatActivity() {

    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var isPlaying1 = false
    private var isPlaying2 = false
    private var progressJob: Job? = null // Job para la coroutine de progreso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_activity)

        val btnPlaySound1 = findViewById<ImageButton>(R.id.btnPlaySound1)
        val btnPlaySound2 = findViewById<ImageButton>(R.id.btnPlaySound2)
        val seekBarVolume = findViewById<SeekBar>(R.id.seekBarVolume)
        val seekBarProgress = findViewById<SeekBar>(R.id.seekBarProgress)

        // Cargar GIF inicial en los botones (play)
        Glide.with(this).asGif().load(R.raw.play).into(btnPlaySound1)
        Glide.with(this).asGif().load(R.raw.play).into(btnPlaySound2)

        // Configurar AudioManager para ajustar el volumen
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        seekBarVolume.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBarVolume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Manejar clic en el botón del primer sonido
        btnPlaySound1.setOnClickListener {
            if (isPlaying2) {
                stopSound(mediaPlayer2, btnPlaySound2)
                isPlaying2 = false
            }
            if (isPlaying1) {
                pauseSound(mediaPlayer1)
                Glide.with(this).asGif().load(R.raw.play).into(btnPlaySound1)
            } else {
                if (mediaPlayer1 == null) {
                    playSound(R.raw.sound1, seekBarProgress) { mediaPlayer ->
                        mediaPlayer1 = mediaPlayer
                    }
                } else {
                    mediaPlayer1?.start()
                    startProgressUpdater(mediaPlayer1!!, seekBarProgress)
                }
                Glide.with(this).asGif().load(R.raw.pause).into(btnPlaySound1)
            }
            isPlaying1 = !isPlaying1
        }

        // Manejar clic en el botón del segundo sonido
        btnPlaySound2.setOnClickListener {
            if (isPlaying1) {
                stopSound(mediaPlayer1, btnPlaySound1)
                isPlaying1 = false
            }
            if (isPlaying2) {
                pauseSound(mediaPlayer2)
                Glide.with(this).asGif().load(R.raw.play).into(btnPlaySound2)
            } else {
                if (mediaPlayer2 == null) {
                    playSound(R.raw.sound2, seekBarProgress) { mediaPlayer ->
                        mediaPlayer2 = mediaPlayer
                    }
                } else {
                    mediaPlayer2?.start()
                    startProgressUpdater(mediaPlayer2!!, seekBarProgress)
                }
                Glide.with(this).asGif().load(R.raw.pause).into(btnPlaySound2)
            }
            isPlaying2 = !isPlaying2
        }
    }

    private fun playSound(soundResId: Int, seekBarProgress: SeekBar, onPrepared: (MediaPlayer) -> Unit) {
        // Crea un MediaPlayer a partir del recurso de sonido proporcionado
        val mediaPlayer = MediaPlayer.create(this, soundResId)

        // Configura un listener para cuando el MediaPlayer esté preparado
        mediaPlayer.setOnPreparedListener {
            seekBarProgress.max = mediaPlayer.duration // Establece la duración máxima de la SeekBar
            mediaPlayer.start() // Inicia la reproducción de audio
            startProgressUpdater(mediaPlayer, seekBarProgress) // Inicia la actualización de la barra de progreso
            onPrepared(mediaPlayer) // Llama a la función proporcionada como parámetro
        }

        // Configura un listener para cuando la reproducción haya finalizado
        mediaPlayer.setOnCompletionListener {
            stopProgressUpdater() // Detiene la actualización de la barra de progreso
            seekBarProgress.progress = 0 // Reinicia la barra de progreso a 0
        }
    }

    // Pausa la reproducción del audio si el MediaPlayer no es nulo
    private fun pauseSound(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.pause()
    }

    private fun stopSound(mediaPlayer: MediaPlayer?, button: ImageButton) {
        mediaPlayer?.pause() // Pausa la reproducción
        mediaPlayer?.seekTo(0) // Reinicia la reproducción al inicio
        Glide.with(this).asGif().load(R.raw.play).into(button) // Cambia el icono del botón a "play"
        stopProgressUpdater() // Detiene la actualización de la barra de progreso
    }

    private fun startProgressUpdater(mediaPlayer: MediaPlayer, seekBarProgress: SeekBar) {
        progressJob?.cancel() // Cancela cualquier coroutine anterior para evitar múltiples ejecuciones

        // Inicia una nueva coroutine para actualizar la barra de progreso mientras el audio se reproduce
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer.isPlaying) {
                seekBarProgress.progress = mediaPlayer.currentPosition // Actualiza la barra de progreso con la posición actual
                delay(500) // Espera 500 ms antes de actualizar nuevamente
            }
        }

        // Maneja los cambios en la barra de progreso cuando el usuario la manipula manualmente
        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) { // Si el usuario mueve la barra, actualiza la posición del audio
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // No se hace nada al comenzar a tocar la barra
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}  // No se hace nada al dejar de tocar la barra
        })
    }

    // Detiene la coroutine que actualiza la barra de progreso
    private fun stopProgressUpdater() {
        progressJob?.cancel()
    }

    // Se ejecuta cuando la actividad es destruida
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1?.release() // Libera los recursos del primer MediaPlayer
        mediaPlayer2?.release() // Libera los recursos del segundo MediaPlayer
        stopProgressUpdater() // Detiene la actualización de la barra de progreso
    }

}
