package com.example.sportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain6Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity6 : AppCompatActivity() {

    private lateinit var binding: ActivityMain6Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMain6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("USER_ID") ?: return

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        binding.button12.setOnClickListener {
            binding.button12.setBackgroundColor(redColor)
            binding.button12.setTextColor(whiteColor)
            saveUserChoice(userId, "Начинающий")
            val intent = Intent(this, MainActivity7::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button10.setOnClickListener {
            binding.button10.setBackgroundColor(redColor)
            binding.button10.setTextColor(whiteColor)
            saveUserChoice(userId, "Средний")
            val intent = Intent(this, MainActivity7::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.button11.setOnClickListener {
            binding.button11.setBackgroundColor(redColor)
            binding.button11.setTextColor(whiteColor)
            saveUserChoice(userId, "Продвинутый")
            val intent = Intent(this, MainActivity7::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        binding.imageButton6.setOnClickListener {
            finish()
        }
    }

    private fun saveUserChoice(userId: String, experience: String) {
        val userChoice = hashMapOf(
            "experience" to experience
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
        binding.button10.setBackgroundColor(defaultColor)
        binding.button10.setTextColor(defaultColor2)
        binding.button11.setBackgroundColor(defaultColor)
        binding.button11.setTextColor(defaultColor2)
        binding.button12.setBackgroundColor(defaultColor)
        binding.button12.setTextColor(defaultColor2)
    }
}