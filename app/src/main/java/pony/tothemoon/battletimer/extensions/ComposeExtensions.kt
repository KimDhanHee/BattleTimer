package pony.tothemoon.battletimer.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun onLifecycleEvent(onEvent: (event: Lifecycle.Event) -> Unit) {
  val eventHandler by rememberUpdatedState(onEvent)
  val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

  DisposableEffect(lifecycleOwner) {
    val lifecycle = lifecycleOwner.lifecycle
    val observer = LifecycleEventObserver { _, event ->
      eventHandler(event)
    }

    lifecycle.addObserver(observer)
    onDispose {
      lifecycle.removeObserver(observer)
    }
  }
}