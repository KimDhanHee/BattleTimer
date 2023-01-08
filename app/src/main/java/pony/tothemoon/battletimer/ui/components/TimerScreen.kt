package pony.tothemoon.battletimer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.Purple200
import pony.tothemoon.battletimer.ui.theme.White900
import pony.tothemoon.battletimer.viewmodel.BattleTimerViewModel
import pony.tothemoon.battletimer.viewmodel.TimerUiState
import pony.tothemoon.battletimer.viewmodel.TimerViewModel
import pony.tothemoon.battletimer.viewmodel.TimerViewModelFactory

@Composable
fun TimerScreen(
  timerInfo: TimerInfo,
  timerViewModel: TimerViewModel = viewModel(factory = TimerViewModelFactory(timerInfo)),
  battleTimerViewModel: BattleTimerViewModel = viewModel(),
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    val timerUiState = timerViewModel.timerUiState
    CircularTimerProgress(
      progress = (timerUiState.time / timerInfo.time.toFloat()),
      modifier = Modifier.size(320.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = timerInfo.title)
        Text(text = timerUiState.time.timeStr)
        Icon(
          imageVector = when (timerUiState) {
            is TimerUiState.Running -> Icons.Default.Pause
            is TimerUiState.Paused -> Icons.Default.PlayArrow
          },
          contentDescription = null,
          modifier = Modifier.clickable {
            when (timerUiState) {
              is TimerUiState.Running -> timerViewModel.pause()
              is TimerUiState.Paused -> timerViewModel.start()
            }
          }
        )
      }
    }

    Divider(modifier = Modifier.padding(vertical = 20.dp), thickness = 1.dp, color = White900)

    val battleTimer = battleTimerViewModel.battleTimer
    val battleTime by remember { mutableStateOf(battleTimer.time) }
    CircularTimerProgress(
      progress = (battleTimer.time / battleTime.toFloat()),
      modifier = Modifier.size(320.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = battleTimer.title)
        Text(text = battleTimer.time.timeStr)
      }
    }
  }
}

@Composable
private fun CircularTimerProgress(
  progress: Float,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    val progressAnim by animateFloatAsState(targetValue = progress)

    CircularProgressIndicator(
      progress = 1f,
      modifier = Modifier.fillMaxSize(),
      color = White900.copy(alpha = 0.5f)
    )
    CircularProgressIndicator(
      progress = progressAnim,
      modifier = Modifier.fillMaxSize(),
      color = Purple200
    )

    Column {
      content()
    }
  }
}