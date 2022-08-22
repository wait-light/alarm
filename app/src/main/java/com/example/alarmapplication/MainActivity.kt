package com.example.alarmapplication

import android.app.AlarmManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.example.alarmapplication.ui.AlarmAddFragment
import com.example.alarmapplication.ui.AlarmListFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        supportFragmentManager.beginTransaction().add(R.id.content, AlarmListFragment()).commit()
    }
}