package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo

class TimerListViewModel : ViewModel() {
  var battleTimer by mutableStateOf(TimerInfo(title = "익명의 코뿔소", time = 0))
    private set

  private var timerJob: Job? = null

  init {
    startBattleTimer()
  }

  fun refreshBattleTimer() {
    battleTimer = battleTimer.copy(time = 0)
    startBattleTimer()
  }

  private fun startBattleTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(TimerInfo.SECONDS_UNIT)
        battleTimer = battleTimer.copy(time = battleTimer.time + TimerInfo.SECONDS_UNIT)
      }
    }
  }

  private val _timerListFlow = MutableStateFlow(emptyList<TimerInfo>())
  val timerListFlow: StateFlow<List<TimerInfo>> = _timerListFlow

  init {
    _timerListFlow.value = listOf(
      TimerInfo(title = "3분 타이머", time = 3 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "5분 타이머", time = 5 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "10분 타이머", time = 10 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "15분 타이머", time = 15 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "20분 타이머", time = 20 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "30분 타이머", time = 30 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "60분 타이머", time = 60 * TimerInfo.MINUTE_UNIT)
    )
  }
}