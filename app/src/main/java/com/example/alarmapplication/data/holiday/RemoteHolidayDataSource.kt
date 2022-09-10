package com.example.alarmapplication.data.holiday

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton


interface RemoteHolidayDao {
    @GET("holiday")
    suspend fun getHolidayStatusByDetailDate(@Query("date") date: String): List<HolidayStatus>

    @GET("holiday")
    suspend fun getYearHolidayStatus(@Query("date") year: Int): List<HolidayStatus>
}

class RemoteHolidayDataSource @Inject constructor() : HolidayDataSource {
    @Inject
    lateinit var dao: RemoteHolidayDao
    override suspend fun getHolidayStatus(date: String): HolidayStatus =
        dao.getHolidayStatusByDetailDate(date)[0]

    override suspend fun getYearHolidayStatus(year: Int): List<HolidayStatus> =
        dao.getYearHolidayStatus(year)

}


@Module
class RemoteHolidayModule {
    companion object {
        const val API_URL = "http://api.haoshenqi.top/"
    }

    @Provides
    @Singleton
    fun provideRemoteHolidayDataSource(): RemoteHolidayDao {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteHolidayDao::class.java)
    }
}