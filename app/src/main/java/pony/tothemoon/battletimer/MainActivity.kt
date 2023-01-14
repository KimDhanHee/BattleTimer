package pony.tothemoon.battletimer

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import pony.tothemoon.battletimer.model.TimerInfo
import pony.tothemoon.battletimer.ui.components.BattleTimerScreen
import pony.tothemoon.battletimer.ui.components.MyTimerScreen
import pony.tothemoon.battletimer.ui.components.TimerDestination
import pony.tothemoon.battletimer.ui.components.TimerListScreen
import pony.tothemoon.battletimer.ui.theme.BattleTimerTheme
import pony.tothemoon.battletimer.ui.theme.Gray100

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TimerApp()
    }
  }

  @Composable
  fun TimerApp() {
    BattleTimerTheme {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val navController = rememberNavController()

        NavHost(navController, startDestination = TimerDestination.TimerList.route) {
          composable(
            route = TimerDestination.TimerList.route,
          ) { navBackStackEntry ->
            window.statusBarColor = Color.WHITE

            val isCancel by navBackStackEntry.savedStateHandle
              .getStateFlow(TimerDestination.TimerList.KEY_IS_CANCEL, false)
              .collectAsState()
            TimerListScreen(
              isCancel = isCancel,
              onClickTimer = { timerInfo ->
                navController.navigateToSingleTop("${TimerDestination.MyTimer.route}/$timerInfo")
              },
              onClickBattle = { timerInfo ->
                navController.navigateToSingleTop("${TimerDestination.BattleTimer.route}/$timerInfo")
              }
            )
          }
          composable(
            route = TimerDestination.BattleTimer.routeWithArgs,
            arguments = TimerDestination.BattleTimer.arguments
          ) { navBackStackEntry ->
            window.statusBarColor = Gray100.toArgb()

            navBackStackEntry.arguments?.getString(TimerDestination.BattleTimer.timerInfoArg)?.let {
              val timerInfo: TimerInfo = Json.decodeFromString(it)
              BattleTimerScreen(timerInfo, navController)
            }
          }
          composable(
            route = TimerDestination.MyTimer.routeWithArgs,
            arguments = TimerDestination.MyTimer.arguments
          ) { navBackStackEntry ->
            window.statusBarColor = Gray100.toArgb()

            navBackStackEntry.arguments?.getString(TimerDestination.MyTimer.timerInfoArg)?.let {
              val timerInfo: TimerInfo = Json.decodeFromString(it)
              MyTimerScreen(timerInfo, navController)
            }
          }
        }
      }
    }
  }
}

fun NavHostController.navigateToSingleTop(route: String) =
  this.navigate(route) {
    popUpTo(
      this@navigateToSingleTop.graph.findStartDestination().id
    ) {
      saveState = true
    }
    launchSingleTop = true
    restoreState = true
  }