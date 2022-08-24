package com.example.alarmapplication.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.alarmapplication.MainActivity2
import com.example.alarmapplication.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
        const val NAME = "ALARM_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        private const val TAG = "AlarmReceiver"
        const val ACTION_START = "notify"
        const val ACTION_STOP = "stop"
    }

    private var notificationManager: NotificationManager? = null
    private var ringtone: Ringtone? = null
    override fun onReceive(context: Context?, intent: Intent?) {
//        context?.apply {
//            startActivity(Intent(this, MainActivity2::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            })
//        }
//        intent?.apply {
//            Log.d(TAG, "onReceive: xx")
//            when (intent.action) {
//                ACTION_START -> start(context!!)
//                ACTION_STOP -> stop(context!!)
//            }
//        }
        context?.startService(Intent(context, AlarmService::class.java))
        Log.e(TAG, "onReceive: xxxxxx${this.hashCode()}")

    }

    private fun start(context: Context) {
        context?.apply {
            notification(context)
//            playRing(context)
        }
    }

    private fun notification(context: Context) {
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(ALARM_CHANNEL_ID, NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(null, null)
            notificationManager!!.createNotificationChannel(notificationChannel)
            notificationManager!!.notify(
                NOTIFICATION_ID,
                getChannelNotificationQ(context).apply {
                    flags = flags or Notification.FLAG_NO_CLEAR
                }
            )
        }
    }

    private fun playRing(context: Context) {
        val ringUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(context, ringUrl).play()
    }

    private fun stopNotification(context: Context) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()
    }

    private fun stop(context: Context) {
        stopNotification(context)
        stopRing(context)
    }

    private fun stopRing(context: Context) {
        val ringUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(context, ringUrl).stop()
    }

    private fun getChannelNotificationQ(
        context: Context
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_ALARM)
            .setAutoCancel(false)
//            .setContentText("啦啦啦")
            .setContent(createRemoteViews(context))
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, MainActivity2::class.java).apply {
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                ), true
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationBuilder.build()
    }

    private fun createRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.alarm_notification).apply {
            setOnClickPendingIntent(
                R.id.check,
                PendingIntent.getBroadcast(
                    context,
                    2,
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }
}