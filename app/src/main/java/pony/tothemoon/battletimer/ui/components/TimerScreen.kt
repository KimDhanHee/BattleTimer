package pony.tothemoon.battletimer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.Purple200
import pony.tothemoon.battletimer.ui.theme.White900
import pony.tothemoon.battletimer.viewmodel.TimerUiState
import pony.tothemoon.battletimer.viewmodel.TimerViewModel
import pony.tothemoon.battletimer.viewmodel.TimerViewModelFactory

@Composable
fun TimerScreen(
  timerInfo: TimerInfo,
  viewModel: TimerViewModel = viewModel(factory = TimerViewModelFactory(timerInfo)),
) {
  Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = timerInfo.title)

    Box(
      modifier = Modifier.size(320.dp),
      contentAlignment = Alignment.Center
    ) {
      val timerUiState = viewModel.timerUiState
      CircularTimerProgress(progress = (timerUiState.time / timerInfo.time.toFloat()))
      TimerProgressIndicator(
        timerUiState,
        onClickStart = { viewModel.start() },
        onClickPause = { viewModel.pause() }
      )
    }
  }
}

@Composable
private fun CircularTimerProgress(progress: Float) {
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
}

@Composable
private fun TimerProgressIndicator(
  timerUiState: TimerUiState,
  onClickStart: () -> Unit = {},
  onClickPause: () -> Unit = {},
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = timerUiState.time.timeStr)
    Icon(
      imageVector = when (timerUiState) {
        is TimerUiState.Running -> Icons.Default.Pause
        is TimerUiState.Paused -> Icons.Default.PlayArrow
      },
      contentDescription = null,
      modifier = Modifier.clickable {
        when (timerUiState) {
          is TimerUiState.Running -> onClickPause()
          is TimerUiState.Paused -> onClickStart()
        }
      }
    )
  }
}