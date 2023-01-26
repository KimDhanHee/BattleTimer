package pony.tothemoon.battletimer.service

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.utils.AlarmUtils
import pony.tothemoon.battletimer.utils.NotificationUtils
import pony.tothemoon.battletimer.utils.WakeLockManager
import pony.tothemoon.media.MediaPlayer

class TimerService : LifecycleService() {
  private val player by lazy {
    MediaPlayer(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action != AlarmUtils.ACTION_TIME_OUT)
      return super.onStartCommand(intent, flags, startId)

    val timerInfo = intent.timerInfo ?: return super.onStartCommand(intent, flags, startId)

    displayNotification(timerInfo)

    alertTimeout()

    return super.onStartCommand(intent, flags, startId)
  }

  private val Intent.timerInfo: TimerInfo?
    get() = when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        this.getParcelableExtra(AlarmUtils.BUNDLE_KEY_TIMER_INFO, TimerInfo::class.java)
      else -> this.getParcelableExtra(AlarmUtils.BUNDLE_KEY_TIMER_INFO)
    }

  private fun displayNotification(timerInfo: TimerInfo) {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startForeground(
        timerInfo.id,
        NotificationUtils.buildTimerTimeoutNotification(this, timerInfo.title)
      )
      else -> NotificationUtils.notifyTimerTimeout(this, timerInfo)
    }
  }

  private fun alertTimeout() {
    lifecycleScope.launch {
      if (player.isPlaying()) return@launch

      val ringtone = RingtoneManager.getActualDefaultRingtoneUri(
        this@TimerService,
        RingtoneManager.TYPE_ALARM
      )
      val audioManager = this@TimerService.getSystemService(Context.AUDIO_SERVICE) as AudioManager
      val volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2

      player.play(ringtone, volume)
    }
  }

  override fun onDestroy() {
    WakeLockManager.release()
    CoroutineScope(Dispatchers.IO).launch {
      player.release()
    }
    stopForeground(STOP_FOREGROUND_DETACH)
    super.onDestroy()
  }
}