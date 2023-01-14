package pony.tothemoon.battletimer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pony.tothemoon.battletimer.R

val Typography = Typography(
  displayLarge = TextStyle(
    fontSize = 60.sp,
    fontWeight = FontWeight.SemiBold,
    fontFamily = FontFamily(
      Font(R.font.rajdhani, FontWeight.SemiBold)
    )
  ),
  displaySmall = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.SemiBold,
  ),
  titleLarge = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold
  ),
  bodyLarge = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.SemiBold,
    fontFamily = FontFamily(
      Font(R.font.rajdhani, FontWeight.SemiBold)
    ),
  ),
  bodyMedium = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold
  ),
  labelMedium = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium
  )
)