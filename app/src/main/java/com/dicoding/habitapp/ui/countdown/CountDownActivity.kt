package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null){
            findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

            val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
            val countDownTime = findViewById<TextView>(R.id.tv_count_down)
            viewModel.setInitialTime(habit.minutesFocus)
            viewModel.currentTimeString.observe(this){
                countDownTime.text = it
            }

            viewModel.eventCountDownFinish.observe(this){
                updateButtonState(!it)
            }


            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
            val channelName = getString(R.string.notify_channel_name)
            val workManager = WorkManager.getInstance(this)

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val inputData = Data.Builder()
                .putInt(HABIT_ID,habit.id)
                .putString(HABIT_TITLE,habit.title)
                .putString(NOTIFICATION_CHANNEL_ID,channelName)
                .build()

            val notificationRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInputData(inputData)
                .setInitialDelay(habit.minutesFocus * 60 * 1000, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(NOTIF_UNIQUE_WORK)
                .build()

            findViewById<Button>(R.id.btn_start).setOnClickListener {
                viewModel.startTimer()
                workManager.enqueueUniqueWork(
                    NOTIF_UNIQUE_WORK,
                    ExistingWorkPolicy.REPLACE,
                    notificationRequest
                )
            }

            findViewById<Button>(R.id.btn_stop).setOnClickListener {
                viewModel.resetTimer()
                workManager.cancelAllWork()
            }
        }

    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}