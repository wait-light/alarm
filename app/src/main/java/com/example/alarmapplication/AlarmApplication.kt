package com.example.alarmapplication

import android.app.Application
import android.content.Context
import com.example.alarmapplication.ui.AlarmItemComponent
import com.example.alarmapplication.ui.AlarmItemModule
import com.example.alarmapplication.ui.AlarmModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AlarmModule::class,AlarmItemModule::class])
interface AlarmComponent {
    fun inject(applicationContext: Context)
    fun alarmItemComponent(): AlarmItemComponent.Factory
}

class AlarmApplication : Application() {
    companion object {
        lateinit var alarmComponent: AlarmComponent
            public get
            private set
    }

    override fun onCreate() {
        super.onCreate()
        alarmComponent = DaggerAlarmComponent.builder().build().apply {
            inject(applicationContext)
        }
    }

}