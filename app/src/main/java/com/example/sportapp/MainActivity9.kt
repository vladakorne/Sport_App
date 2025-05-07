package com.example.sportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain9Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity9 : AppCompatActivity() {

    private lateinit var binding: ActivityMain9Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMain9Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("USER_ID") ?: return

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        binding.button13.setOnClickListener {
            binding.button13.setBackgroundColor(redColor)
            binding.button13.setTextColor(whiteColor)
            saveUserChoice(userId, "Короткие (15 минут)")
            val intent = Intent(this, MainActivity9_1::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button14.setOnClickListener {
            binding.button14.setBackgroundColor(redColor)
            binding.button14.setTextColor(whiteColor)
            saveUserChoice(userId, "Средние (30-40 минут)")
            val intent = Intent(this, MainActivity9_1::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button16.setOnClickListener {
            binding.button16.setBackgroundColor(redColor)
            binding.button16.setTextColor(whiteColor)
            saveUserChoice(userId, "Долгие (60-80 минут)")
            val intent = Intent(this, MainActivity9_1::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton9.setOnClickListener {
            finish()
        }

    }

    private fun saveUserChoice(userId: String, training_time: String) {
        val userChoice = hashMapOf(
            "training_time" to training_time
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
        binding.button13.setBackgroundColor(defaultColor)
        binding.button13.setTextColor(defaultColor2)
        binding.button14.setBackgroundColor(defaultColor)
        binding.button14.setTextColor(defaultColor2)
        binding.button16.setBackgroundColor(defaultColor)
        binding.button16.setTextColor(defaultColor2)
    }
}