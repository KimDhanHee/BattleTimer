package pony.tothemoon.battletimer.viewmodel

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.datastore.ActiveTimer
import pony.tothemoon.battletimer.datastore.TimerDataStore
import pony.tothemoon.battletimer.model.TimerInfo

class SingleTimerViewModel(private val timerInfo: TimerInfo) : ViewModel() {
  var timerUiState: SingleTimerUiState by mutableStateOf(SingleTimerUiState.Idle(timerInfo.remainedTime))
    private set

  init {
    when (timerInfo.state) {
      TimerInfo.State.RUNNING -> start()
      TimerInfo.State.PAUSE ->
        timerUiState = SingleTimerUiState.Pause(timerInfo.remainedTime)
      TimerInfo.State.FINISH ->
        timerUiState = SingleTimerUiState.Finish(0)
      else -> Unit
    }
  }

  private var timer: CountDownTimer? = null
  fun start() {
    val timeTick = 100L
    timer = object : CountDownTimer(timerUiState.time, timeTick) {
      override fun onTick(remainedTime: Long) {
        timerUiState = SingleTimerUiState.Running(timerUiState.time - timeTick)
      }

      override fun onFinish() {
        timerUiState = SingleTimerUiState.Finish(0)
      }
    }.start()
  }

  fun pause() {
    timer?.cancel()
    timerUiState = SingleTimerUiState.Pause(timerUiState.time)
  }

  fun dismiss() {
    timerUiState = SingleTimerUiState.Idle(timerInfo.time)
    clear()
  }

  fun save() {
    if (timerUiState.isActive) {
      CoroutineScope(Dispatchers.IO).launch {
        val current = ActiveTimer(
          isBattle = false,
          _timerInfo = timerInfo.copy(
            remainedTime = timerUiState.time,
            state = when (timerUiState) {
              is SingleTimerUiState.Running -> TimerInfo.State.RUNNING
              is SingleTimerUiState.Pause -> TimerInfo.State.PAUSE
              else -> TimerInfo.State.IDLE
            }
          )
        )
        TimerDataStore.save(current)
      }
    }
  }

  fun clear() {
    timer?.cancel()
    timer = null

    CoroutineScope(Dispatchers.IO).launch {
      TimerDataStore.clear()
    }
  }
}

sealed class SingleTimerUiState {
  data class Idle(override val time: Long) : SingleTimerUiState()
  data class Running(override val time: Long) : SingleTimerUiState()
  data class Pause(override val time: Long) : SingleTimerUiState()
  data class Finish(override val time: Long) : SingleTimerUiState()

  val isActive: Boolean get() = this !is Idle
  abstract val time: Long
}

class SingleTimerViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SingleTimerViewModel::class.java)) {
      return SingleTimerViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}