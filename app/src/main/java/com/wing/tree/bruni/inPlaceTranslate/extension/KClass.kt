package com.wing.tree.bruni.inPlaceTranslate.extension

import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

//internal fun <T : Any> KClass<T>.getDeclaredField(name: String): Field =
//    java.getDeclaredField(name).apply {
//        isAccessible = true
//    }
//
//internal fun <T : Any> KClass<T>.getDeclaredMethod(name: String, vararg parameterTypes: Class<*>): Method =
//    java.getDeclaredMethod(name, *parameterTypes).apply {
//        isAccessible = true
//    }