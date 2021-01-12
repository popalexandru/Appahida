package com.example.appahida

interface onVersionChanged {
    fun onVersionChanged(version : Int)
    fun startLoading()
    fun stopLoading()
}