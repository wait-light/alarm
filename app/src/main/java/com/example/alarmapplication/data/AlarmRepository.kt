package com.example.alarmapplication.data

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import java.io.Serializable
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Entity
@TypeConverters(LocalTimeConvert::class)
data class Alarm(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var localTime: LocalTime,
    @AlarmRepeat var repeat: Int,
    var ring: String,
    var vibration: Boolean,
    var autoDelete: Boolean,
    var remark: String,
    var enable: Boolean
) : Serializable

class LocalTimeConvert {
    @TypeConverter
    fun object2String(obj: LocalTime): Long {
        return obj.toSecondOfDay().toLong()
    }

    @TypeConverter
    fun string2Object(localTime: Long): LocalTime {
        return LocalTime.ofSecondOfDay(localTime)
    }
}

@Singleton
class AlarmRepository @Inject constructor() {
    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var dataSource: AlarmDataSource

    fun getAllAlarm(): Flow<List<Alarm>> = dataSource.getAllAlarm()

    suspend fun addAlarm(alarm: Alarm): Long = dataSource.addAlarm(alarm)

    suspend fun removeAlarm(alarm: Alarm) = dataSource.removeAlarm(alarm)

    suspend fun updateAlarm(alarm: Alarm) = dataSource.updateAlarm(alarm)

    fun getAlarm(id: Long): Alarm = dataSource.getAlarm(id)

}