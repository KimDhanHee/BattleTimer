package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo

class TimerViewModel(timerInfo: TimerInfo) : ViewModel() {
  var timerUiState: TimerUiState by mutableStateOf(TimerUiState.Paused(timerInfo.time))
    private set

  private var timerJob: Job? = null

  fun start() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        timerUiState = TimerUiState.Running(timerUiState.time - 100)
        delay(100)
      }
    }
  }

  fun pause() {
    timerJob?.cancel()
    timerUiState = TimerUiState.Paused(timerUiState.time)
  }
}

sealed class TimerUiState {
  data class Running(override val time: Long) : TimerUiState()
  data class Paused(override val time: Long) : TimerUiState()

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