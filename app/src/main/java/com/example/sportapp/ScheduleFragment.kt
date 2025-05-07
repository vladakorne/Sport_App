package com.example.sportapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ScheduleFragment : Fragment() {

    private lateinit var seekBarVolume: SeekBar
    private lateinit var textVolumeLevel: TextView
    private lateinit var buttonChangeDays: Button
    private lateinit var buttonChangeTime: Button
    private lateinit var backButton: ImageButton
    private lateinit var userId: String

    private val REQUEST_CHANGE_DAYS = 1001
    private val REQUEST_CHANGE_TIME = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID") ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        seekBarVolume = view.findViewById(R.id.seekBarVolume)
        textVolumeLevel = view.findViewById(R.id.textVolumeLevel)
        buttonChangeDays = view.findViewById(R.id.buttonChangeDays)
        buttonChangeTime = view.findViewById(R.id.buttonChangeTime)
        backButton = view.findViewById(R.id.imageButtonBack)

        val prefs = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        val savedVolume = prefs.getInt("volumeLevel", maxVolume / 2)

        seekBarVolume.max = maxVolume
        seekBarVolume.progress = savedVolume
        textVolumeLevel.text = "Громкость: ${savedVolume * 100 / maxVolume}%"

        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0)
                textVolumeLevel.text = "Громкость: ${progress * 100 / maxVolume}%"
                editor.putInt("volumeLevel", progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        buttonChangeDays.setOnClickListener {
            buttonChangeDays.setBackgroundColor(redColor)
            buttonChangeDays.setTextColor(whiteColor)

            val intent = Intent(requireContext(), MainActivity10::class.java)
            intent.putExtra("fromFragment", true)
            intent.putExtra("USER_ID", userId)
            startActivityForResult(intent, REQUEST_CHANGE_DAYS)
        }

        buttonChangeTime.setOnClickListener {
            buttonChangeTime.setBackgroundColor(redColor)
            buttonChangeTime.setTextColor(whiteColor)

            val intent = Intent(requireContext(), MainActivity11::class.java)
            intent.putExtra("fromFragment", true)
            intent.putExtra("USER_ID", userId)
            startActivityForResult(intent, REQUEST_CHANGE_TIME)
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val defaultColor = Color.parseColor("#333E3E3E")
        val defaultTextColor = Color.parseColor("#000000")
        buttonChangeDays.setBackgroundColor(defaultColor)
        buttonChangeDays.setTextColor(defaultTextColor)
        buttonChangeTime.setBackgroundColor(defaultColor)
        buttonChangeTime.setTextColor(defaultTextColor)
    }

    private fun updateFirebaseSettings() {
        val prefs = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val selectedDays =
            prefs.getString("selectedDays", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?: emptyList()

        if (userId.isNotEmpty()) {
            val updateMap = mapOf(
                "selected_days" to selectedDays.sorted()
            )
            Firebase.database.getReference("users").child(userId).updateChildren(updateMap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CHANGE_DAYS -> {
                    updateFirebaseSettings()
                }

                REQUEST_CHANGE_TIME -> {
                    val hour = data?.getIntExtra("notificationHour", -1) ?: -1
                    val minute = data?.getIntExtra("notificationMinute", -1) ?: -1
                    if (hour >= 0 && minute >= 0) {
                        Firebase.database.getReference("users").child(userId).updateChildren(
                            mapOf(
                                "notification_hour" to hour,
                                "notification_minute" to minute
                            )
                        )
                    }
                }
            }
        }
    }
}
