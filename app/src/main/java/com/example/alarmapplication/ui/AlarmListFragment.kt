package com.example.alarmapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.data.*
import com.example.alarmapplication.databinding.AlarmItemBinding
import com.example.alarmapplication.databinding.FragmentAlarmListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmListFragment : Fragment() {
    lateinit var binding: FragmentAlarmListBinding
    lateinit var navController: NavController

    @Inject
    lateinit var alarmListViewModel: AlarmListViewModel

    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory
    lateinit var adapter: Adapter

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadData()
        }
    }

    companion object {
        const val DATA_UPDATE_KEY = "top.waitlight.alarm.data.update"
    }

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        observe()
    }

    private fun bind() {
        binding.apply {
            adapter = Adapter()
            alarmList.layoutManager = LinearLayoutManager(context)
            alarmList.adapter = adapter
            action.setOnClickListener {
                if (alarmListViewModel.multipleCheck.value!!) {
                    GlobalScope.launch {
                        alarmListViewModel.deletePickerAlarm()
                    }
                } else {
                    navController.navigate(AlarmListFragmentDirections.actionAlarmListFragmentToAlarmAddFragment())
                }
            }
            tools.setupWithNavController(
                navController,
                AppBarConfiguration(navController.graph)
            )
        }

    }


    private fun observe() {
        alarmListViewModel.alarms.observe(viewLifecycleOwner) {
            it?.apply {
                adapter.notifyDataSetChanged()
            }
        }
        alarmListViewModel.multipleCheck.observe(viewLifecycleOwner) {
            if (it!!) {
                binding.action.setImageResource(R.drawable.delete)
                binding.tools.setNavigationIcon(R.drawable.close)
                binding.tools.setNavigationOnClickListener { alarmListViewModel.toggleMultipleCheck() }
            } else {
                binding.action.setImageResource(R.drawable.add)
                binding.tools.navigationIcon = null
            }
            adapter.notifyDataSetChanged()
        }
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = AlarmItemBinding.inflate(layoutInflater, parent, false)
            binding.root.setOnLongClickListener {
                alarmListViewModel.toggleMultipleCheck()
                true
            }
            return ViewHolder(binding)
        }

        private val timePeriodString = context?.resources?.getStringArray(R.array.time_period)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val alarm = alarmListViewModel.alarms.value!!.get(position)
            val checkList = alarmListViewModel.checkList
            if (alarm.enable) {
                holder.binding.nextTime.visibility = View.VISIBLE
                GlobalScope.launch {
                    val string = "${
                        alarmRepeatStrategyFactory.getAlarmStrategy(alarm.repeat)?.nextTime(alarm)
                            ?.userFriendlyTimeString()
                    }后响铃" ?: ""
                    Log.d("TAG", "${Thread.currentThread().name}: ")
                    holder.binding.nextTime.post {
                        Log.d("TAG", "${Thread.currentThread().name}: ")
                        holder.binding.nextTime.text = string
                    }
                }
            } else {
                holder.binding.enable.isChecked = false
                holder.binding.nextTime.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if (!alarmListViewModel.multipleCheck.value!!) {
                    navController.navigate(
                        AlarmListFragmentDirections.actionAlarmListFragmentToAlarmAddFragment(
                            alarm
                        )
                    )
                } else {
                    checkList[position] = !checkList[position]
                    holder.binding.multiplyCheck.isChecked = checkList[position]
                }
                Log.d("TAGxxxx", "onBindViewHolder: " + checkList)
                Log.d("TAGxxxx", "onBindViewHolder: $position ${holder.binding.multiplyCheck.isChecked}")

            }
            holder.binding.apply {
                timePeriod.text =
                    if (alarm.localTime.hour < 12) timePeriodString?.get(0) else timePeriodString?.get(
                        1
                    )
                time.text = "${
                    digitsFill(
                        alarm.localTime.hour % 12,
                        2
                    )
                }:${digitsFill(alarm.localTime.minute, 2)}"
                cycle.text = AlarmRepeat.getNameTypePairs(requireContext()).get(alarm.repeat).first
                if (alarmListViewModel.multipleCheck.value!!) {
                    enable.visibility = View.GONE
                    multiplyCheck.visibility = View.VISIBLE
                    multiplyCheck.isChecked = checkList[position]
                    multiplyCheck.setOnClickListener {
                        checkList[position] = !checkList[position]
                    }
                    Log.d("TAGxx", "onBindViewHolder: " + checkList)
                    Log.d("TAGxx", "onBindViewHolder: $position ${holder.binding.multiplyCheck.isChecked}")

                } else {
//                    binding.tools.navigationIcon = null
                    multiplyCheck.visibility = View.GONE
                    enable.visibility = View.VISIBLE
                    enable.isChecked = alarm.enable
                    enable.setOnClickListener {
                        alarm.enable = enable.isChecked
                        GlobalScope.launch {
                            alarmListViewModel.updateAlarm(alarm)
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = alarmListViewModel.alarms.value?.size ?: 0
    }

    override fun onResume() {
        super.onResume()
        loadData()
        context?.registerReceiver(dataUpdateReceiver, IntentFilter(DATA_UPDATE_KEY))
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(dataUpdateReceiver)
    }

    private fun loadData() {
        GlobalScope.launch {
            alarmListViewModel.fetchAllAlarm()
        }
    }

    class ViewHolder(val binding: AlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}