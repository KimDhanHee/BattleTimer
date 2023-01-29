package pony.tothemoon.battletimer.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object MediaVibrator {
  private val Context.vibrator: Vibrator
    get() = this.getSystemService(Vibrator::class.java)

  private const val DEFAULT_VIBRATING_PERIOD = 500L
  private val DEFAULT_TIMING = longArrayOf(DEFAULT_VIBRATING_PERIOD, DEFAULT_VIBRATING_PERIOD)

  /**
   * start 기본 동작 진동 무한 반복
   * @param timings array 의 첫 번째 값은 첫 진동 시작하기까지 걸리는 시간, 다음 값들은 진동 길이
   * @param repeat repeat == 0 : 무한 반복, repeat == -1 : 1회 재생 (vibrateOnce 활용)
   */
  fun vibrateRepeat(context: Context, timings: LongArray = DEFAULT_TIMING, repeat: Int = 0) {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
        context.vibrator.vibrate(VibrationEffect.createWaveform(timings, repeat))
      else -> context.vibrator.vibrate(timings, repeat)
    }
  }

  fun stop(context: Context) {
    context.vibrator.cancel()
  }

  fun vibrateOnce(context: Context, period: Long = DEFAULT_VIBRATING_PERIOD) {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
        context.vibrator.vibrate(
          VibrationEffect.createOneShot(period, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      else -> context.vibrator.vibrate(longArrayOf(period, period), -1)
    }
  }
}