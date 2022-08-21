package com.example.alarmapplication.util

import android.content.Context

fun Float.sp2px(context: Context):Float{
    val fontScale: Float = context.getResources().getDisplayMetrics().scaledDensity
    return (this * fontScale + 0.5f)
}