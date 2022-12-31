package pony.tothemoon.battletimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pony.tothemoon.battletimer.ui.components.TimerDestination
import pony.tothemoon.battletimer.ui.components.TimerListScreen
import pony.tothemoon.battletimer.ui.theme.BattleTimerTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TimerApp()
    }
  }
}

@Composable
fun TimerApp() {
  BattleTimerTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      val navController = rememberNavController()

      NavHost(navController, startDestination = TimerDestination.TimerList.route) {
        composable(route = TimerDestination.TimerList.route) {
          TimerListScreen()
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  BattleTimerTheme {
    TimerApp()
  }
}