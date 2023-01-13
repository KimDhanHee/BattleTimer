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
      TimerInfo(title = "Timer1", time = 5 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer2", time = 10 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer3", time = 15 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer4", time = 20 * TimerInfo.MINUTE_UNIT)
    )
  }
}