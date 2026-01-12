package com.example.newsapp.domain.util

sealed interface Result<T> {
    data class Success<T>(val msg: String = "", val data: T? = null): Result<T>
    data class Failure<T>(val msg: String = "", val data: T? = null): Result<T>
}