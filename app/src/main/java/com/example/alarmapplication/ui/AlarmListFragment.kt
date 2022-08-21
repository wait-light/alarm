package com.example.alarmapplication.ui

import android.app.AlarmManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapplication.R
import com.example.alarmapplication.databinding.FragmentAlarmListBinding

class AlarmListFragment : Fragment() {
    lateinit var binding: FragmentAlarmListBinding
    val data =
        mutableListOf<String>("123", "123", "34", "sdf", "", "", "", "", "", "", "", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val alarmManager =
            context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        binding.apply {
            root.layoutManager = LinearLayoutManager(context)
            root.adapter = Adapter()
        }
        return binding.root
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun getItemCount(): Int = data.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}