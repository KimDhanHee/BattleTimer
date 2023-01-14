package pony.tothemoon.battletimer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import pony.tothemoon.battletimer.model.TimerInfo

@Composable
fun MyTimerScreen(timerInfo: TimerInfo, navController: NavHostController) {
  Text(text = timerInfo.title)
}