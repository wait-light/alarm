package com.example.alarmapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.data.AlarmItemViewModel
import com.example.alarmapplication.databinding.FragmentAlarmAddBinding
import com.example.alarmapplication.ui.component.LineRadioGroup
import com.example.alarmapplication.util.shotToast
import com.example.alarmapplication.util.sp2px
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlarmAddFragment : Fragment() {
    lateinit var binding: FragmentAlarmAddBinding

    @Inject
    lateinit var alarmItemViewModel: AlarmItemViewModel

    companion object {
        private const val TAG = "AlarmAddFragment"
        private val one2twelveStringArray =
            (1..12).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
        private val zero2FiftyNineStringArray =
            (0..59).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AlarmApplication.alarmComponent.alarmItemComponent().create().inject(this)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            addAlarm.setOnClickListener {
                getPickerTime().toString().shotToast(requireContext())
            }
            repeat.setOnClickListener {
                BottomSheetDialog(requireContext()).apply {
                    setContentView(LineRadioGroup(
                        requireContext(),
                        listOf("sdf" to "asdf", "asdf" to "asdf")
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