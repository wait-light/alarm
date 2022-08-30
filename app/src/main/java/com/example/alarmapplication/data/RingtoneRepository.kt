package com.example.alarmapplication.data

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.example.alarmapplication.ui.AlarmAddFragment

data class Ringtone(var title: String, var url: Uri)

suspend fun getRingtoneList(context: Context): List<Ringtone> {
    val ringtoneList = mutableListOf<Ringtone>()
    val ringtoneManager = RingtoneManager(context)
    ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
    val cursor = ringtoneManager.cursor
    while (cursor.moveToNext()) {
        ringtoneList.add(
            Ringtone(
                ringtoneManager.getRingtone(cursor.position).getTitle(context),
                ringtoneManager.getRingtoneUri(cursor.position)
            )
        )
    }
    return ringtoneList
}