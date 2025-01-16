package com.jorge.aplicacionmultimedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnRecordVideo: ImageButton
    private lateinit var btnPlayPauseVideo: ImageButton
    private var videoUri: Uri? = null
    private var isPlaying = false

    // Reemplazo de startActivityForResult
    private lateinit var videoCaptureLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)

        // Inicializar vistas
        btnRecordVideo = findViewById(R.id.btnRecordVideo)
        btnPlayPauseVideo = findViewById(R.id.btnPlayPauseVideo)
        videoView = findViewById(R.id.videoView)

        // Configurar el launcher para grabar video
        videoCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                videoUri = result.data?.data
                videoUri?.let {
                    isPlaying = false
                    Glide.with(this).asGif().load(R.raw.play).into(btnPlayPauseVideo)

                    videoView.setVideoURI(it) // Asignar el URI al VideoView
                    videoView.setOnPreparedListener { mediaPlayer ->
                        videoView.seekTo(1) // Mostrar el primer frame
                    }
                }
            }
        }

        // Configurar botones
        Glide.with(this).asGif().load(R.raw.record).into(btnRecordVideo)
        Glide.with(this).asGif().load(R.raw.play).into(btnPlayPauseVideo)

        // Botón para grabar video
        btnRecordVideo.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)
            videoCaptureLauncher.launch(intent)
        }

        // Botón para reproducir o pausar video
        btnPlayPauseVideo.setOnClickListener {
            if (videoUri != null) {
                if (isPlaying) {
                    videoView.pause()
                    isPlaying = false
                    Glide.with(this).asGif().load(R.raw.play).into(btnPlayPauseVideo)
                } else {
                    videoView.start()
                    isPlaying = true
                    Glide.with(this).asGif().load(R.raw.pause).into(btnPlayPauseVideo)
                }
            }
        }
    }
}
