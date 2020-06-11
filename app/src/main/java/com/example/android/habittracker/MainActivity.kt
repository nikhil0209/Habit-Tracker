package com.example.android.habittracker

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI(findViewById(R.id.white_space))
        val dateView: TextView  = findViewById(R.id.start_date)
        val elapsedTimeView:TextView = findViewById(R.id.elapsed_time)
        val habitNameView: TextView = findViewById(R.id.habit_name)
        val reasonOneView: TextView = findViewById(R.id.reason_one)
        val reasonTwoView: TextView = findViewById(R.id.reason_two)
        val reasonThreeView: TextView = findViewById(R.id.reason_three)
        val myPreferences: String = "my_preferences"
        val startDateKey: String = "start_date"
        val habitNameKey: String = "habit_name"
        val reasonOneKey: String = "reason_one"
        val reasonTwoKey: String = "reason_two"
        val reasonThreeKey: String = "reason_three"

        sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        if(sharedPreferences.contains(habitNameKey))
            habitNameView.text = sharedPreferences.getString(habitNameKey, "")
        if(sharedPreferences.contains(reasonOneKey))
            reasonOneView.text = sharedPreferences.getString(reasonOneKey, "")
        if(sharedPreferences.contains(reasonTwoKey))
            reasonTwoView.text = sharedPreferences.getString(reasonTwoKey,"")
        if(sharedPreferences.contains(reasonThreeKey))
            reasonThreeView.text = sharedPreferences.getString(reasonThreeKey, "")
        if(sharedPreferences.contains(startDateKey))
            dateView.text = sharedPreferences.getString(startDateKey,
                SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()))
        else
            dateView.text = SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())

        val cal = Calendar.getInstance()
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val editor = sharedPreferences.edit()
            editor.putString(startDateKey, sdf.format(cal.time))
            editor.commit()
            dateView.text = sdf.format(cal.time)

        }

        dateView.setOnClickListener {
            DatePickerDialog(this@MainActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val T: Timer = Timer()
        val timerTask: TimerTask = object: TimerTask() {
            override fun run() {
                runOnUiThread(Runnable() {
                    run() {
                        val date: Date = Date()
                        val startDate = sdf.parse(dateView.text.toString())
                        val timeDiffInMilis: Long
                        if(startDate != null && date != null) {
                            timeDiffInMilis = Math.abs(date.getTime() - startDate.getTime())
                            val timeDiffInDays = java.util.concurrent.TimeUnit.DAYS.convert(
                                timeDiffInMilis,
                                java.util.concurrent.TimeUnit.MILLISECONDS
                            )
                            elapsedTimeView.text = timeDiffInDays.toString() + " Days"
                        }
                        val editor = sharedPreferences.edit()
                        editor.putString(habitNameKey, habitNameView.text.toString())
                        editor.putString(reasonOneKey, reasonOneView.text.toString())
                        editor.putString(reasonTwoKey, reasonTwoView.text.toString())
                        editor.putString(reasonThreeKey, reasonThreeView.text.toString())
                        editor.commit()
                    }
                })
            }
        }
        T.scheduleAtFixedRate(timerTask, 0, 500)

    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideKeyboard(view)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }


}