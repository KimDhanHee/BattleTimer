package pony.tothemoon.battletimer.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pony.tothemoon.battletimer.MainActivity
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.model.TimerInfo

object NotificationUtils {
  private const val TIMER_ALARM_CHANNEL_ID = "timer_alarm_channel"

  private val Context.notificationManager: NotificationManagerCompat
    get() = NotificationManagerCompat.from(this)

  @RequiresApi(Build.VERSION_CODES.O)
  fun createNotificationChannel(context: Context) {
    context.notificationManager.createNotificationChannel(
      NotificationChannel(
        TIMER_ALARM_CHANNEL_ID,
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
      )
    )
  }

  fun notifyTimerTimeout(context: Context, timerInfo: TimerInfo) {
    context.notificationManager.notify(
      timerInfo.id,
      buildTimerTimeoutNotification(
        context,
        timerInfo.title
      )
    )
  }

  fun buildTimerTimeoutNotification(context: Context, title: String): Notification =
    buildNotification(
      context,
      title,
      description = ""
    )

  private fun buildNotification(
    context: Context,
    title: String,
    description: String,
  ): Notification = NotificationCompat.Builder(context, TIMER_ALARM_CHANNEL_ID)
    .setSmallIcon(R.mipmap.ic_launcher)
    .setContentTitle(title)
    .setContentText(description)
    .setContentIntent(
      PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    )
    .build()

  fun removeNotification(context: Context, timerId: Int) {
    context.notificationManager.cancel(timerId)
  }
}