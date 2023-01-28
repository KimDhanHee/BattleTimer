package pony.tothemoon.battletimer.utils

import android.app.Application
import android.content.Context
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

  fun string(@StringRes resId: Int): String = when (resId) {
    0 -> ""
    else -> context.resources.getString(resId)
  }
}