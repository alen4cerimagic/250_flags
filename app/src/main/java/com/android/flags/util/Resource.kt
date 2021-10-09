package com.android.flags.util

data class Resource<out T>(val status: Status, val data: T?)

enum class Status {
    SUCCESS,
    SERVER_ERROR,
    INTERNAL_ERROR,
    LOADING
}