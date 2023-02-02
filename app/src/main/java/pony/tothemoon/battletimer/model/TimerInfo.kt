package pony.tothemoon.battletimer.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

@Parcelize
@Serializable
data class TimerInfo(
  val id: Int = -1,
  val title: String = "",
  val time: Long = 0L,
  val remainedTime: Long = time,
  val state: State = State.IDLE,
) : Parcelable {
  override fun toString(): String = Uri.encode(Json.encodeToString(this))

  companion object {
    const val HOUR_UNIT = 60 * 60 * 1000L
    const val MINUTE_UNIT = 60 * 1000L
    const val SECONDS_UNIT = 1000L
  }

  enum class State {
    IDLE,
    RUNNING,
    PAUSE,
    FINISH,
    ;
  }
}

val Long.timeStr: String
  get() {
    val hour = ((abs(this) / TimerInfo.HOUR_UNIT).toInt())
    val minute = ((abs((this) / TimerInfo.MINUTE_UNIT % 60)).toInt())
    val seconds = ((abs((this) / TimerInfo.SECONDS_UNIT) % 60).toInt())

    val plusMinusSign = when {
      this < 0 -> "-"
      else -> ""
    }

    return "${plusMinusSign}%02d:%02d:%02d".format(hour, minute, seconds)
  }