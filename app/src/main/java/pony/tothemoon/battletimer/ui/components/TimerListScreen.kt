package pony.tothemoon.battletimer.ui.components

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
import pony.tothemoon.battletimer.viewmodel.TimerListViewModel

@Composable
fun TimerListScreen(viewmodel: TimerListViewModel = viewModel()) {
  val timerList by viewmodel.timerListFlow.collectAsState()
  TimerList(modifier = Modifier.padding(20.dp), timerList = timerList)
}

@Composable
fun TimerList(modifier: Modifier = Modifier, timerList: List<TimerInfo> = emptyList()) {
  LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(timerList) { timerInfo ->
      TimerListItem(timerInfo)
    }
  }
}

@Composable
fun TimerListItem(timerInfo: TimerInfo) {
  Card(
    modifier = Modifier
      .fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
  ) {
    Column(
      modifier = Modifier.padding(20.dp)
    ) {
      Text(text = timerInfo.title)
      Spacer(modifier = Modifier.size(8.dp))
      Text(text = timerInfo.time.timeStr)
    }
  }
}

@Preview
@Composable
fun TimerListItemPreview() {
  BattleTimerTheme {
    TimerListItem(timerInfo = TimerInfo(title = "Battle Timer"))
  }
}