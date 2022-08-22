package com.example.alarmapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.data.Alarm
import com.example.alarmapplication.data.AlarmListViewModel
import com.example.alarmapplication.data.AlarmRepeatStrategyFactory
import com.example.alarmapplication.data.AlarmRepeatStrategyFactoryImpl
import com.example.alarmapplication.databinding.AlarmItemBinding
import com.example.alarmapplication.databinding.FragmentAlarmListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

class AlarmListFragment : Fragment() {
    lateinit var binding: FragmentAlarmListBinding
    lateinit var navController: NavController

    @Inject
    lateinit var alarmListViewModel: AlarmListViewModel

    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory
    lateinit var adapter: Adapter
    val data =
        mutableListOf<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AlarmApplication.ALARM_COMPONENT.alarmItemComponent().create().inject(this)
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val alarmManager =
//            context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        binding.apply {
            adapter = Adapter()
            alarmList.layoutManager = LinearLayoutManager(context)
            alarmList.adapter = adapter
            addAlarm.setOnClickListener {
                navController.navigate(AlarmListFragmentDirections.actionAlarmListFragmentToAlarmAddFragment())
            }
        }
        return binding.root
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = AlarmItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val alarm = data[position]
            holder.itemView.setOnClickListener {
                navController.navigate(
                    AlarmListFragmentDirections.actionAlarmListFragmentToAlarmAddFragment(
                        alarm
                    )
                )
            }
            holder.binding.apply {
                timePeriod.setText(if (alarm.localTime.hour < 12) "上午" else "下午")
                time.setText(alarm.localTime.toString())
                enable.isChecked = alarm.enable
                enable.setOnClickListener {
                    alarm.enable = enable.isChecked
                    GlobalScope.launch {
                        alarmListViewModel.updateAlarm(alarm)
                    }
                }
            }
        }

        override fun getItemCount(): Int = data.size
    }

    override fun onResume() {
        super.onResume()
        loadData()

    }

    private fun loadData() {
        GlobalScope.launch {
            alarmListViewModel.getAllAlarm().collect() {
                data.clear()
                data.addAll(it)
                requireActivity().runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    class ViewHolder(val binding: AlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}