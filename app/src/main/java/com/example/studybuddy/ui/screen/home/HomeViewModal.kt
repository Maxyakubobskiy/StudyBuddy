package com.example.studybuddy.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studybuddy.MyApp
import com.example.studybuddy.domain.model.ScheduleEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HomeViewModal : ViewModel() {

    private val homeworkRepository = MyApp.instance.homeworkRepository
    private val scheduleEntryRepository = MyApp.instance.scheduleEntryRepository

    val top3Homework = homeworkRepository.getTop3Homework()


    fun getTodaySchedule():Flow<List<ScheduleEntry>> {
        val today = LocalDate.now().dayOfWeek.name
        Log.d("YWEFGUYGEDUYASD", today)
        return scheduleEntryRepository.getTodaySchedule(today)
    }
}