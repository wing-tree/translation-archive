package com.wing.tree.bruni.translator.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TextStyle.copyAsDp() = with(LocalDensity.current) {
    val fontSize = fontSize.value.dp.toSp()
    val lineHeight = lineHeight.value.dp.toSp()
    val letterSpacing = letterSpacing.value.dp.toSp()

    copy(
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing
    )
}
