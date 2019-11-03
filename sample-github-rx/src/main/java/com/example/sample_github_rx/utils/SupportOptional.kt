package com.example.sample_github_rx.utils

sealed class SupportOptional<out T: Any>(private val __value: T?) {

    val isEmpty: Boolean
        get() = null == __value

    val value: T
        get() = checkNotNull(__value)

}

class Empty<out T: Any>: SupportOptional<T>(null)

class Some<out T: Any>(value: T): SupportOptional<T>(value)

inline fun <reified T: Any> emptyOptional() = Empty<T>()

inline fun <reified T: Any> optionalOf(value: T?)
    = if(value != null) Some(value) else Empty<T>()