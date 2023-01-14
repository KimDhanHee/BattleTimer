package pony.tothemoon.battletimer.ui.components

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pony.tothemoon.battletimer.model.TimerInfo

sealed class TimerDestination {
  object TimerList : TimerDestination() {
    override val route: String = "timer_list"

    const val KEY_IS_CANCEL = "is_cancel"
  }

  object BattleTimer : TimerDestination() {
    override val route: String
      get() = "battle_timer"
    const val timerInfoArg = "timerInfo"
    val routeWithArgs = "$route/{$timerInfoArg}"
    val arguments = listOf(navArgument(timerInfoArg) { type = TimerNavType() })
  }

  abstract val route: String
}

class TimerNavType : JsonNavType<TimerInfo>() {
  override fun fromJsonParse(value: String): TimerInfo = Json.decodeFromString(value)

  override fun TimerInfo.toJsonParse(): String = Json.encodeToString(this)
}

abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
  abstract fun fromJsonParse(value: String): T
  abstract fun T.toJsonParse(): String

  override fun get(bundle: Bundle, key: String): T? = bundle.getString(key)?.let { parseValue(it) }
  override fun parseValue(value: String): T = fromJsonParse(value)
  override fun put(bundle: Bundle, key: String, value: T) {
    bundle.putString(key, value.toJsonParse())
  }
}