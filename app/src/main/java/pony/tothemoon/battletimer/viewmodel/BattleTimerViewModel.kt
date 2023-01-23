package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.datastore.ActiveTimer
import pony.tothemoon.battletimer.datastore.TimerDataStore
import pony.tothemoon.battletimer.model.TimerInfo

class BattleTimerViewModel(private val timerInfo: TimerInfo) : ViewModel() {
  var battleTimer by mutableStateOf(TimerInfo(title = "익명의 코뿔소", time = timerInfo.remainedTime))
    private set

  var timerUiState: BattleTimerUiState by mutableStateOf(BattleTimerUiState.Idle(timerInfo.remainedTime))
    private set

  init {
    if (timerInfo.state == TimerInfo.State.RUNNING) {
      viewModelScope.launch { startBattle() }
    }
  }

  private suspend fun startBattle() {
    val winningTime = (10 * TimerInfo.SECONDS_UNIT until 50 * TimerInfo.SECONDS_UNIT).random()
    val timeTick = 100L
    val encourageTexts = arrayOf(
      "끝까지만 하면 이기는거야",
      "거의 다 왔어요! 끝까지 해내세요",
      "힘내세요! 할 수 있어요!"
    )
    var encourageText: String = encourageTexts.random()

    while (timerUiState.time > 0) {
      delay(timeTick)

      val remainedTime = timerUiState.time - timeTick
      val hasWin = remainedTime < winningTime
      val changeEncourage = (remainedTime / timeTick % timeTick) == 0L

      if (changeEncourage) {
        encourageText = encourageTexts.random()
      }

      timerUiState = BattleTimerUiState.Running(
        time = remainedTime,
        hasWin = hasWin,
        encourageText = encourageText
      )

      if (!hasWin) {
        battleTimer = battleTimer.copy(remainedTime = remainedTime)
      }
    }

    timerUiState = BattleTimerUiState.Finish(timerUiState.time)
    clear()
  }

  fun start() {
    viewModelScope.launch {
      if (timerUiState is BattleTimerUiState.Idle || timerUiState is BattleTimerUiState.Finish) {
        timerUiState = BattleTimerUiState.Loading(timerInfo.time)
        battleTimer = battleTimer.copy(time = timerInfo.time)

        delay(2000)

        repeat(3) {
          delay(TimerInfo.SECONDS_UNIT)
          timerUiState = BattleTimerUiState.Ready(timerUiState.time, 3 - it)
        }

        startBattle()
      }
    }
  }

  fun cancel() {
    timerUiState = BattleTimerUiState.Idle(timerInfo.time)
    clear()
  }

  fun save() {
    if (timerUiState is BattleTimerUiState.Running) {
      CoroutineScope(Dispatchers.IO).launch {
        TimerDataStore.save(
          ActiveTimer(
            isBattle = true,
            _timerInfo = timerInfo.copy(
              remainedTime = timerUiState.time,
              state = when (timerUiState) {
                is BattleTimerUiState.Running -> TimerInfo.State.RUNNING
                else -> TimerInfo.State.IDLE
              }
            )
          )
        )
      }
    }
  }

  fun clear() {
    CoroutineScope(Dispatchers.IO).launch {
      TimerDataStore.clear()
    }
  }
}

sealed class BattleTimerUiState {
  data class Idle(override val time: Long) : BattleTimerUiState()
  data class Loading(override val time: Long) : BattleTimerUiState()
  data class Ready(override val time: Long, val countdown: Int) : BattleTimerUiState()
  data class Running(
    override val time: Long,
    val hasWin: Boolean = false,
    val encourageText: String,
  ) : BattleTimerUiState()

  data class Finish(override val time: Long) : BattleTimerUiState()

  abstract val time: Long

  val displayBattle: Boolean
    get() = this is Running || this is Finish
}

class BattleTimerViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(BattleTimerViewModel::class.java)) {
      return BattleTimerViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}