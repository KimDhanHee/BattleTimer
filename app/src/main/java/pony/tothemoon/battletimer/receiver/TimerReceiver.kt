package pony.tothemoon.battletimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import pony.tothemoon.battletimer.service.TimerService
import pony.tothemoon.battletimer.utils.AlarmUtils
import pony.tothemoon.battletimer.utils.WakeLockManager

class TimerReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != AlarmUtils.ACTION_TIME_OUT) return

    WakeLockManager.acquireCpuWakeLock(context)

    intent.apply {
      setClass(context, TimerService::class.java)
    }.startService(context)
  }
}

private fun Intent.startService(context: Context) = when {
  Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> context.startForegroundService(this)
  else -> context.startService(this)
}