package pony.tothemoon.battletimer.ui.components

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.extensions.onLifecycleEvent
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.service.TimerService
import pony.tothemoon.battletimer.ui.theme.Gray100
import pony.tothemoon.battletimer.utils.AlarmUtils
import pony.tothemoon.battletimer.viewmodel.SingleTimerUiState
import pony.tothemoon.battletimer.viewmodel.SingleTimerViewModel
import pony.tothemoon.battletimer.viewmodel.SingleTimerViewModelFactory

@Composable
fun SingleTimerScreen(
  timerInfo: TimerInfo,
  navController: NavHostController,
  viewmodel: SingleTimerViewModel = viewModel(factory = SingleTimerViewModelFactory(timerInfo)),
) {
  val context = LocalContext.current

  onLifecycleEvent { event ->
    when (event) {
      Lifecycle.Event.ON_CREATE -> viewmodel.clear()
      Lifecycle.Event.ON_PAUSE -> viewmodel.save()
      else -> Unit
    }
  }

  var showDialog by remember { mutableStateOf(false) }
  if (showDialog) {
    ExitDialog(
      title = stringResource(id = R.string.single_timer_exit_title),
      positive = stringResource(id = R.string.single_timer_exit_positive),
      negative = stringResource(id = R.string.single_timer_exit_negative),
      onClickOk = {
        showDialog = false

        AlarmUtils.cancelAlarm(context, timerInfo.id)
        viewmodel.dismiss()
        cancel(navController)
      },
      onClickCancel = {
        showDialog = false
      }
    )
  }

  val timerUiState = viewmodel.timerUiState

  val onBack = {
    when {
      timerUiState.isActive -> showDialog = true
      timerUiState is SingleTimerUiState.Idle -> cancel(navController)
      timerUiState is SingleTimerUiState.Finish -> reset(navController)
    }
  }

  BackHandler { onBack() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Gray100)
  ) {
    Header(text = timerInfo.title, onClickBack = { onBack() })
    Body(
      timerInfo = timerInfo,
      timerUiState = timerUiState,
      onClickStart = {
        viewmodel.start()
        AlarmUtils.setAlarm(context, timerInfo.copy(remainedTime = timerUiState.time))
      },
      onClickPause = {
        viewmodel.pause()
        AlarmUtils.cancelAlarm(context, timerInfo.id)
      },
      onClickDismiss = {
        viewmodel.dismiss()
        context.stopService(Intent(context, TimerService::class.java))
      },
      modifier = Modifier.weight(1f)
    )
    Spacer(modifier = Modifier.size(100.dp))
  }
}

private fun cancel(navController: NavHostController) {
  navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set(TimerDestination.TimerList.KEY_IS_CANCEL, true)
  navController.navigateUp()
}

private fun reset(navController: NavHostController) {
  navController.previousBackStackEntry
    ?.savedStateHandle
    ?.remove<Boolean>(TimerDestination.TimerList.KEY_IS_CANCEL)
  navController.navigateUp()
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