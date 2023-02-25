package pony.tothemoon.battletimer.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pony.tothemoon.battletimer.utils.AndroidUtils

@Database(entities = [TimerHistory::class], version = 1)
@TypeConverters(TimerTypeConverter::class)
abstract class TimerDatabase : RoomDatabase() {
  abstract fun timerDao(): TimerDao

  companion object {
    @Volatile
    private var INSTANCE: TimerDatabase? = null

    val timerDao: TimerDao
      get() = database.timerDao()

    private val database: TimerDatabase
      get() = INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          AndroidUtils.application,
          TimerDatabase::class.java,
          "timer_database"
        ).fallbackToDestructiveMigration().build()

        INSTANCE = instance

        instance
      }
  }
}