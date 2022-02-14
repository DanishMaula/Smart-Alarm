package com.danish.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.danish.smartalarm.Fragment.DateDialogFragment
import com.danish.smartalarm.Fragment.TimeDialogFragment
import com.danish.smartalarm.data.Alarm
import com.danish.smartalarm.data.local.AlarmDB
import com.danish.smartalarm.databinding.ActivityRepeatingAlarmBinding
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding : ActivityRepeatingAlarmBinding? = null
    private val binding get () = _binding as ActivityRepeatingAlarmBinding

    private val db by lazy { AlarmDB(this) }
    private var alarmService : AlarmReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView(){
        binding.apply {
            btn_set_time_repeating.setOnClickListener{
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnAdd.setOnClickListener {
                val time = tvRepeating.text.toString()
                val message = edtNoteRepeating.text.toString()

                if (time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm),
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    alarmService?.setRepeatingAlarm(
                        applicationContext,
                        AlarmReceiver.TYPE_REPEATING,
                        time,
                        message
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                message,
                                AlarmReceiver.TYPE_REPEATING
                            )
                        )
                        Log.i("AddAlarm", "Alarm set on:  $time with message $message")
                        finish()
                    }
                }


            }


            btnCancel.setOnClickListener {
                finish()
            }

            btnSetTimeRepeating.setOnClickListener{
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")

            }
        }
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()

        calendar.set(0, 0, 0, hour, minute )
        val dateformatted = SimpleDateFormat("hh:mm", Locale.getDefault())

        binding.tvRepeating.text = dateformatted.format(calendar.time)
    }

}