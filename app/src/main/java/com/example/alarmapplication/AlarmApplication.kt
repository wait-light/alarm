package com.example.alarmapplication

import android.app.Application
import com.example.alarmapplication.data.AlarmRepeatStrategy
import com.example.alarmapplication.data.AlarmStrategyModule
import com.example.alarmapplication.data.DataSourceModule
import com.example.alarmapplication.ui.AlarmItemComponent
import com.example.alarmapplication.ui.AlarmItemModule
import com.example.alarmapplication.ui.AlarmModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AlarmModule::class, AlarmItemModule::class, DataSourceModule::class, AlarmStrategyModule::class])
interface AlarmComponent {
    fun alarmItemComponent(): AlarmItemComponent.Factory
    fun alarmRepeatStrategies(): Map<Int, AlarmRepeatStrategy>
}

class AlarmApplication : Application() {
    companion object {
        lateinit var ALARM_COMPONENT: AlarmComponent
            public get
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ALARM_COMPONENT =
            DaggerAlarmComponent.builder().alarmModule(AlarmModule(applicationContext)).build()
    }

}