package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.utils.AndroidUtils

class TimerListViewModel : ViewModel() {
  private val newRandomUser: String
    get() = AndroidUtils.stringArray(R.array.random_users).random()

  var otherUserTimer by mutableStateOf(
    TimerInfo(
      title = AndroidUtils.string(R.string.timer_list_battle_timer_title, newRandomUser),
      time = 0
    )
  )
    private set

  private var timerJob: Job? = null

  init {
    startOtherUserTimer()
  }

  fun refreshOtherUserTimer() {
    otherUserTimer = otherUserTimer.copy(
      title = AndroidUtils.string(R.string.timer_list_battle_timer_title, newRandomUser),
      time = 0
    )
    startOtherUserTimer()
  }

  private fun startOtherUserTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(TimerInfo.SECONDS_UNIT)
        when {
          otherUserTimer.time >= 50 * TimerInfo.MINUTE_UNIT -> refreshOtherUserTimer()
          else -> otherUserTimer = otherUserTimer.copy(time = otherUserTimer.time + TimerInfo.SECONDS_UNIT)
        }
      }
    }
  }

  val presetTimers = arrayOf(
    TimerInfo(title = AndroidUtils.string(R.string.minute_3_timer), time = 3 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_5_timer), time = 5 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_10_timer), time = 10 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_15_timer), time = 15 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_25_timer), time = 25 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_50_timer), time = 50 * TimerInfo.MINUTE_UNIT),
  )
}