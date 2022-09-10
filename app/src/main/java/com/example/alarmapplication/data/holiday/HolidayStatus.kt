package com.example.alarmapplication.data.holiday

import androidx.annotation.IntDef
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.time.LocalDateTime

@Entity(tableName = "HolidayStatus")
data class HolidayStatus(
    @PrimaryKey val date: String, @HolidayStatusDef val status: Int,
    val year: Int, val month: Int
) :
    Serializable {
    companion object {
        //工作日
        const val STATUS_WORK_DAY = 0

        //周末
        const val STATUS_WEEKEND = 1

        //调休
        const val STATUS_SABBATICAL = 2

        //法定节假日
        const val STATUS_HOLIDAY = 3
    }

    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(year, month, date.split("-")[2].toInt(), 0, 0)
    }
}

@IntDef(
    HolidayStatus.STATUS_WORK_DAY,
    HolidayStatus.STATUS_WEEKEND,
    HolidayStatus.STATUS_SABBATICAL,
    HolidayStatus.STATUS_HOLIDAY
)
@Retention(RetentionPolicy.SOURCE)
annotation class HolidayStatusDef