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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
  onClickSingle: (TimerInfo) -> Unit = {},
  onClickBattle: (TimerInfo) -> Unit = {},
  timerListViewModel: TimerListViewModel = viewModel(),
) {
  LaunchedEffect(isCancel) {
    if (!isCancel) {
      timerListViewModel.refreshOtherUserTimer()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(start = 20.dp, end = 20.dp)
  ) {
    Title(modifier = Modifier.padding(top = 24.dp, bottom = 30.dp))

    val timerList = timerListViewModel.presetTimers
    TimerList(
      otherUserTimer = timerListViewModel.otherUserTimer,
      timerArray = timerList,
      onClickSingle = { onClickSingle(it.copy(type = TimerInfo.Type.SINGLE)) },
      onClickBattle = { onClickBattle(it.copy(type = TimerInfo.Type.BATTLE)) },
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
  otherUserTimer: TimerInfo,
  modifier: Modifier = Modifier,
  timerArray: Array<TimerInfo> = emptyArray(),
  onClickSingle: (TimerInfo) -> Unit = {},
  onClickBattle: (TimerInfo) -> Unit = {},
) {
  var showOtherUserTimer by rememberSaveable { mutableStateOf(true) }

  LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(bottom = 20.dp)
  ) {
    if (showOtherUserTimer) {
      item {
        OtherUserTimer(otherUserTimer, onClickClose = { showOtherUserTimer = false })
      }
    }

    items(timerArray) { timerInfo ->
      Row {
        TimerListItem(
          title = stringResource(id = R.string.timer_list_single_timer),
          subTitle = timerInfo.time.timeStr,
          painter = painterResource(id = R.drawable.ic_single_timer),
          modifier = Modifier.weight(1f),
          onClick = { onClickSingle(timerInfo) },
        )
        Spacer(modifier = Modifier.size(8.dp))
        TimerListItem(
          title = stringResource(id = R.string.timer_list_battle_timer),
          subTitle = timerInfo.time.timeStr,
          painter = painterResource(id = R.drawable.ic_battle_timer),
          modifier = Modifier.weight(1f),
          onClick = { onClickBattle(timerInfo) },
        )
      }
    }
  }
}

@Composable
private fun OtherUserTimer(
  timerInfo: TimerInfo,
  modifier: Modifier = Modifier,
  onClickClose: () -> Unit,
) {
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
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = colorAnim)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = timerInfo.title,
        modifier = Modifier.weight(1f),
        color = Color.White,
        style = MaterialTheme.typography.bodySmall
      )
      Spacer(modifier = Modifier.size(2.dp))
      Text(
        text = timerInfo.time.timeStr,
        color = Color.White,
        style = MaterialTheme.typography.bodySmall
      )
      Spacer(modifier = Modifier.size(4.dp))
      Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .clickable { onClickClose() },
        tint = Color.White
      )
    }
  }
}

@Composable
private fun TimerListItem(
  title: String,
  subTitle: String,
  painter: Painter,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Gray100)
  ) {
    Column(modifier = Modifier.padding(start = 12.dp, top = 20.dp, end = 12.dp, bottom = 36.dp)) {
      Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.size(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          painter = painter,
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = subTitle,
          color = Color.White,
          style = MaterialTheme.typography.titleMedium
        )
      }
    }
  }
}