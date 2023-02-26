package pony.tothemoon.battletimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.model.TimerDatabase
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.utils.AndroidUtils

class TimerListViewModel : ViewModel() {
  val todayConcentrateTimeFlow: StateFlow<String> =
    TimerDatabase.timerDao.getTimeOfDate().map { it.timeStr }.catch { }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = 0L.timeStr
    )

  val todayWinCountFlow: StateFlow<Int> = TimerDatabase.timerDao.getWinCountOfDate().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = 0
  )

  val presetTimers = arrayOf(
    TimerInfo(title = AndroidUtils.string(R.string.minute_3_timer), time = 3 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_5_timer), time = 5 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_10_timer), time = 10 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_15_timer), time = 15 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_25_timer), time = 25 * TimerInfo.MINUTE_UNIT),
    TimerInfo(title = AndroidUtils.string(R.string.minute_50_timer), time = 50 * TimerInfo.MINUTE_UNIT),
  )
}