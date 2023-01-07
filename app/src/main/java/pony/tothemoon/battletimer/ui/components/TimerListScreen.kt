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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.model.timeStr
import pony.tothemoon.battletimer.ui.theme.BattleTimerTheme
import pony.tothemoon.battletimer.ui.theme.Red900
import pony.tothemoon.battletimer.ui.theme.White900
import pony.tothemoon.battletimer.viewmodel.TimerListViewModel

@Composable
fun TimerListScreen(onTimerItemClick: (TimerInfo) -> Unit = {}, viewmodel: TimerListViewModel = viewModel()) {
  val timerList by viewmodel.timerListFlow.collectAsState()
  TimerList(modifier = Modifier.padding(20.dp), viewmodel.battleTimer, timerList, onTimerItemClick)
}

@Composable
fun TimerList(
  modifier: Modifier = Modifier,
  battleTimer: TimerInfo,
  timerList: List<TimerInfo> = emptyList(),
  onTimerItemClick: (TimerInfo) -> Unit = {}
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
    initialValue = White900,
    targetValue = Red900,
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

@Preview
@Composable
fun BattleTimerPreview() {
  BattleTimerTheme {
    TimerListItem(timerInfo = TimerInfo(title = "Battle Timer"))
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
  ) {
    TimerListItemContent(timerInfo)
  }
}

@Preview
@Composable
fun TimerListItemPreview() {
  BattleTimerTheme {
    TimerListItem(timerInfo = TimerInfo(title = "Battle Timer"))
  }
}

@Composable
private fun TimerListItemContent(timerInfo: TimerInfo, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(20.dp)
  ) {
    Text(text = timerInfo.title)
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = timerInfo.time.timeStr)
  }
}