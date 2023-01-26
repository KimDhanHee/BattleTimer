package pony.tothemoon.battletimer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pony.tothemoon.battletimer.BuildConfig
import pony.tothemoon.battletimer.MainActivity
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.receiver.TimerReceiver

object AlarmUtils {
  const val ACTION_TIME_OUT = "${BuildConfig.APPLICATION_ID}.TIME_OUT"
  const val BUNDLE_KEY_TIMER_INFO = "timer_info"

  private val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

  fun setAlarm(context: Context, timerInfo: TimerInfo) {
    val requestCode = 100

    val operation = PendingIntent.getBroadcast(
      context,
      timerInfo.id,
      Intent(context, TimerReceiver::class.java).apply {
        action = ACTION_TIME_OUT
        flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES or
          Intent.FLAG_RECEIVER_FOREGROUND or
          Intent.FLAG_RECEIVER_REPLACE_PENDING
        putExtra(BUNDLE_KEY_TIMER_INFO, timerInfo)
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    context.alarmManager.setAlarmClock(
      AlarmManager.AlarmClockInfo(
        System.currentTimeMillis() + timerInfo.remainedTime,
        PendingIntent.getActivity(
          context,
          requestCode,
          Intent(context, MainActivity::class.java),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      ),
      operation
    )
  }

  fun cancelAlarm(context: Context, timerId: Int) {
    context.alarmManager.cancel(
      PendingIntent.getBroadcast(
        context,
        timerId,
        Intent(context, TimerReceiver::class.java).apply {
          action = ACTION_TIME_OUT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    )
  }
}