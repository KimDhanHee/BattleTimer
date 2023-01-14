package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo

class TimerViewModel(timerInfo: TimerInfo) : ViewModel() {
  var battleTimer by mutableStateOf(TimerInfo(title = "익명의 코뿔소", time = timerInfo.time))
    private set

  private suspend fun startBattle() {
    while (timerUiState.time > 0) {
      delay(TimerInfo.SECONDS_UNIT)

      timerUiState = TimerUiState.Running(timerUiState.time - TimerInfo.SECONDS_UNIT)

      battleTimer = battleTimer.copy(time = battleTimer.time - TimerInfo.SECONDS_UNIT)
    }

    timerUiState = TimerUiState.Finish(timerUiState.time)
  }

  var timerUiState: TimerUiState by mutableStateOf(TimerUiState.Idle(timerInfo.time))
    private set

  fun start() {
    viewModelScope.launch {
      if (timerUiState is TimerUiState.Idle) {
        timerUiState = TimerUiState.Loading(timerUiState.time, "")

        delay(2000)

        repeat(3) {
          delay(TimerInfo.SECONDS_UNIT)
          timerUiState = TimerUiState.Ready(timerUiState.time, 3 - it)
        }

        startBattle()
      }
    }
  }
}

sealed class TimerUiState {
  data class Idle(override val time: Long) : TimerUiState()
  data class Loading(override val time: Long, val text: String) : TimerUiState()
  data class Ready(override val time: Long, val countdown: Int) : TimerUiState()
  data class Running(override val time: Long) : TimerUiState()
  data class Finish(override val time: Long) : TimerUiState()

  abstract val time: Long
}

class TimerViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
      return TimerViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}