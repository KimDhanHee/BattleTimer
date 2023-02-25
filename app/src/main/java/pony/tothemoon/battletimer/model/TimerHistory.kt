package pony.tothemoon.battletimer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@Entity(tableName = "timer_history")
data class TimerHistory(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val date: LocalDate = Clock.System.now().today(),
  val time: Long,
  val isWin: Boolean,
  val type: TimerInfo.Type,
) {
  companion object {
    const val DATE_FORMAT = "yyyy-MM-dd"
  }
}