package com.danish.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.danish.smartalarm.Fragment.DateDialogFragment
import com.danish.smartalarm.Fragment.TimeDialogFragment
import com.danish.smartalarm.data.Alarm
import com.danish.smartalarm.data.local.AlarmDB
import com.danish.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.danish.smartalarm.helper.timeFormatter
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DateDialogFragment.DialogDateSetListener,
    TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private val db by lazy { AlarmDB(this) }
    private var alarmService : AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }


            btnCancel.setOnClickListener {
                finish()
            }


            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAdd.setOnClickListener {
                val date = tvOneDate.text.toString()
                val time = tvOneTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date == "Date" && time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm),
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    alarmService?.setOneTimeAlarm(
                        applicationContext,
                        AlarmReceiver.TYPE_ONE_TIME,
                        date,
                        time,
                        message)

                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                message,
                                AlarmReceiver.TYPE_ONE_TIME
                            )
                        )
                        Log.i("AddAlarm", "Alarm set on: $date $time with message $message")
                        finish()
                    }
                }


            }
        }

    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        // untuk menyetal/menentukan calendar menjadi waktu yang ditentukan
        calendar.set(year, month, dayOfMonth)
        val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOneDate.text = dateFormatted.format(calendar.time)
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {

        binding.tvOneTime.text = timeFormatter(hour, minute)
    }
}
