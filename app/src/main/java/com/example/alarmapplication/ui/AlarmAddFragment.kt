package com.example.alarmapplication.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.SystemClock
import android.provider.AlarmClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.R
import com.example.alarmapplication.data.*
import com.example.alarmapplication.databinding.FragmentAlarmAddBinding
import com.example.alarmapplication.databinding.RemarkAddBinding
import com.example.alarmapplication.databinding.RingtoneItemBinding
import com.example.alarmapplication.databinding.SingleRecycleviewBinding
import com.example.alarmapplication.domain.AlarmDomain
import com.example.alarmapplication.ui.component.LineRadioGroup
import com.example.alarmapplication.util.shotToast
import com.example.alarmapplication.util.sp2px
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

class AlarmAddFragment : Fragment() {
    lateinit var binding: FragmentAlarmAddBinding
    private val args: AlarmAddFragmentArgs by navArgs()

    @Inject
    lateinit var alarmItemViewModel: AlarmItemViewModel
    lateinit var navController: NavController

//    @Inject
//    lateinit var alarmDomain: AlarmDomain
//    @Inject
//    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory

    companion object {
        private const val TAG = "AlarmAddFragment"
        private val one2twelveStringArray =
            (1..12).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
        private val zero2FiftyNineStringArray =
            (0..59).map { if (it > 9) it.toString() else "0$it" }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AlarmApplication.ALARM_COMPONENT.alarmItemComponent().create().inject(this)
//        val registerForActivityResult =
//            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//                Log.e(TAG, "onCreate: xxxxxxxxxxxxxxxxxxxxxxxxxxx$it")
//                if (it) {
//
//                } else {
//
//                }
//            }
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.SCHEDULE_EXACT_ALARM
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            val uri = Uri.parse("package:" + context?.getPackageName());
//            val i = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, uri);
//            startActivity(i);
////            registerForActivityResult.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
//        } else {
//            Log.e(TAG, "onCreate: ")
//        }


        super.onCreate(savedInstanceState)
        navController = findNavController()
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
                    repeat.text = AlarmRepeat.NAME_TYPE_PAIRS[it.repeat].first
                    ring.text = if(it.ring.isNullOrEmpty()) "" else Uri.parse(it.ring).getQueryParameter("title")
                }
            }
        }
        alarmItemViewModel.isAddAlarm.observe(viewLifecycleOwner) {
            if (it!!) {
                binding.check.setText("添加")
            } else {
                binding.check.setText("更新")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args.alarm?.let {
            alarmItemViewModel.apply {
                setNotAddAlarm()
                updateCurrentAlarm(it)
            }
        }
        binding = FragmentAlarmAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bind() {
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
                if (alarmItemViewModel.isAddAlarm.value!!) {
                    addAlarm()
                } else {
                    updateAlarm()
                }
                requireActivity().runOnUiThread {
                    navController.popBackStack()
                }
            }
            repeatWrapper.setOnClickListener {
                showRepeatDialog()
            }
            remarkWrapper.setOnClickListener {
                showRemarkDialog(it as ViewGroup)
            }
            vibrationWrapper.setOnClickListener {
                vibration.isChecked = !vibration.isChecked
            }
            ringWrapper.setOnClickListener {
                showRingtoneDialog()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.text)
    }

    class RingtoneRecycleView(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
        var data: List<Ringtone>? = null
        var onClickListener: OnClickListener? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.ringtone_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = data?.get(position)?.title
            holder.text.setOnClickListener {
                onClickListener?.onClick(it, position)
            }
        }

        override fun getItemCount(): Int = data?.size ?: 0

        interface OnClickListener {
            fun onClick(view: View, position: Int)
        }
    }

    private fun showRingtoneDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(SingleRecycleviewBinding.inflate(layoutInflater).let {
            it.content.apply {
                val ringtoneRecycleView = RingtoneRecycleView(requireContext()).apply {
                    var ringtone: android.media.Ringtone? = null
                    onClickListener = object : RingtoneRecycleView.OnClickListener {
                        override fun onClick(view: View, position: Int) {
                            ringtone?.stop()
                            ringtone =
                                RingtoneManager.getRingtone(context, data?.get(position)?.url)
                                    .apply { play() }
                            dialog.setOnDismissListener {
                                alarmItemViewModel.currentAlarm.value!!.ring =
                                    data?.get(position)?.url.toString()
                                binding.ring.setText(data?.get(position)?.title)
                                ringtone?.stop()
                            }
                        }
                    }
                }
                layoutManager = LinearLayoutManager(context)
                adapter = ringtoneRecycleView
                GlobalScope.launch {
                    ringtoneRecycleView.data = getRingtoneList(context)
                    post {
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
            it.root
        })
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        bind()
        binding.tools.setupWithNavController(
            navController,
            AppBarConfiguration(navController.graph)
        )
    }

    private fun fixedCurrentAlarm(): Alarm {
        val currentAlarm = alarmItemViewModel.currentAlarm.value!!
        return currentAlarm.apply {
            localTime = getPickerTime()
            vibration = binding.vibration.isChecked
            autoDelete = false
            remark = binding.remark.text.toString()
        }
    }

    private fun addAlarm() {
//        val alarmService = context?.getSystemService(AlarmManager::class.java) as AlarmManager
//        alarmService.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            Calendar.getInstance().apply {
//                timeInMillis += 3000
////                timeInMillis += 1000 * 60 * 5
//            }.timeInMillis,
//            PendingIntent.getBroadcast(
//                context,
//                12138,
//                Intent(
//                    requireContext(),
//                    AlarmReceiver::class.java
//                ).apply {
//                    setAction(AlarmReceiver.ACTION_START)
//                    putExtra("aaa",123)
////                    putExtra("aaa", fixedCurrentAlarm())
//                },
//                PendingIntent.FLAG_ONE_SHOT
//            )
//        )
        GlobalScope.launch {
            alarmItemViewModel.addAlarm(fixedCurrentAlarm())
//            val fixedCurrentAlarm = fixedCurrentAlarm()
//
//            Log.d(TAG, "addAlarm: ${ alarmItemViewModel.addAlarm(fixedCurrentAlarm)}")
        }
    }

    private fun updateAlarm() {
        GlobalScope.launch {
            alarmItemViewModel.updateAlarm(fixedCurrentAlarm())
        }
    }

    private fun showRepeatDialog() {
        BottomSheetDialog(requireContext()).apply {
            setContentView(LineRadioGroup(requireContext(), AlarmRepeat.NAME_TYPE_PAIRS).apply {
                setOnCheckedChangeListener { group, id ->
                    binding.repeat.text = AlarmRepeat.NAME_TYPE_PAIRS[id].first
                    alarmItemViewModel.currentAlarm.value!!.repeat = id
                    dismiss()
                }
            })
            show()
        }
    }

    private fun showRemarkDialog(parent: ViewGroup) {
        val remarkAddBinding = RemarkAddBinding.inflate(layoutInflater, parent, false)
        val remarkDialog = BottomSheetDialog(requireContext())
        remarkAddBinding.apply {
            confirm.setOnClickListener {
                binding.remark.setText(remark.text)
                remarkDialog.dismiss()
            }
            cancel.setOnClickListener {
                remarkDialog.dismiss()
            }
        }
        remarkDialog.apply {
            setContentView(remarkAddBinding.root)
            show()
        }
    }

    private fun getPickerTime(): LocalTime {
        var pickerTime: LocalTime
        binding.apply {
            pickerTime = LocalTime.of(timePeriod.value * 12 + timeHour.value % 12, timeMinute.value)
        }
        return pickerTime
    }

}