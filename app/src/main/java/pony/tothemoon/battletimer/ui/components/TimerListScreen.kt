package pony.tothemoon.battletimer.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
  onClickTimer: (TimerInfo) -> Unit = {},
  onClickBattle: (TimerInfo) -> Unit = {},
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

    val timerList = timerListViewModel.presetTimers
    TimerList(
      battleTimer = timerListViewModel.battleTimer,
      timerArray = timerList,
      onClickTimer = onClickTimer,
      onClickBattle = onClickBattle,
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
  timerArray: Array<TimerInfo> = emptyArray(),
  onClickTimer: (TimerInfo) -> Unit = {},
  onClickBattle: (TimerInfo) -> Unit = {},
) {
  LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(bottom = 20.dp)
  ) {
    item {
      BattleTimer(battleTimer)
    }

    items(timerArray) { timerInfo ->
      TimerListItem(
        timerInfo,
        onClickTimer = onClickTimer,
        onClickBattle = onClickBattle
      )
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
    TimerListItemContent(timerInfo, modifier.padding(20.dp))
  }
}

@Composable
private fun TimerListItem(
  timerInfo: TimerInfo,
  modifier: Modifier = Modifier,
  onClickBattle: (TimerInfo) -> Unit = {},
  onClickTimer: (TimerInfo) -> Unit = {},
) {
  Card(
    modifier = modifier
      .fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Gray100)
  ) {
    Row(modifier = Modifier
      .fillMaxWidth()
      .padding(20.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TimerListItemContent(timerInfo, modifier = Modifier.weight(1f))

      Icon(
        painter = painterResource(id = R.drawable.ic_timer_32_32),
        contentDescription = null,
        modifier = Modifier
          .padding(8.dp)
          .clickable { onClickTimer(timerInfo) },
        tint = Color.White
      )
      Spacer(modifier = Modifier.size(8.dp))
      Icon(
        painter = painterResource(id = R.drawable.ic_swords_32),
        contentDescription = null,
        modifier = Modifier
          .padding(8.dp)
          .clickable { onClickBattle(timerInfo) },
        tint = Color.White
      )
    }
  }
}

@Composable
private fun TimerListItemContent(timerInfo: TimerInfo, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
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