package pony.tothemoon.battletimer

import android.app.Application
import android.os.Build
import pony.tothemoon.battletimer.utils.AndroidUtils
import pony.tothemoon.battletimer.utils.NotificationUtils

class BattleTimerApp : Application() {
  override fun onCreate() {
    super.onCreate()

    AndroidUtils.initialize(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationUtils.createNotificationChannel(this)
    }
  }
}