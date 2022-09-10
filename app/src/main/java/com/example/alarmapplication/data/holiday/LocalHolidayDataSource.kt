package com.example.alarmapplication.data.holiday

import android.content.Context
import androidx.room.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

class LocalHolidayDataSource @Inject constructor() : HolidayDataSource {
    @Inject
    lateinit var localHolidayDao: LocalHolidayDao
    override suspend fun getHolidayStatus(date: String): HolidayStatus =
        localHolidayDao.getHolidayStatus(date)

    override suspend fun getYearHolidayStatus(year: Int): List<HolidayStatus> =
        localHolidayDao.getYearHolidayStatus(year)

    suspend fun getMonthHolidayStatus(year: Int, mouth: Int): List<HolidayStatus> =
        localHolidayDao.getMonthHolidayStatus(year, mouth)

    fun insetHolidayStatus(vararg holidayStatues: HolidayStatus) =
        localHolidayDao.insetHolidayStatus(*holidayStatues)

    fun deleteHolidayStatus(holidayStatus: HolidayStatus) =
        localHolidayDao.deleteHolidayStatus(holidayStatus)
}

@Dao
interface LocalHolidayDao {
    @Query("select * from HolidayStatus where year = :year")
    suspend fun getYearHolidayStatus(year: Int): List<HolidayStatus>

    @Query("select * from HolidayStatus where month = :month and year = :year")
    suspend fun getMonthHolidayStatus(year: Int, month: Int): List<HolidayStatus>

    @Query("select * from HolidayStatus where date = :date limit 1")
    suspend fun getHolidayStatus(date: String): HolidayStatus

    @Insert
    fun insetHolidayStatus(vararg holidayStatues: HolidayStatus)

    @Delete
    fun deleteHolidayStatus(holidayStatus: HolidayStatus)
}

@Database(entities = [HolidayStatus::class], version = 1, exportSchema = false)
abstract class HolidayDataBase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "holiday"
    }

    abstract fun getLocalHolidayDao(): LocalHolidayDao
}

@Module
class LocalHolidayDataSourceModule {
    @Provides
    fun provideLocalHolidayDao(context: Context): LocalHolidayDao {
        return Room.databaseBuilder(
            context, HolidayDataBase::class.java,
            HolidayDataBase.DATABASE_NAME
        ).build().getLocalHolidayDao()
    }
}