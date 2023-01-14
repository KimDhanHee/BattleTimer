package pony.tothemoon.battletimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.ui.theme.Gray100

@Composable
fun MyTimerScreen(timerInfo: TimerInfo, navController: NavHostController) {
  Column(modifier = Modifier.fillMaxSize().background(color = Gray100)) {
    Header(text = timerInfo.title, onClickBack = { navController.navigateUp() })
  }
}