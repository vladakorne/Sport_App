package com.example.sportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain91Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity9_1 : AppCompatActivity() {

    private lateinit var binding: ActivityMain91Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMain91Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("USER_ID") ?: return

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        binding.button21.setOnClickListener {
            binding.button21.setBackgroundColor(redColor)
            binding.button21.setTextColor(whiteColor)
            saveUserChoice(userId, "Неделя")
            val intent = Intent(this, MainActivity10::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button22.setOnClickListener {
            binding.button22.setBackgroundColor(redColor)
            binding.button22.setTextColor(whiteColor)
            saveUserChoice(userId, "Месяц")
            val intent = Intent(this, MainActivity10::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button23.setOnClickListener {
            binding.button23.setBackgroundColor(redColor)
            binding.button23.setTextColor(whiteColor)
            saveUserChoice(userId, "3 месяца")
            val intent = Intent(this, MainActivity10::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton91.setOnClickListener {
            finish()
        }

    }

    private fun saveUserChoice(userId: String, plan_for: String) {
        val userChoice = hashMapOf(
            "plan_for" to plan_for
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
        binding.button21.setBackgroundColor(defaultColor)
        binding.button21.setTextColor(defaultColor2)
        binding.button22.setBackgroundColor(defaultColor)
        binding.button22.setTextColor(defaultColor2)
        binding.button23.setBackgroundColor(defaultColor)
        binding.button23.setTextColor(defaultColor2)
    }
}