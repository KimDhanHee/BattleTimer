package pony.tothemoon.battletimer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import pony.tothemoon.battletimer.model.TimerInfo

@Composable
fun TimerScreen(timerInfo: TimerInfo) {
  Text(text = timerInfo.title)
}