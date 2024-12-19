package fr.lehautcambara.astronomicon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.lehautcambara.astronomicon.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.uglyqua, FontWeight.Normal),
            Font(R.font.uglyqua_italic, FontWeight.Normal, FontStyle.Italic)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.uglyqua, FontWeight.Normal),
            Font(R.font.uglyqua_italic, FontWeight.Normal, FontStyle.Italic)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.uglyqua, FontWeight.Normal),
            Font(R.font.uglyqua_italic, FontWeight.Normal, FontStyle.Italic)),
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.uglyqua, FontWeight.Normal),
            Font(R.font.uglyqua_italic, FontWeight.Normal, FontStyle.Italic)),
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )


)