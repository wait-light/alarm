package com.example.alarmapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.data.*
import com.example.alarmapplication.databinding.FragmentAlarmAddBinding
import com.example.alarmapplication.ui.component.LineRadioGroup
import com.example.alarmapplication.util.sp2px
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.log

class AlarmAddFragment : Fragment() {
    lateinit var binding: FragmentAlarmAddBinding
    private val args: AlarmAddFragmentArgs by navArgs()

    @Inject
    lateinit var alarmItemViewModel: AlarmItemViewModel
    lateinit var navController: NavController

    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory

    companion object {
        private const val TAG = "AlarmAddFragment"
        private val one2twelveStringArray =
            (1..12).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
        private val zero2FiftyNineStringArray =
            (0..59).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AlarmApplication.ALARM_COMPONENT.alarmItemComponent().create().inject(this)
//        val alarmService = context?.getSystemService(AlarmManager::class.java) as AlarmManager
//        alarmService.set(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            5000L,
//            "asdf",
//            object : AlarmManager.OnAlarmListener {
//                override fun onAlarm() {
//                    Log.d(TAG, "onAlarm: xxxxxxxxxx")
//                }
//            },
//            null
//        )
        super.onCreate(savedInstanceState)
        navController = findNavController()
        Log.d(
            TAG,
            "onCreate: xxxx${(alarmRepeatStrategyFactory as AlarmRepeatStrategyFactoryImpl).strategies}"
        )
    }

    private fun observe() {
        alarmItemViewModel.currentAlarm.observe(viewLifecycleOwner) {
            it?.apply {
                binding.apply {
                    timePeriod.value = if (localTime.hour < 12) 0 else 1
                    timeHour.value = localTime.hour % 12
                    timeMinute.value = localTime.minute
                    vibration.isChecked = it.vibration
                    remark.text = it.remark
                    repeat.text = it.repeat.toString()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args.alarm?.let { alarmItemViewModel.updateCurrentAlarm(it) }
        observe()
        binding = FragmentAlarmAddBinding.inflate(inflater, container, false)
        binding.apply {
            val pickerTextSize = context?.let { 30f.sp2px(it) } ?: 20f
            val pickerDividerHeight = 0
            val pickerDescendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            timePeriod.apply {
                displayedValues = context.resources.getStringArray(R.array.time_period)
                minValue = 0
                maxValue = displayedValues.size - 1
                descendantFocusability = pickerDescendantFocusability
                selectionDividerHeight = pickerDividerHeight
                textSize = pickerTextSize
            }
            timeHour.apply {
                displayedValues = one2twelveStringArray
                minValue = 1
                maxValue = displayedValues.size
                descendantFocusability = pickerDescendantFocusability
                selectionDividerHeight = pickerDividerHeight
                textSize = pickerTextSize
            }
            timeMinute.apply {
                displayedValues = zero2FiftyNineStringArray
                minValue = 0
                maxValue = displayedValues.size - 1
                descendantFocusability = pickerDescendantFocusability
                selectionDividerHeight = pickerDividerHeight
                textSize = pickerTextSize

            }
            check.setOnClickListener {
                GlobalScope.launch {
                    alarmItemViewModel.addAlarm(
                        Alarm(
                            0,
                            getPickerTime(),
                            AlarmRepeat.EVERYDAY,
                            "s",
                            vibration.isChecked,
                            false,
                            remark.text.toString(),
                            true
                        )
                    )
                    Log.d(TAG, "onCreateView: ${alarmItemViewModel.getAllAlarm()}")
                }

            }
            repeat.setOnClickListener {
                BottomSheetDialog(requireContext()).apply {
                    setContentView(LineRadioGroup(
                        requireContext(),
                        listOf(
                            "只响一次" to AlarmRepeat.ONE_TIME,
                            "周一到周五" to AlarmRepeat.MONDAY2FRIDAY,
                            "法定工作日" to AlarmRepeat.WORKING_DAY,
                            "每天" to AlarmRepeat.EVERYDAY,
                            "法定节假日" to AlarmRepeat.STATUTORY_HOLIDAYS,
                            "大小周上班时间" to AlarmRepeat.REWARD
                        )
                    ).apply {
                        setOnCheckedChangeListener { group, id ->
                            dismiss()
                        }
                    })
                    show()
                }
            }
            remark.setOnClickListener {
                BottomSheetDialog(requireContext()).apply {
                    setContentView(
                        layoutInflater.inflate(
                            R.layout.remark_add,
                            it as ViewGroup,
                            false
                        )
                    )
                    show()
                }
            }
            close.setOnClickListener {
                navController.popBackStack()
            }
        }
        return binding.root
    }

    fun getPickerTime(): LocalTime {
        var pickerTime: LocalTime
        binding.apply {
            pickerTime = LocalTime.of(timePeriod.value * 12 + timeHour.value % 12, timeMinute.value)
        }
        return pickerTime
    }

}