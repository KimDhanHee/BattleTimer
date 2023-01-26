package pony.tothemoon.battletimer.utils

import android.content.Context
import android.os.PowerManager
import pony.tothemoon.battletimer.model.TimerInfo

object WakeLockManager {
  private const val CPU_WAKE_LOCK_MILLIS = 10 * TimerInfo.MINUTE_UNIT

  private const val TAG = "Pony:WakeLockManager"
  private var wakeLock: PowerManager.WakeLock? = null

  fun acquireCpuWakeLock(context: Context) {
    release()

    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    wakeLock = powerManager.newWakeLock(
      PowerManager.PARTIAL_WAKE_LOCK,
      TAG
    )
    wakeLock?.acquire(CPU_WAKE_LOCK_MILLIS)
  }

  fun release() {
    wakeLock?.release()
    wakeLock = null
  }
}