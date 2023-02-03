package pony.tothemoon.battletimer.utils

import android.app.Application
import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

object AndroidUtils {
  lateinit var application: Application
    private set

  fun initialize(context: Context?) {
    if (context == null) return

    application = context.applicationContext as Application
  }

  val context: Context
    get() = application

  fun string(@StringRes resId: Int, vararg formatArgs: Any): String = when (resId) {
    0 -> ""
    else -> context.resources.getString(resId, *formatArgs)
  }

  @JvmStatic
  fun stringArray(@ArrayRes resId: Int): Array<String> = when (resId) {
    0 -> emptyArray()
    else -> context.resources.getStringArray(resId)
  }

  var isBackground: Boolean = true
    private set

  fun resume() { isBackground = false }
  fun pause() { isBackground = true }
}