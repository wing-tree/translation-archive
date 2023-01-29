package com.wing.tree.bruni.translator.extension

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wing.tree.bruni.core.constant.ZERO

fun Modifier.paddingStart(start: Dp) = padding(start = start)
fun Modifier.paddingTop(top: Dp) = padding(top = top)
fun Modifier.paddingEnd(end: Dp) = padding(end = end)
fun Modifier.paddingBottom(bottom: Dp) = padding(bottom = bottom)
fun Modifier.paddingHorizontal(horizontal: Dp) = padding(horizontal, ZERO.dp)
fun Modifier.paddingVertical(vertical: Dp) = padding(ZERO.dp, vertical)
