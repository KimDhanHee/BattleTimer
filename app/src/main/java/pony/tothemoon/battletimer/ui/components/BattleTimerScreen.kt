package pony.tothemoon.battletimer.ui.components

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.extensions.onLifecycleEvent
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.service.TimerService
import pony.tothemoon.battletimer.ui.theme.Gray100
import pony.tothemoon.battletimer.ui.theme.White900
import pony.tothemoon.battletimer.utils.AlarmUtils
import pony.tothemoon.battletimer.viewmodel.BattleTimerUiState
import pony.tothemoon.battletimer.viewmodel.BattleTimerViewModel
import pony.tothemoon.battletimer.viewmodel.BattleTimerViewModelFactory
import java.util.Locale

@Composable
fun BattleTimerScreen(
  timerInfo: TimerInfo,
  navController: NavHostController,
  viewmodel: BattleTimerViewModel = viewModel(factory = BattleTimerViewModelFactory(timerInfo)),
) {
  onLifecycleEvent { event ->
    when (event) {
      Lifecycle.Event.ON_CREATE -> viewmodel.clear()
      Lifecycle.Event.ON_PAUSE -> viewmodel.save()
      else -> Unit
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    val timerUiState = viewmodel.timerUiState

    var showExitDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showExitScreen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(timerUiState) {
      if (timerUiState is BattleTimerUiState.Finish) showFeedbackDialog = true
    }

    if (showFeedbackDialog) {
      ConfirmDialog(
        title = stringResource(id = R.string.battle_timer_feedback_title),
        positive = stringResource(id = R.string.battle_timer_feedback_positive),
        negative = stringResource(id = R.string.battle_timer_feedback_negative),
        onClickOk = {
          showFeedbackDialog = false
          val uri = when (Locale.getDefault()) {
            Locale.KOREA, Locale.KOREAN -> "https://tally.so/r/mKprWM"
            else -> ":https://tally.so/r/3X5bzL"
          }.toUri()
          context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        },
        onClickCancel = { showFeedbackDialog = false },
      )
    }

    if (showExitDialog) {
      ConfirmDialog(
        title = stringResource(id = R.string.battle_timer_exit_title),
        positive = stringResource(id = R.string.battle_timer_exit_positive),
        negative = stringResource(id = R.string.battle_timer_exit_negative),
        onClickOk = {
          showExitDialog = false
          showExitScreen = true
          AlarmUtils.cancelAlarm(context, timerInfo.id)
        },
        onClickCancel = { showExitDialog = false },
      )
    }

    val onBack = {
      when (timerUiState) {
        is BattleTimerUiState.Idle -> cancel(navController)
        is BattleTimerUiState.Finish -> reset(navController)
        else -> showExitDialog = true
      }
    }

    BackHandler { onBack() }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(color = Gray100),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Header(text = timerInfo.title, onClickBack = { onBack() })
      Body(
        myTimer = timerInfo,
        battleTimer = viewmodel.battleTimer,
        timerUiState = timerUiState,
        modifier = Modifier.weight(1f)
      )
      Footer(
        timerUiState,
        onClickStart = {
          viewmodel.start()
          context.stopService(Intent(context, TimerService::class.java))
          AlarmUtils.setAlarm(
            context,
            timerInfo.copy(remainedTime = timerInfo.remainedTime + 9 * TimerInfo.SECONDS_UNIT)
          )
        },
        onCancel = { onBack() },
        onFinish = {
          reset(navController)
          context.stopService(Intent(context, TimerService::class.java))
        },
      )
    }

    if (timerUiState is BattleTimerUiState.Loading) {
      LoadingScreen(stringResource(id = timerUiState.textRes, viewmodel.battleTimer.title))
    }

    if (timerUiState is BattleTimerUiState.Ready) {
      ReadyScreen(timerUiState.countdown)
    }

    if (showExitScreen) {
      ExitScreen(onTimeout = {
        showExitScreen = false

        viewmodel.cancel()
        cancel(navController)
      })
    }
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
  myTimer: TimerInfo,
  battleTimer: TimerInfo,
  timerUiState: BattleTimerUiState,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    ProgressIndicator(
      progress = timerUiState.time / myTimer.time.toFloat(),
      progressText = when (timerUiState) {
        is BattleTimerUiState.Finish -> stringResource(id = R.string.battle_timer_win)
        else -> timerUiState.time.timeStr
      },
      label = when (timerUiState) {
        is BattleTimerUiState.Running -> stringResource(id = timerUiState.textRes)
        is BattleTimerUiState.Finish -> stringResource(id = R.string.battle_timer_good_job)
        else -> ""
      },
      modifier = Modifier
        .weight(1f)
        .padding(20.dp)
    )

    if (timerUiState.displayBattle) {
      val battleTime by remember { mutableStateOf(battleTimer.time) }
      val displayWin =
        timerUiState is BattleTimerUiState.Running && timerUiState.hasWin || timerUiState is BattleTimerUiState.Finish
      ProgressIndicator(
        progress = battleTimer.remainedTime / battleTime.toFloat(),
        progressText = when (timerUiState) {
          is BattleTimerUiState.Finish -> stringResource(id = R.string.battle_timer_lose)
          else -> battleTimer.remainedTime.timeStr
        },
        label = when {
          displayWin -> stringResource(id = R.string.battle_timer_other_left, battleTimer.title)
          else -> ""
        },
        modifier = Modifier
          .weight(1f)
          .background(color = Color.White)
          .padding(horizontal = 20.dp),
        textColor = Gray100,
        timerColor = Gray100
      )
    }
  }
}

@Composable
private fun Footer(
  battleTimerUiState: BattleTimerUiState,
  onClickStart: () -> Unit,
  onCancel: () -> Unit,
  onFinish: () -> Unit,
) {
  val backgroundColor = when {
    battleTimerUiState.displayBattle -> Color.White
    else -> Gray100
  }
  val textColor = when {
    battleTimerUiState.displayBattle -> Gray100
    else -> Color.White
  }

  if (battleTimerUiState is BattleTimerUiState.Idle) {
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(id = R.string.battle_timer_bottom_description),
      color = textColor,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelMedium,
    )
  }
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = backgroundColor)
      .padding(vertical = 20.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    when (battleTimerUiState) {
      is BattleTimerUiState.Idle ->
        TimerButton(
          text = stringResource(id = R.string.battle_timer_button_start),
          color = textColor,
          onClick = onClickStart
        )
      is BattleTimerUiState.Loading, is BattleTimerUiState.Ready, is BattleTimerUiState.Running ->
        TimerButton(
          text = stringResource(id = R.string.battle_timer_button_giveup),
          color = textColor,
          onClick = onCancel
        )
      is BattleTimerUiState.Finish -> {
        TimerButton(
          text = stringResource(id = R.string.battle_timer_button_one_more),
          color = textColor,
          onClick = onClickStart
        )
        Spacer(modifier = Modifier.size(20.dp))
        TimerButton(
          text = stringResource(id = R.string.battle_timer_button_finish),
          color = textColor,
          onClick = onFinish
        )
      }
    }
  }
}

@Composable
private fun TimerButton(text: String, color: Color, onClick: () -> Unit) {
  OutlinedButton(
    onClick = onClick,
    border = BorderStroke(width = 1.dp, color = color)
  ) {
    Text(
      text = text,
      color = color,
      style = MaterialTheme.typography.labelMedium
    )
  }
}

@Composable
private fun ProgressIndicator(
  progress: Float,
  progressText: String,
  label: String = "",
  modifier: Modifier = Modifier,
  textColor: Color = Color.White,
  timerColor: Color = Color.White,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = progressText,
      modifier = Modifier
        .padding(vertical = 32.dp)
        .fillMaxWidth(),
      color = timerColor,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.displayLarge
    )

    val progressAnim by animateFloatAsState(
      targetValue = progress,
      animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(
      progress = progressAnim,
      modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .clip(shape = RoundedCornerShape(6.dp)),
      color = timerColor,
      trackColor = White900
    )
    Spacer(modifier = Modifier.size(48.dp))
    Text(
      text = label,
      color = textColor,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelMedium,
    )
  }
}

@Composable
private fun LoadingScreen(text: String) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.8f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = Color.White,
      textAlign = TextAlign.Center,
      lineHeight = 42.sp,
      style = MaterialTheme.typography.displaySmall
    )
  }
}

@Composable
private fun ReadyScreen(countdown: Int) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.8f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$countdown",
      color = Color.White,
      style = MaterialTheme.typography.displayLarge
    )
  }
}

@Composable
private fun ExitScreen(onTimeout: () -> Unit) {
  LaunchedEffect(Unit) {
    delay(2000)
    onTimeout()
  }
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.8f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(id = R.string.battle_timer_exit_encourage),
      color = Color.White,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.displaySmall
    )
  }
}