package pony.tothemoon.battletimer.event

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EventLogger {
  fun log(event: PonyEvent, bundle: Bundle? = null) {
    Firebase.analytics.logEvent(event.name, (bundle ?: Bundle()).apply {
      putString("timestamp", currentTimeStr)
    })
  }

  private val currentTimeStr: String
    get() = SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS", Locale.ROOT).format(Date())
}

enum class PonyEvent {
  START_TIMER,
  CANCEL_TIMER,
  FINISH_TIMER,
}