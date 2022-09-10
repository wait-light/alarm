package com.example.alarmapplication.data.holiday

import dagger.Module

interface HolidayDataSource {
    suspend fun getHolidayStatus(date: String): HolidayStatus
    suspend fun getYearHolidayStatus(year: Int): List<HolidayStatus>
}

@Module(includes = [LocalHolidayDataSourceModule::class, RemoteHolidayModule::class])
interface HolidayDataSourceModule