package com.example.alarmapplication.data

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.MainActivity2
import com.example.alarmapplication.R
import com.example.alarmapplication.domain.AlarmDomain
import com.example.alarmapplication.ui.AlarmListFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

class AlarmService : Service() {
    companion object {
        private const val TAG = "AlarmService"
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
        const val NAME = "ALARM_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "notify"
        const val ACTION_STOP = "stop"
    }

    private var notificationManager: NotificationManager? = null
    private var ringtone: Ringtone? = null
    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory
    private var count = 0

    @Inject
    lateinit var alarmDomain: AlarmDomain

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        AlarmApplication.ALARM_COMPONENT.alarmItemComponent().create().inject(this)
        super.onCreate()
        start(baseContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        intent?.apply {
            when (this.action) {
                ACTION_STOP -> stop(baseContext)
//                else -> start(applicationContext)
            }
        }
            ?: start(baseContext)
        if (count++ == 0) {
//            alarmRepeatStrategyFactory.getAlarmStrategy()
            intent?.getLongExtra(AlarmReceiver.ALARM_ID_KEY, 0L)
                ?.let {
                    GlobalScope.launch {
                        val alarm = alarmDomain.alarmRepository.getAlarm(it)
                        alarmRepeatStrategyFactory.getAlarmStrategy(alarm.repeat)?.onAlarm(alarm)
                    }
                }
        }
        Log.e(TAG, "onStartCommand: xxxxx${this.hashCode()} ${this.notificationManager}")
        return result

    }

    private fun start(context: Context) {
        context?.apply {
            notification(context)
            playRing(context)

        }
    }

    private fun notification(context: Context) {
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
                getChannelNotificationQ(context).apply {
                    flags = flags or Notification.FLAG_NO_CLEAR
                }
            )
        }
    }

    private fun playRing(context: Context) {
        val ringUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
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
//        val ringUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        RingtoneManager.getRingtone(context, ringUrl).stop()
    }

    private fun getChannelNotificationQ(
        context: Context
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            AlarmReceiver.ALARM_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.alarm)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_ALARM)
            .setAutoCancel(false)
//            .setContentText("啦啦啦")
            .setContent(createRemoteViews(context))
//            .setFullScreenIntent(
//                PendingIntent.getActivity(
//                    context,
//                    1,
//                    Intent(context, MainActivity2::class.java).apply {
//                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    },
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                ), true
//            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationBuilder.build()
    }

    private fun createRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.alarm_notification).apply {
            setOnClickPendingIntent(
                R.id.check,
                PendingIntent.getService(
                    context,
                    2,
                    Intent(context, AlarmService::class.java).setAction(ACTION_STOP),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }
}