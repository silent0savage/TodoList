package com.example.todoList.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.todoList.R

val RobotoFamily = FontFamily(
    Font(R.font.roboto_mono_light, FontWeight.Light),
    Font(R.font.roboto_mono_regular, FontWeight.Normal),
    Font(R.font.roboto_mono_medium, FontWeight.Medium),
    Font(R.font.roboto_mono_bold, FontWeight.Bold),
)

// Set of Material typography styles to start with
val AppTypography = Typography(
    bodyLarge = Typography().bodyLarge.copy(fontFamily = RobotoFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = RobotoFamily),
    titleLarge = Typography().titleLarge.copy(fontFamily = RobotoFamily),
    labelLarge = Typography().labelLarge.copy(fontFamily = RobotoFamily),

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

