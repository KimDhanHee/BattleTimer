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

class MyTimerViewModel(private val timerInfo: TimerInfo) : ViewModel() {
  var timerUiState: MyTimerUiState by mutableStateOf(MyTimerUiState.Idle(timerInfo.time))
    private set

  private var timerJob: Job? = null

  fun start() {
    timerJob = viewModelScope.launch {
      while (timerUiState.time > 0) {
        timerUiState = MyTimerUiState.Running(timerUiState.time - 100)
        delay(100)
      }

      timerUiState = MyTimerUiState.Finish(0)
    }
  }

  fun pause() {
    timerJob?.cancel()
    timerUiState = MyTimerUiState.Pause(timerUiState.time)
  }

  fun dismiss() {
    timerUiState = MyTimerUiState.Idle(timerInfo.time)
  }
}

sealed class MyTimerUiState {
  data class Idle(override val time: Long) : MyTimerUiState()
  data class Running(override val time: Long) : MyTimerUiState()
  data class Pause(override val time: Long) : MyTimerUiState()
  data class Finish(override val time: Long) : MyTimerUiState()

  abstract val time: Long
}

class MyTimerViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MyTimerViewModel::class.java)) {
      return MyTimerViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}