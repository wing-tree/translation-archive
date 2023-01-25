package com.wing.tree.bruni.translator.extension

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wing.tree.bruni.core.constant.ZERO

fun Modifier.paddingHorizontal(horizontal: Dp) = padding(horizontal, ZERO.dp)
