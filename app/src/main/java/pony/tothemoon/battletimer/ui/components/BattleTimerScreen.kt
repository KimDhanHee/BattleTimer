package pony.tothemoon.battletimer.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.Gray100
import pony.tothemoon.battletimer.ui.theme.White900
import pony.tothemoon.battletimer.viewmodel.BattleTimerUiState
import pony.tothemoon.battletimer.viewmodel.BattleTimerViewModel
import pony.tothemoon.battletimer.viewmodel.BattleTimerViewModelFactory

@Composable
fun BattleTimerScreen(
  timerInfo: TimerInfo,
  navController: NavHostController,
  viewmodel: BattleTimerViewModel = viewModel(factory = BattleTimerViewModelFactory(timerInfo)),
) {
  Box(modifier = Modifier.fillMaxSize()) {
    val timerUiState = viewmodel.timerUiState

    BackHandler { back(navController, timerUiState) }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(color = Gray100),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Header(text = timerInfo.title, onClickBack = { back(navController, timerUiState) })
      Body(
        myTimer = timerInfo,
        battleTimer = viewmodel.battleTimer,
        timerUiState = timerUiState,
        modifier = Modifier.weight(1f)
      )
      Footer(
        timerUiState,
        onClickStart = { viewmodel.start() },
        onCancel = { giveUp(navController) },
        onFinish = { victory(navController) },
      )
    }

    if (timerUiState is BattleTimerUiState.Loading) {
      LoadingScreen()
    }

    if (timerUiState is BattleTimerUiState.Ready) {
      ReadyScreen(timerUiState.countdown)
    }
  }
}

private fun back(navController: NavHostController, timerUiState: BattleTimerUiState) {
  when (timerUiState) {
    is BattleTimerUiState.Finish -> victory(navController)
    else -> giveUp(navController)
  }
}

private fun giveUp(navController: NavHostController) {
  navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set(TimerDestination.TimerList.KEY_IS_CANCEL, true)
  navController.navigateUp()
}

private fun victory(navController: NavHostController) {
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
    Timer(
      title = "My Timer",
      totalTime = myTimer.time,
      runningTime = timerUiState.time,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 20.dp)
    )

    if (timerUiState.displayBattle) {
      val battleTime by remember { mutableStateOf(battleTimer.time) }
      Timer(
        title = "Battle Timer",
        totalTime = battleTime,
        runningTime = battleTimer.time,
        modifier = Modifier
          .weight(1f)
          .background(color = Color.White)
          .padding(horizontal = 20.dp),
        titleColor = Gray100,
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
private fun Timer(
  title: String,
  totalTime: Long,
  runningTime: Long,
  modifier: Modifier = Modifier,
  titleColor: Color = Color.White,
  timerColor: Color = Color.White,
) {
  Column(
    modifier = modifier
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.size(20.dp))
    Text(
      text = title,
      color = titleColor,
      style = MaterialTheme.typography.labelMedium
    )
    Text(
      text = runningTime.timeStr,
      modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth(),
      color = timerColor,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.displayLarge
    )

    val progress by animateFloatAsState(
      targetValue = runningTime / totalTime.toFloat(),
      animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(
      progress = progress,
      modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .clip(shape = RoundedCornerShape(6.dp)),
      color = timerColor,
      trackColor = White900
    )
  }
}

@Composable
private fun LoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Gray.copy(alpha = 0.5f)),
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
      .background(color = Color.Gray.copy(alpha = 0.5f)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$countdown",
      color = Color.White,
      style = MaterialTheme.typography.displayLarge
    )
  }
}