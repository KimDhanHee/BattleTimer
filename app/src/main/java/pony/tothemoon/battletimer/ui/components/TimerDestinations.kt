package pony.tothemoon.battletimer.ui.components

sealed class TimerDestination {
  object TimerList: TimerDestination() {
    override val route: String = "timer_list"
  }

  abstract val route: String
}