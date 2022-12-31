package pony.tothemoon.battletimer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pony.tothemoon.battletimer.model.TimerInfo

class TimerListViewModel: ViewModel() {
  private val _timerListFlow = MutableStateFlow(emptyList<TimerInfo>())
  val timerListFlow: StateFlow<List<TimerInfo>> = _timerListFlow

  init {
    _timerListFlow.value = listOf(
      TimerInfo(title = "Timer1"),
      TimerInfo(title = "Timer2"),
      TimerInfo(title = "Timer3"),
      TimerInfo(title = "Timer4")
    )
  }
}