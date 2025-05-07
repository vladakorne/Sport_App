package com.example.sportapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain10Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity10 : AppCompatActivity() {

    private lateinit var binding: ActivityMain10Binding
    private val selectedDays = mutableSetOf<Int>()
    private var fromFragment = false
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain10Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fromFragment = intent.getBooleanExtra("fromFragment", false)
        userId = intent.getStringExtra("USER_ID") ?: return

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val checkBoxes = listOf(
            binding.checkBox to 2,
            binding.checkBox2 to 3,
            binding.checkBox3 to 4,
            binding.checkBox4 to 5,
            binding.checkBox5 to 6,
            binding.checkBox6 to 7,
            binding.checkBox7 to 1
        )

        val sharedPrefs = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val savedDays = sharedPrefs.getString("selectedDays", "")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        selectedDays.addAll(savedDays)

        checkBoxes.forEach { (checkBox, day) ->
            checkBox.isChecked = day in selectedDays
        }

        binding.switch1.isChecked = sharedPrefs.getBoolean("notificationsEnabled", true)

        val redColor = ColorStateList.valueOf(Color.parseColor("#FD3433"))
        val grayColor = ColorStateList.valueOf(Color.parseColor("#333E3E3E"))

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val day = checkBoxes.find { it.first == buttonView }?.second
            if (day != null) {
                if (isChecked) selectedDays.add(day)
                else selectedDays.remove(day)
            }
            binding.button17.isEnabled = selectedDays.isNotEmpty()
            binding.button17.backgroundTintList =
                if (selectedDays.isNotEmpty()) redColor else grayColor
        }

        checkBoxes.forEach { (checkBox, _) ->
            checkBox.setOnCheckedChangeListener(checkBoxListener)
        }

        binding.button17.setOnClickListener {
            val notificationsEnabled = binding.switch1.isChecked

            sharedPrefs.edit().apply {
                putBoolean("notificationsEnabled", notificationsEnabled)
                putString("selectedDays", selectedDays.joinToString(","))
                apply()
            }

            saveSelectedDaysToFirebase(userId, selectedDays, notificationsEnabled)

            Log.d("Выбранные дни", "Сохранены дни: ${selectedDays.sorted()}")

            if (!notificationsEnabled) {
                cancelWeeklyNotifications(this, selectedDays)
            }

            if (fromFragment) {
                setResult(RESULT_OK)
                finish()
            } else {
                val nextIntent = if (notificationsEnabled) {
                    Intent(this, MainActivity11::class.java)
                } else {
                    Intent(this, MainActivity12::class.java)
                }
                nextIntent.putExtra("fromFragment", fromFragment)
                nextIntent.putExtra("USER_ID", userId)
                nextIntent.putExtra("notificationsEnabled", notificationsEnabled)
                startActivity(nextIntent)
            }
        }

        binding.imageButton10.setOnClickListener {
            finish()
        }

        binding.button17.isEnabled = selectedDays.isNotEmpty()
        binding.button17.backgroundTintList = if (selectedDays.isNotEmpty()) redColor else grayColor
    }

    private fun saveSelectedDaysToFirebase(
        userId: String,
        days: Set<Int>,
        notificationsEnabled: Boolean
    ) {
        if (userId.isEmpty()) return
        val database = Firebase.database
        val usersRef = database.getReference("users")

        val updateMap = mapOf(
            "selected_days" to days.sorted(),
            "notifications_enabled" to notificationsEnabled
        )

        usersRef.child(userId).updateChildren(updateMap)
    }

    private fun cancelWeeklyNotifications(context: Context, days: Set<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (day in days) {
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                day,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("Уведомления", "Отменено уведомление на день $day")
        }
    }
}
