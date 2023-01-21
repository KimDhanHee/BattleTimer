package pony.tothemoon.battletimer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.extensions.onLifecycleEvent
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.Gray100
import pony.tothemoon.battletimer.viewmodel.SingleTimerUiState
import pony.tothemoon.battletimer.viewmodel.SingleTimerViewModel
import pony.tothemoon.battletimer.viewmodel.SingleTimerViewModelFactory

@Composable
fun SingleTimerScreen(
  timerInfo: TimerInfo,
  navController: NavHostController,
  viewmodel: SingleTimerViewModel = viewModel(factory = SingleTimerViewModelFactory(timerInfo)),
) {
  onLifecycleEvent { event ->
    when (event) {
      Lifecycle.Event.ON_CREATE -> viewmodel.clear()
      Lifecycle.Event.ON_PAUSE -> viewmodel.save()
      else -> Unit
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Gray100)
  ) {
    val timerUiState = viewmodel.timerUiState

    Header(text = timerInfo.title, onClickBack = { navController.navigateUp() })
    Body(
      timerInfo = timerInfo,
      timerUiState = timerUiState,
      onClickStart = { viewmodel.start() },
      onClickPause = { viewmodel.pause() },
      onClickDismiss = { viewmodel.dismiss() },
      modifier = Modifier.weight(1f)
    )
    Spacer(modifier = Modifier.size(100.dp))
  }
}

@Composable
private fun Body(
  timerInfo: TimerInfo,
  timerUiState: SingleTimerUiState,
  onClickStart: () -> Unit,
  onClickPause: () -> Unit,
  onClickDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    CircularTimer(
      runningTime = timerUiState.time,
      totalTime = timerInfo.time,
      modifier = Modifier.size((LocalConfiguration.current.screenWidthDp * 0.9f).dp)
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      if (timerUiState is SingleTimerUiState.Finish) {
        Text(
          text = "타이머 종료",
          color = Color.White,
          style = MaterialTheme.typography.labelLarge
        )
      }

      Text(
        text = timerUiState.time.timeStr,
        color = Color.White,
        style = MaterialTheme.typography.displayLarge
      )
      Spacer(modifier = Modifier.size(36.dp))

      when (timerUiState) {
        is SingleTimerUiState.Running -> {
          Icon(
            painter = painterResource(id = R.drawable.ic_pause_24),
            contentDescription = null,
            modifier = Modifier
              .size(48.dp)
              .padding(2.dp)
              .clickable { onClickPause() },
            tint = Color.White
          )
        }
        is SingleTimerUiState.Finish -> {
          Button(
            onClick = { onClickDismiss() },
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
          ) {
            Icon(
              painter = painterResource(id = R.drawable.ic_bell_off_16),
              contentDescription = null,
              tint = Color.Black
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
              text = "해제",
              style = MaterialTheme.typography.labelLarge,
              color = Color.Black
            )
          }
        }
        else -> {
          Icon(
            painter = painterResource(id = R.drawable.ic_play_24),
            contentDescription = null,
            modifier = Modifier
              .size(48.dp)
              .padding(2.dp)
              .clickable { onClickStart() },
            tint = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun CircularTimer(
  runningTime: Long,
  totalTime: Long,
  modifier: Modifier = Modifier,
) {
  val progress by animateFloatAsState(
    targetValue = (runningTime / totalTime.toFloat()),
    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
  )
  CircularProgressIndicator(
    progress = 1f,
    modifier = modifier,
    color = Color.Gray.copy(alpha = 0.5f),
    strokeWidth = 4.dp
  )
  CircularProgressIndicator(
    progress = progress,
    modifier = modifier,
    color = Color.White,
    strokeWidth = 4.dp
  )
}