package pony.tothemoon.battletimer

import android.app.Application
import pony.tothemoon.battletimer.utils.AndroidUtils

class BattleTimerApp : Application() {
  override fun onCreate() {
    super.onCreate()

    AndroidUtils.initialize(this)
  }
}