package pony.tothemoon.battletimer.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pony.tothemoon.battletimer.R
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.Gray100
import pony.tothemoon.battletimer.ui.theme.Red100
import pony.tothemoon.battletimer.viewmodel.TimerListViewModel

@Composable
fun TimerListScreen(
  isCancel: Boolean = false,
  onTimerItemClick: (TimerInfo) -> Unit = {},
  timerListViewModel: TimerListViewModel = viewModel(),
) {
  LaunchedEffect(isCancel) {
    if (!isCancel) {
      timerListViewModel.refreshBattleTimer()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp)
  ) {
    Title(modifier = Modifier.padding(top = 24.dp, bottom = 30.dp))

    val timerList by timerListViewModel.timerListFlow.collectAsState()
    TimerList(
      battleTimer = timerListViewModel.battleTimer,
      timerList = timerList,
      onTimerItemClick = onTimerItemClick
    )
  }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
  Text(
    text = stringResource(id = R.string.app_name),
    modifier = modifier,
    style = MaterialTheme.typography.titleLarge
  )
}

@Composable
private fun TimerList(
  battleTimer: TimerInfo,
  modifier: Modifier = Modifier,
  timerList: List<TimerInfo> = emptyList(),
  onTimerItemClick: (TimerInfo) -> Unit = {},
) {
  LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item {
      BattleTimer(battleTimer)
    }

    items(timerList) { timerInfo ->
      TimerListItem(timerInfo, onClick = onTimerItemClick)
    }
  }
}

@Composable
private fun BattleTimer(timerInfo: TimerInfo, modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition()
  val colorAnim = infiniteTransition.animateColor(
    initialValue = Gray100,
    targetValue = Red100,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000),
      repeatMode = RepeatMode.Reverse
    )
  ).value
  Card(
    modifier = modifier
      .fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = colorAnim)
  ) {
    TimerListItemContent(timerInfo)
  }
}

@Composable
private fun TimerListItem(
  timerInfo: TimerInfo,
  modifier: Modifier = Modifier,
  onClick: (TimerInfo) -> Unit = {},
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick(timerInfo) },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Gray100)
  ) {
    TimerListItemContent(timerInfo)
  }
}

@Composable
private fun TimerListItemContent(timerInfo: TimerInfo, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(20.dp)
  ) {
    Text(
      text = timerInfo.title,
      color = Color.White,
      style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.size(8.dp))
    Text(
      text = timerInfo.time.timeStr,
      color = Color.White,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}