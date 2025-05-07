package com.example.sportapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain11Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class MainActivity11 : AppCompatActivity() {

    private lateinit var binding: ActivityMain11Binding
    private lateinit var alarmManager: AlarmManager
    private var fromFragment = false
    private lateinit var userId: String
    private var notificationsEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain11Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fromFragment = intent.getBooleanExtra("fromFragment", false)
        userId = intent.getStringExtra("USER_ID") ?: return
        notificationsEnabled = intent.getBooleanExtra("notificationsEnabled", true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.timePicker.setIs24HourView(true)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val sharedPrefs = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val savedHour = sharedPrefs.getInt("notificationHour", -1)
        val savedMinute = sharedPrefs.getInt("notificationMinute", -1)


        if (savedHour != -1 && savedMinute != -1) {
            binding.timePicker.hour = savedHour
            binding.timePicker.minute = savedMinute
            binding.button20.text =
                "Выбрано время: ${String.format("%02d:%02d", savedHour, savedMinute)}"
            binding.button19.isEnabled = true
            binding.button19.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FD3433"))
        }

        binding.button20.setOnClickListener {
            binding.timePicker.visibility = View.VISIBLE
        }

        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            binding.button20.text =
                "Выбрано время: ${String.format("%02d:%02d", hourOfDay, minute)}"
            binding.button19.isEnabled = true
            binding.button19.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FD3433"))
        }

        binding.button19.setOnClickListener {
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute

            sharedPrefs.edit()
                .putInt("notificationHour", hour)
                .putInt("notificationMinute", minute)
                .apply()

            saveNotificationTimeToFirebase(userId, hour, minute)

            val selectedDaysString = sharedPrefs.getString("selectedDays", "")
            val selectedDays =
                selectedDaysString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()

            if (notificationsEnabled) {
                selectedDays.forEach { scheduleWeeklyNotification(it, hour, minute) }
                Log.d(
                    "Уведомления",
                    "Запланированы уведомления на дни $selectedDays в $hour:$minute"
                )
            } else {
                selectedDays.forEach { cancelWeeklyNotification(it) }
                Log.d(
                    "Уведомления",
                    "Оповещения отключены. Уведомления отменены для дней $selectedDays"
                )
            }

            if (fromFragment) {
                val resultIntent = Intent().apply {
                    putExtra("notificationHour", hour)
                    putExtra("notificationMinute", minute)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                val nextIntent = Intent(this, MainActivity13::class.java)
                nextIntent.putExtra("USER_ID", userId)
                startActivity(nextIntent)
            }
        }

        binding.imageButton11.setOnClickListener {
            finish()
        }
    }

    private fun saveNotificationTimeToFirebase(userId: String, hour: Int, minute: Int) {
        if (userId.isEmpty()) return
        val usersRef = Firebase.database.getReference("users")
        val updateMap = mapOf(
            "notification_hour" to hour,
            "notification_minute" to minute
        )
        usersRef.child(userId).updateChildren(updateMap)
    }

    private fun scheduleWeeklyNotification(dayOfWeek: Int, hour: Int, minute: Int) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            dayOfWeek,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )

        Log.d("Уведомления", "Запланировано уведомление на день $dayOfWeek в $hour:$minute")
    }

    private fun cancelWeeklyNotification(dayOfWeek: Int) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            dayOfWeek,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("Уведомления", "Отменено уведомление на день $dayOfWeek")
    }
}
