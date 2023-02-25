package pony.tothemoon.battletimer.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Dao
abstract class TimerDao {
  @Query("SELECT COUNT(*) FROM timer_history WHERE date = :date AND isWin = 1 AND type = :type")
  abstract fun getWinCountOfDate(
    date: LocalDate = Clock.System.now().today(),
    type: TimerInfo.Type = TimerInfo.Type.BATTLE,
  ): Flow<Int>

  @Query("SELECT SUM(time) FROM timer_history WHERE date = :date")
  abstract fun getTimeOfDate(date: LocalDate = Clock.System.now().today()): Flow<Long>

  @Insert
  abstract suspend fun save(timerHistory: TimerHistory)
}

class TimerTypeConverter {
  @TypeConverter
  fun dateStrToDate(dateStr: String): LocalDate = dateStr.toLocalDate()

  @TypeConverter
  fun dateToDateStr(date: LocalDate): String = date.format(TimerHistory.DATE_FORMAT)
}

fun LocalDate.format(formatStr: String): String =
  DateTimeFormatter.ofPattern(formatStr).format(this.toJavaLocalDate())

fun Instant.today(): LocalDate = this.toLocalDateTime(TimeZone.currentSystemDefault()).date