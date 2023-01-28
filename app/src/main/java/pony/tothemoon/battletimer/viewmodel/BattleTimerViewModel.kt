package pony.tothemoon.battletimer.viewmodel

import androidx.annotation.StringRes
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
import pony.tothemoon.battletimer.R
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
    val encourageTextResArray = arrayOf(
      R.string.battle_timer_encourage_1,
      R.string.battle_timer_encourage_2,
      R.string.battle_timer_encourage_3,
      R.string.battle_timer_encourage_4,
      R.string.battle_timer_encourage_5,
      R.string.battle_timer_encourage_6,
      R.string.battle_timer_encourage_7,
      R.string.battle_timer_encourage_8,
      R.string.battle_timer_encourage_9,
      R.string.battle_timer_encourage_10,
      R.string.battle_timer_encourage_11,
      R.string.battle_timer_encourage_12,
    )
    var encourageTextRes: Int = encourageTextResArray.random()

    while (timerUiState.time > 0) {
      delay(timeTick)

      val remainedTime = timerUiState.time - timeTick
      val hasWin = remainedTime < winningTime
      val changeEncourage = (remainedTime / timeTick % timeTick) == 0L

      if (changeEncourage) {
        encourageTextRes = encourageTextResArray.random()
      }

      timerUiState = BattleTimerUiState.Running(
        time = remainedTime,
        hasWin = hasWin,
        textRes = encourageTextRes
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
        loading()

        battleTimer = battleTimer.copy(time = timerInfo.time)

        countdown()

        startBattle()
      }
    }
  }

  private suspend fun loading() {
    val loadingTextRes = arrayOf(
      R.string.battle_timer_loading_search,
      R.string.battle_timer_loading_enter,
      R.string.battle_timer_loading_end
    )
    loadingTextRes.forEach { textRes ->
      timerUiState = BattleTimerUiState.Loading(timerInfo.time, textRes)
      delay(1000)
    }
  }

  private suspend fun countdown() {
    repeat(3) {
      delay(TimerInfo.SECONDS_UNIT)
      timerUiState = BattleTimerUiState.Ready(timerUiState.time, 3 - it)
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
  data class Loading(override val time: Long, @StringRes val textRes: Int) : BattleTimerUiState()
  data class Ready(override val time: Long, val countdown: Int) : BattleTimerUiState()
  data class Running(
    override val time: Long,
    val hasWin: Boolean = false,
    @StringRes val textRes: Int,
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