package com.example.sportapp

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class NPSActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var valueText: TextView
    private lateinit var submitButton: Button

    private var selectedScore = 5 // значение по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_npsactivity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        seekBar = findViewById(R.id.npsSeekBar)
        valueText = findViewById(R.id.npsValueText)
        submitButton = findViewById(R.id.npsSubmitButton)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedScore = progress
                valueText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        submitButton.setOnClickListener {
            saveScoreToFirebase(selectedScore)
        }
    }

    private fun saveScoreToFirebase(score: Int) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("nps_feedback")

        val feedbackId = ref.push().key

        if (feedbackId != null) {
            ref.child(feedbackId).setValue(score)
                .addOnSuccessListener {
                    Toast.makeText(this, "Спасибо за вашу оценку: $score", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Ошибка отправки. Попробуйте позже.", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}
