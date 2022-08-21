package com.example.alarmapplication.util

import android.content.Context
import android.widget.Toast

fun String.shotToast(context: Context) {
    toast(context, Toast.LENGTH_SHORT)
}

fun String.longToast(context: Context) {
    toast(context, Toast.LENGTH_LONG)
}

fun String.toast(context: Context, duration: Int) {
    Toast.makeText(context, this, duration).show()
}