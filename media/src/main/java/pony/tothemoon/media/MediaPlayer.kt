package pony.tothemoon.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaPlayer(
  private val context: Context,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
  private val player by lazy {
    ExoPlayer.Builder(context)
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
          .setUsage(C.USAGE_MEDIA)
          .build(),
        true
      )
      .build()
      .apply {
        repeatMode = Player.REPEAT_MODE_ONE
      }
  }

  private var originVolume = 0

  suspend fun play(uri: Uri, volume: Int) {
    withContext(dispatcher) {
      player.apply {
        originVolume = deviceVolume
        deviceVolume = volume
        setMediaItem(MediaItem.fromUri(uri))
        prepare()
      }.play()
    }
  }

  suspend fun isPlaying(): Boolean = withContext(dispatcher) {
    player.isPlaying
  }

  suspend fun stop() {
    withContext(dispatcher) {
      player.deviceVolume = originVolume
      player.stop()
    }
  }

  suspend fun release() {
    withContext(dispatcher) {
      player.release()
    }
  }
}