package pony.tothemoon.battletimer.viewmodel

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pony.tothemoon.battletimer.datastore.ActiveTimer
import pony.tothemoon.battletimer.datastore.TimerDataStore
import pony.tothemoon.battletimer.model.TimerDatabase
import pony.tothemoon.battletimer.model.TimerHistory
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

    timer?.cancel()
    timer = object : CountDownTimer(timerUiState.time, timeTick) {
      override fun onTick(remainedTime: Long) {
        timerUiState = SingleTimerUiState.Running(timerUiState.time - timeTick)
      }

      override fun onFinish() {
        timerUiState = SingleTimerUiState.Finish(0)

        viewModelScope.launch {
          saveHistory()
        }
      }
    }.start()
  }

  fun pause() {
    timer?.cancel()
    timer = null

    timerUiState = SingleTimerUiState.Pause(timerUiState.time)
  }

  fun cancel() {
    viewModelScope.launch {
      saveHistory()
      dismiss()
    }
  }

  fun dismiss() {
    timerUiState = SingleTimerUiState.Idle(timerInfo.time)
    clear()
  }

  fun saveTimerState() {
    if (timerUiState.isActive) {
      CoroutineScope(Dispatchers.IO).launch {
        val current = ActiveTimer(
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

  private suspend fun saveHistory() {
    withContext(Dispatchers.IO) {
      TimerDatabase.timerDao.save(
        TimerHistory(
          time = timerInfo.time - timerUiState.time,
          isWin = timerUiState is SingleTimerUiState.Finish,
          type = timerInfo.type
        )
      )
    }
  }

  private fun clear() {
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