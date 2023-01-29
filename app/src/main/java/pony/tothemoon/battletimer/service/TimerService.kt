package pony.tothemoon.battletimer.service

import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.utils.AlarmUtils
import pony.tothemoon.battletimer.utils.AndroidUtils
import pony.tothemoon.battletimer.utils.MediaVibrator
import pony.tothemoon.battletimer.utils.NotificationUtils

class TimerService : LifecycleService() {
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action != AlarmUtils.ACTION_TIME_OUT)
      return super.onStartCommand(intent, flags, startId)

    val timerInfo = intent.timerInfo ?: return super.onStartCommand(intent, flags, startId)

    displayNotification(timerInfo)

    if (AndroidUtils.isBackground) {
      alertTimeout()
    }

    return super.onStartCommand(intent, flags, startId)
  }

  private val Intent.timerInfo: TimerInfo?
    get() = when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        this.getParcelableExtra(AlarmUtils.BUNDLE_KEY_TIMER_INFO, TimerInfo::class.java)
      else -> this.getParcelableExtra(AlarmUtils.BUNDLE_KEY_TIMER_INFO)
    }

  private fun displayNotification(timerInfo: TimerInfo) {
    val subTitle = AndroidUtils.string(R.string.timer_noti_end_sub_title)

    NotificationUtils.notify(this, timerInfo.id, timerInfo.title, subTitle)

    val needToStartForeground = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    if (needToStartForeground) {
      startForeground(
        timerInfo.id,
        NotificationUtils.buildNotification(this, timerInfo.title, subTitle)
      )
    }
  }

  private fun alertTimeout() {
    lifecycleScope.launch {
      while (AndroidUtils.isBackground) {
        MediaVibrator.vibrateOnce(this@TimerService)
        delay(10 * TimerInfo.SECONDS_UNIT)
      }
    }
  }
}