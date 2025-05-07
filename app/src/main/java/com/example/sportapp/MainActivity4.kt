package com.example.sportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain4Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity4 : AppCompatActivity() {

    private lateinit var binding: ActivityMain4Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("USER_ID") ?: return

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        binding.button5.setOnClickListener {
            binding.button5.setBackgroundColor(redColor)
            binding.button5.setTextColor(whiteColor)
            saveUserChoice(userId, "Тренировки на пресс")
            val intent = Intent(this, MainActivity5::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button6.setOnClickListener {
            binding.button6.setBackgroundColor(redColor)
            binding.button6.setTextColor(whiteColor)
            saveUserChoice(userId, "Тренировки на растяжку")
            val intent = Intent(this, MainActivity6::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton4.setOnClickListener {
            finish()
        }
    }


    private fun saveUserChoice(userId: String, workoutType: String) {
        val userChoice = hashMapOf(
            "workoutType" to workoutType
        )

        usersRef.child(userId).updateChildren(userChoice as Map<String, Any>)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    override fun onResume() {
        super.onResume()

        val defaultColor = Color.parseColor("#333E3E3E")
        val defaultColor2 = Color.parseColor("#000000")
        binding.button5.setBackgroundColor(defaultColor)
        binding.button5.setTextColor(defaultColor2)
        binding.button6.setBackgroundColor(defaultColor)
        binding.button6.setTextColor(defaultColor2)
    }
}