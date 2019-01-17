package com.cadabra.sociallogin

interface LoginListener<T> {
    fun onSuccess(token: T)
    fun onError(throwable: Throwable)
}