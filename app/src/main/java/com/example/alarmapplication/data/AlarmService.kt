package com.example.alarmapplication.data

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.domain.AlarmDomain
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmService : Service() {
    companion object {
        private const val TAG = "AlarmService"
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
        const val NAME = "ALARM_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "notify"
        const val ACTION_STOP = "stop"
        const val DEBUG = true
        const val STOP_REQUEST_CODE = 2
    }

    private var notificationManager: NotificationManager? = null
    private var ringtone: Ringtone? = null

    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory

    @Inject
    lateinit var alarmDomain: AlarmDomain

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        AlarmApplication.ALARM_COMPONENT.alarmItemComponent().create().inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        intent?.apply {
            when (this.action) {
                ACTION_STOP -> stop(baseContext)
                ACTION_START -> getLongExtra(AlarmReceiver.ALARM_ID_KEY, 0L)
                    ?.let {
                        if (it == 0L) {
                            throw Exception("must have a valid alarm")
                        }
                        GlobalScope.launch {
                            val alarm = alarmDomain.alarmRepository.getAlarm(it)
                            alarmRepeatStrategyFactory.getAlarmStrategy(alarm.repeat)
                                ?.onAlarm(alarm)
                            start(baseContext, alarm)
                        }
                    }
            }
        }
        if (DEBUG) {
            Log.e(TAG, "onStartCommand: xxxxx${this.hashCode()} ${this.notificationManager}")
        }
        return result

    }

    private fun start(context: Context, alarm: Alarm) {
        context?.apply {
            notification(context, alarm)
            playRing(context, alarm)
        }
    }

    private fun notification(context: Context, alarm: Alarm) {
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(
                    ALARM_CHANNEL_ID,
                    NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.setSound(null, null)
            notificationManager!!.createNotificationChannel(notificationChannel)
            notificationManager!!.notify(
                NOTIFICATION_ID,
                createNotification(context, alarm).apply {
                    flags = flags or Notification.FLAG_NO_CLEAR
                }
            )
        }
    }

    private fun playRing(context: Context, alarm: Alarm) {
        val ringUrl =
            if (alarm.ring.isNullOrEmpty()) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            else Uri.parse(alarm.ring)
        Log.e(TAG, "playRing: ${ringUrl}  alarm ${alarm}")
        ringtone = RingtoneManager.getRingtone(context, ringUrl).apply { play() }
    }

    private fun stopNotification(context: Context) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()
    }

    private fun stop(context: Context) {
        stopNotification(context)
        stopRing(context)
        stopSelf()
    }

    private fun stopRing(context: Context) {
        ringtone?.stop()
    }

    private fun createNotification(
        context: Context,
        alarm: Alarm
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            ALARM_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.alarm)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setContent(createRemoteViews(context, alarm))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationBuilder.build()
    }

    private fun createRemoteViews(context: Context, alarm: Alarm): RemoteViews {
        return RemoteViews(context.packageName, R.layout.alarm_notification).apply {
            setOnClickPendingIntent(
                R.id.check,
                PendingIntent.getService(
                    context,
                    STOP_REQUEST_CODE,
                    Intent(context, AlarmService::class.java).setAction(ACTION_STOP),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            setTextViewText(R.id.remark, alarm.remark)
        }
    }
}