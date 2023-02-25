package pony.tothemoon.battletimer.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.utils.AndroidUtils
import kotlin.math.max

object TimerDataStore {
  private val Context.datastore: DataStore<Preferences> by preferencesDataStore("timer")

  private val prefActiveTimer = stringPreferencesKey("active_timer")
  val activeTimerFlow = AndroidUtils.context.datastore.data.map { preferences ->
    preferences[prefActiveTimer]?.let {
      when {
        it.isBlank() -> null
        else -> Json.decodeFromString<ActiveTimer>(it)
      }
    }
  }

  suspend fun save(activeTimer: ActiveTimer) {
    AndroidUtils.context.datastore.edit { preferences ->
      preferences[prefActiveTimer] = Json.encodeToString(activeTimer)
    }
  }

  suspend fun clear() {
    AndroidUtils.context.datastore.edit { preferences ->
      preferences[prefActiveTimer] = ""
    }
  }
}

@Serializable
data class ActiveTimer(
  private val _timerInfo: TimerInfo,
  private val lastRunningTime: Long = System.currentTimeMillis(),
) {
  val timerInfo: TimerInfo
    get() {
      val remainedTime =
        max(0, _timerInfo.remainedTime - (System.currentTimeMillis() - lastRunningTime))
      val state = when (remainedTime) {
        0L -> TimerInfo.State.FINISH
        else -> TimerInfo.State.RUNNING
      }
      return _timerInfo.copy(remainedTime = remainedTime, state = state)
    }
}