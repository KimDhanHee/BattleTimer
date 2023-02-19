package pony.tothemoon.battletimer.viewmodel

import android.os.CountDownTimer
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
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
import pony.tothemoon.battletimer.datastore.UserDataStore
import pony.tothemoon.battletimer.event.EventLogger
import pony.tothemoon.battletimer.event.PonyEvent
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.utils.AndroidUtils

class BattleTimerViewModel(private val timerInfo: TimerInfo) : ViewModel() {
  private val newRandomUser: String
    get() = AndroidUtils.stringArray(R.array.random_users).random()

  var battleTimer by mutableStateOf(
    TimerInfo(
      title = newRandomUser,
      remainedTime = timerInfo.remainedTime,
      time = timerInfo.time
    )
  )
    private set

  var timerUiState: BattleTimerUiState by mutableStateOf(BattleTimerUiState.Idle(timerInfo.remainedTime))
    private set

  init {
    when (timerInfo.state) {
      TimerInfo.State.RUNNING -> startBattle()
      TimerInfo.State.FINISH -> finish()
      else -> Unit
    }
  }

  fun start() {
    viewModelScope.launch {
      if (timerUiState is BattleTimerUiState.Idle || timerUiState is BattleTimerUiState.Finish) {
        init()

        loading()

        countdown()

        startBattle()
      }
    }
  }

  private fun init() {
    battleTimer = battleTimer.copy(title = newRandomUser, time = timerInfo.remainedTime)
  }

  private suspend fun loading() {
    val loadingTextRes = arrayOf(
      R.string.battle_timer_loading_search,
      R.string.battle_timer_loading_enter,
      R.string.battle_timer_loading_end
    )
    loadingTextRes.forEach { textRes ->
      timerUiState = BattleTimerUiState.Loading(timerInfo.time, textRes)
      delay(2000)
    }
  }

  private suspend fun countdown() {
    repeat(3) {
      timerUiState = BattleTimerUiState.Ready(timerUiState.time, 3 - it)
      delay(TimerInfo.SECONDS_UNIT)
    }
  }

  private var timer: CountDownTimer? = null
  private fun startBattle() {
    val concentrateOnScreenTime =
      (timerInfo.time - 1 * TimerInfo.MINUTE_UNIT until timerUiState.time - 30 * TimerInfo.SECONDS_UNIT).random()
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

    timer = object : CountDownTimer(timerUiState.time, timeTick) {
      override fun onTick(remainedTime: Long) {
        val hasWin = remainedTime < winningTime
        val changeEncourage = (remainedTime / timeTick % timeTick) == 0L

        if (changeEncourage) {
          encourageTextRes = encourageTextResArray.random()
        }

        timerUiState = BattleTimerUiState.Running(
          time = remainedTime,
          battleTextRes = when {
            remainedTime in (concentrateOnScreenTime..timerInfo.time) -> R.string.battle_timer_other_concentrating_screen_on
            remainedTime in (winningTime..concentrateOnScreenTime) -> R.string.battle_timer_other_concentrating_screen_off
            remainedTime < winningTime -> R.string.battle_timer_other_left
            else -> 0
          },
          encourageTextRes = encourageTextRes
        )

        if (!hasWin) {
          battleTimer = battleTimer.copy(remainedTime = remainedTime)
        }
      }

      override fun onFinish() {
        finish()
      }
    }.start()
  }

  private fun finish() {
    timerUiState = BattleTimerUiState.Finish(0)

    viewModelScope.launch { UserDataStore.finishBattle() }

    clear()

    EventLogger.log(PonyEvent.FINISH_TIMER, bundleOf(
      "type" to "battle",
      "time" to timerInfo.time
    ))
  }

  fun cancel() {
    timerUiState = BattleTimerUiState.Idle(timerInfo.time)

    timer?.cancel()
    timer = null

    clear()
  }

  fun save() {
    if (timerUiState.isRunning) {
      CoroutineScope(Dispatchers.IO).launch {
        val current = ActiveTimer(
          isBattle = true,
          _timerInfo = timerInfo.copy(
            remainedTime = timerUiState.time,
            state = TimerInfo.State.RUNNING
          )
        )
        TimerDataStore.save(current)
      }
    }
  }

  private fun clear() {
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
    @StringRes val battleTextRes: Int,
    @StringRes val encourageTextRes: Int,
  ) : BattleTimerUiState()

  data class Finish(override val time: Long) : BattleTimerUiState()

  abstract val time: Long

  val displayBattle: Boolean
    get() = this is Running || this is Finish

  val isRunning: Boolean
    get() = this is Loading || this is Ready || this is Running

  val battleLabelRes
    get() = when (this) {
      is Running -> this.battleTextRes
      is Finish -> R.string.battle_timer_other_left
      else -> 0
    }
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