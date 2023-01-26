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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
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

    var showDialog by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (showDialog) {
      ExitDialog(
        title = "먼저 마무리 하시겠어요?",
        positive = "그만 할래",
        negative = "더 해볼게",
        onClickOk = {
          showDialog = false
          showExit = true
          AlarmUtils.cancelAlarm(context, timerInfo.id)
        },
        onClickCancel = { showDialog = false },
      )
    }

    val onBack = {
      when (timerUiState) {
        is BattleTimerUiState.Idle -> cancel(navController)
        is BattleTimerUiState.Finish -> reset(navController)
        else -> showDialog = true
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
            timerInfo.copy(remainedTime = timerInfo.remainedTime + 5 * TimerInfo.SECONDS_UNIT)
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
      LoadingScreen()
    }

    if (timerUiState is BattleTimerUiState.Ready) {
      ReadyScreen(timerUiState.countdown)
    }

    if (showExit) {
      ExitScreen(onTimeout = {
        showExit = false

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
  Column(modifier = modifier.padding(top = 36.dp)) {
    ProgressIndicator(
      progress = timerUiState.time / myTimer.time.toFloat(),
      progressText = when (timerUiState) {
        is BattleTimerUiState.Finish -> "YOU WIN!"
        else -> timerUiState.time.timeStr
      },
      label = when (timerUiState) {
        is BattleTimerUiState.Running -> timerUiState.encourageText
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
          is BattleTimerUiState.Finish -> "YOU LOSE"
          else -> battleTimer.remainedTime.timeStr
        },
        label = when {
          displayWin -> "익명의 코뿔소님이 포기하셨습니다"
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
  val buttonColor = when {
    battleTimerUiState.displayBattle -> Gray100
    else -> Color.White
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
          text = "배틀 시작하기",
          color = buttonColor,
          onClick = onClickStart
        )
      is BattleTimerUiState.Loading, is BattleTimerUiState.Ready, is BattleTimerUiState.Running ->
        TimerButton(
          text = "포기하기",
          color = buttonColor,
          onClick = onCancel
        )
      is BattleTimerUiState.Finish -> {
        TimerButton(
          text = "한번 더 하기",
          color = buttonColor,
          onClick = onClickStart
        )
        Spacer(modifier = Modifier.size(20.dp))
        TimerButton(
          text = "종료하기",
          color = buttonColor,
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
        .padding(vertical = 16.dp)
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
    Spacer(modifier = Modifier.size(32.dp))
    Text(
      text = label,
      color = textColor,
      style = MaterialTheme.typography.labelMedium
    )
  }
}

@Composable
private fun LoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.6f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "상대방 탐색 중 입니다...",
      color = Color.White,
      style = MaterialTheme.typography.displaySmall
    )
  }
}

@Composable
private fun ReadyScreen(countdown: Int) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.6f)),
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
      .background(color = Color.Gray.copy(alpha = 0.6f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "다음에는 더 잘할거에요\n또 만나요☺️",
      color = Color.White,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.displaySmall
    )
  }
}