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
        val mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer.setOnPreparedListener {
            seekBarProgress.max = mediaPlayer.duration
            mediaPlayer.start()
            startProgressUpdater(mediaPlayer, seekBarProgress)
            onPrepared(mediaPlayer)
        }

        mediaPlayer.setOnCompletionListener {
            stopProgressUpdater()
            seekBarProgress.progress = 0
        }
    }

    private fun pauseSound(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.pause()
    }

    private fun stopSound(mediaPlayer: MediaPlayer?, button: ImageButton) {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
        Glide.with(this).asGif().load(R.raw.play).into(button)
        stopProgressUpdater()
    }

    private fun startProgressUpdater(mediaPlayer: MediaPlayer, seekBarProgress: SeekBar) {
        progressJob?.cancel() // Cancelar cualquier coroutine previa
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer.isPlaying) {
                seekBarProgress.progress = mediaPlayer.currentPosition
                delay(500) // Esperar 500 ms antes de actualizar nuevamente
            }
        }

        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun stopProgressUpdater() {
        progressJob?.cancel() // Cancelar la coroutine de progreso
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        stopProgressUpdater()
    }
}
