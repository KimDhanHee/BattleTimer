package pony.tothemoon.battletimer.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import pony.tothemoon.battletimer.utils.AndroidUtils

object UserDataStore {
  private val Context.datastore: DataStore<Preferences> by preferencesDataStore("user")

  private val prefShowFeedbackDialog = booleanPreferencesKey("show_feedback_dialog")

  private val prefBattleCount = intPreferencesKey("battle_count")
  val displayFeedbackFlow = AndroidUtils.context.datastore.data.map { preferences ->
    val satisfyCondition = preferences[prefBattleCount]?.let { it >= 3 } ?: false

    (preferences[prefShowFeedbackDialog] ?: true) && satisfyCondition
  }

  suspend fun displayFeedbackDialog() {
    AndroidUtils.context.datastore.edit { preferences ->
      preferences[prefShowFeedbackDialog] = false
    }
  }

  suspend fun finishBattle() {
    AndroidUtils.context.datastore.edit { preferences ->
      preferences[prefBattleCount] = (preferences[prefBattleCount] ?: 0) + 1
    }
  }
}