package com.dicoding.courseschedule.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.databinding.ActivityAddBinding
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    enum class ButtonNum{
        StartTime,EndTime
    }
    private var lastBtn = ButtonNum.StartTime
    private lateinit var view: View
    private lateinit var viewModel: AddCourseViewModel
     lateinit var binding: ActivityAddBinding
     private var startTime = ""
    private var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val factory = AddViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this,factory)[AddCourseViewModel::class.java]
        viewModel.saved.observe(this){
            if (it.getContentIfNotHandled() == true)
                onBackPressedDispatcher.onBackPressed()
            else {
                val message = getString(R.string.input_empty_message)
                Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                val courseName = findViewById<TextInputEditText?>(R.id.ed_course_name).text.toString().trim()
                val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition
                val startTime = findViewById<TextView>(R.id.tv_add_time).text.toString().trim()
                val endTime = findViewById<TextView>(R.id.tv_addend_time).text.toString().trim()
                val lecturer = findViewById<TextInputEditText>(R.id.ed_lecturer).text.toString().trim()
                val note = findViewById<TextInputEditText>(R.id.ed_note).text.toString().trim()

                when {
                    courseName.isEmpty() -> false
                    startTime.isEmpty() -> false
                    endTime.isEmpty() -> false
                    day == -1 -> false
                    lecturer.isEmpty() -> false
                    note.isEmpty() -> false

                    else -> {
                        viewModel.insertCourse(courseName,day, startTime, endTime, lecturer, note)
                        finish()
                        true
                    }
                }
            } else -> super.onOptionsItemSelected(item)
        }
    }

    fun showStartTimePicker(view: View){
        TimePickerFragment().show(
            supportFragmentManager,"startTime"
        )
        this.view = view
    }

    fun showEndTimePicker(view: View){
        TimePickerFragment().show(
            supportFragmentManager, "endTime"
        )
        this.view = view
    }


    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {

        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (view.id) {
            R.id.ib_start_time -> {
                findViewById<TextView>(R.id.tv_add_time).text = timeFormat.format(calender.time)
            }
            R.id.ib_end_time -> {
                findViewById<TextView>(R.id.tv_addend_time).text = timeFormat.format(calender.time)
            }
        }
    }
}