package com.example.alarmapplication.data

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AlarmItemViewModel : ViewModel {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    constructor() : super() {

    }

}