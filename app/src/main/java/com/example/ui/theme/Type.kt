package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val AlataFontFamily = FontFamily(
  Font(R.font.alata, FontWeight.Normal),
)

val InstrumentSansFontFamily = FontFamily(
  Font(R.font.instrument_sans, FontWeight.Normal),
  Font(R.font.instrument_sans, FontWeight.Medium),
  Font(R.font.instrument_sans, FontWeight.SemiBold),
  Font(R.font.instrument_sans, FontWeight.Bold),
)

val SourceCodeProFontFamily = FontFamily(
  Font(R.font.source_code_pro, FontWeight.Medium),
  Font(R.font.source_code_pro, FontWeight.SemiBold),
)

val MonoMetricMediumStyle = TextStyle(
  fontFamily = SourceCodeProFontFamily,
  fontWeight = FontWeight.Medium,
  fontSize = 13.sp,
  lineHeight = 18.sp,
  letterSpacing = 0.sp,
  fontFeatureSettings = "tnum",
)

val TabularMetricStyle = MonoMetricMediumStyle

val MonoMetricLargeStyle = TextStyle(
  fontFamily = SourceCodeProFontFamily,
  fontWeight = FontWeight.SemiBold,
  fontSize = 24.sp,
  lineHeight = 30.sp,
  letterSpacing = (-0.25).sp,
  fontFeatureSettings = "tnum",
)

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = AlataFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 40.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.5).sp,
  ),
  displaySmall = TextStyle(
    fontFamily = AlataFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 28.sp,
    lineHeight = 34.sp,
    letterSpacing = (-0.25).sp,
  ),
  headlineLarge = TextStyle(
    fontFamily = AlataFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp,
  ),
  headlineMedium = TextStyle(
    fontFamily = AlataFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp,
  ),
  titleLarge = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
  ),
  titleMedium = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp,
  ),
  bodyLarge = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
  ),
  bodyMedium = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
  ),
  bodySmall = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.sp,
  ),
  labelLarge = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
  ),
  labelMedium = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp,
  ),
  labelSmall = TextStyle(
    fontFamily = InstrumentSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.25.sp,
  ),
)
