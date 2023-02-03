package pony.tothemoon.battletimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun Header(text: String, onClickBack: () -> Unit) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .padding(all = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Filled.ArrowBackIos,
      contentDescription = null,
      modifier = Modifier
        .size(48.dp)
        .padding(8.dp)
        .clickable { onClickBack() },
      tint = Color.White
    )
    Text(
      text = text,
      modifier = Modifier.weight(1f),
      color = Color.White,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.size(48.dp))
  }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmDialog(
  title: String,
  positive: String,
  negative: String,
  onClickOk: () -> Unit,
  onClickCancel: () -> Unit,
) {
  Dialog(
    onDismissRequest = onClickCancel,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Column(
      modifier = Modifier
        .padding(42.dp)
        .background(color = Color.White, shape = RoundedCornerShape(6.dp))
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge
      )
      Spacer(modifier = Modifier.size(32.dp))
      Row(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = positive,
          modifier = Modifier
            .weight(1f)
            .clickable { onClickOk() },
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelMedium
        )
        Text(
          text = negative,
          modifier = Modifier
            .weight(1f)
            .clickable { onClickCancel() },
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}