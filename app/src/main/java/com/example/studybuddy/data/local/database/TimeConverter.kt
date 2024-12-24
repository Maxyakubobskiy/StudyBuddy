package com.example.studybuddy.data.local.database

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeConverter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?) = localTime?.format(formatter)


    @TypeConverter
    fun toLocalTime(value: String?) = value?.let { LocalTime.parse(it, formatter) }

}