package pony.tothemoon.battletimer.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import pony.tothemoon.battletimer.viewmodel.TimerUiState
import pony.tothemoon.battletimer.viewmodel.TimerViewModel
import pony.tothemoon.battletimer.viewmodel.TimerViewModelFactory

@Composable
fun TimerScreen(
  timerInfo: TimerInfo,
  navController: NavHostController,
  timerViewModel: TimerViewModel = viewModel(factory = TimerViewModelFactory(timerInfo)),
) {
  Box(modifier = Modifier.fillMaxSize()) {
    val timerUiState = timerViewModel.timerUiState

    BackHandler {
      back(navController, timerUiState)
    }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Header(text = timerInfo.title, onClickBack = {
        back(navController, timerUiState)
      })
      Body(
        myTimer = timerInfo,
        battleTimer = timerViewModel.battleTimer,
        timerUiState = timerUiState,
        modifier = Modifier.weight(1f)
      )
      Footer(
        timerUiState,
        onClickStart = {
          timerViewModel.start()
        },
        onCancel = { giveUp(navController) },
        onFinish = { navController.navigateUp() },
        modifier = Modifier.padding(vertical = 20.dp)
      )
    }

    if (timerUiState is TimerUiState.Loading) {
      LoadingScreen()
    }

    if (timerUiState is TimerUiState.Ready) {
      ReadyScreen(timerUiState.countdown)
    }
  }
}

private fun back(navController: NavHostController, timerUiState: TimerUiState) {
  when (timerUiState) {
    is TimerUiState.Idle -> giveUp(navController)
    else -> navController.navigateUp()
  }
}

private fun giveUp(navController: NavHostController) {
  navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set(TimerDestination.TimerList.KEY_IS_CANCEL, true)
  navController.navigateUp()
}

@Composable
private fun Header(text: String, onClickBack: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Filled.ArrowBack,
      contentDescription = null,
      modifier = Modifier
        .size(48.dp)
        .clickable { onClickBack() }
    )
    Text(
      text = text,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.size(48.dp))
  }
}

@Composable
private fun Body(
  myTimer: TimerInfo,
  battleTimer: TimerInfo,
  timerUiState: TimerUiState,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.padding(top = 60.dp)) {
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
          .padding(horizontal = 20.dp)
      )
    }
  }
}

@Composable
private fun Footer(
  timerUiState: TimerUiState,
  onClickStart: () -> Unit,
  onCancel: () -> Unit,
  onFinish: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    when (timerUiState) {
      is TimerUiState.Idle -> OutlinedButton(onClick = onClickStart) {
        Text(text = "배틀 시작하기")
      }
      is TimerUiState.Loading, is TimerUiState.Ready, is TimerUiState.Running -> {
        OutlinedButton(onClick = onCancel) {
          Text(text = "포기하기")
        }
      }
      is TimerUiState.Finish -> {
        OutlinedButton(onClick = onClickStart) {
          Text(text = "한번 더 하기")
        }
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedButton(onClick = onFinish) {
          Text(text = "종료하기")
        }
      }
    }
  }
}

@Composable
private fun Timer(
  title: String,
  totalTime: Long,
  runningTime: Long,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = title)
    Text(text = runningTime.timeStr, modifier = Modifier.padding(vertical = 16.dp))

    val progress by animateFloatAsState(
      targetValue = runningTime / totalTime.toFloat(),
      animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(
      progress = progress,
      modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .clip(shape = RoundedCornerShape(6.dp))
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
    Text(text = "Loading...", color = Color.White)
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
    Text(text = "$countdown", color = Color.White)
  }
}