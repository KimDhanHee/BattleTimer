package pony.tothemoon.battletimer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pony.tothemoon.battletimer.model.TimerInfo

class TimerListViewModel : ViewModel() {
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
      TimerInfo(title = "60분 타이머", time = 30 * TimerInfo.MINUTE_UNIT)
    )
  }
}