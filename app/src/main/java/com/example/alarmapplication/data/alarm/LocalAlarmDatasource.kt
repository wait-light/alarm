package com.example.alarmapplication.data

import android.content.Context
import androidx.room.*
import com.example.alarmapplication.data.alarm.AlarmDataSource
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAlarmDatasource : AlarmDataSource {

    private var applicationContext: Context
    private var dataBase: AlarmDataBase

    @Inject
    constructor(applicationContext: Context) {
        this.applicationContext = applicationContext
        this.dataBase = AlarmDataBase.getInstance(applicationContext)
    }

    override fun getAllAlarm(): Flow<List<Alarm>> = dataBase.alarmDao().getAll()
    override fun getAlarm(id: Long): Alarm = dataBase.alarmDao().getAlarm(id)

    override suspend fun addAlarm(alarm: Alarm): Long = dataBase.alarmDao().insert(alarm)

    override suspend fun removeAlarm(alarm: Alarm) = dataBase.alarmDao().delete(alarm)

    override suspend fun updateAlarm(alarm: Alarm) = dataBase.alarmDao().update(alarm)
}

@Dao
interface AlarmDao {
    @Query("select * from alarm")
    fun getAll(): Flow<List<Alarm>>

    @Query("select * from alarm where id = :id")
    fun getAlarm(id: Long): Alarm

    @Insert
    suspend fun insert(alarm: Alarm): Long

    @Delete
    suspend fun delete(alarm: Alarm)

    @Update
    suspend fun update(alarm: Alarm)
}

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@Singleton
abstract class AlarmDataBase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "alarm-db"

        @Volatile
        private var instance: AlarmDataBase? = null
        fun getInstance(context: Context): AlarmDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AlarmDataBase {
            return Room.databaseBuilder(
                context, AlarmDataBase::class.java,
                DATABASE_NAME
            ).build()
        }


    }

    abstract fun alarmDao(): AlarmDao
}

@Module
interface DataSourceModule {
    @Binds
    fun bindAlarmDataSource(dataSource: LocalAlarmDatasource): AlarmDataSource
}
