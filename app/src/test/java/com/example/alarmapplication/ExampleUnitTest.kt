package com.example.alarmapplication

import com.example.alarmapplication.data.holiday.HolidayStatus
import com.example.alarmapplication.data.toEpochMilli
import org.junit.Test

import org.junit.Assert.*
import java.time.*
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun localDateTimeGetLong() {
        val now = LocalDateTime.now()
        println(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    @Test
    fun common() {
        println("------------ ${LocalDateTime.now().dayOfWeek.value}")
    }

    @Test
    fun isRewardWeekTest() {
        println("-------------------")
        println(isRewardWeek((LocalDateTime.of(2022, 9, 3, 0, 0))))
        println(isRewardWeek(LocalDateTime.now()))
    }

    fun isRewardWeek(date: LocalDateTime): Boolean {
        return (((date.dayOfYear + 5) / 7) % 2) == 1
    }


}