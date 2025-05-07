package com.example.sportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain2Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getStringExtra("USER_ID") ?: return

        val redColor = Color.parseColor("#FD3433")
        val whiteColor = Color.parseColor("#FFFFFFFF")

        binding.button2.setOnClickListener {
            binding.button2.setBackgroundColor(redColor)
            binding.button2.setTextColor(whiteColor)
            saveUserChoice(userId, "Женский")
        }

        binding.button3.setOnClickListener {
            binding.button3.setBackgroundColor(redColor)
            binding.button3.setTextColor(whiteColor)
            saveUserChoice(userId, "Мужской")
        }

        binding.imageButton2.setOnClickListener {
            finish()
        }
    }

    private fun saveUserChoice(userId: String, gender: String) {
        val userChoice = hashMapOf(
            "gender" to gender
        )

        usersRef.child(userId).updateChildren(userChoice as Map<String, Any>)
            .addOnSuccessListener {
                val intent = Intent(this, MainActivity3::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
            .addOnFailureListener {

            }
    }

    override fun onResume() {
        super.onResume()

        val defaultColor = Color.parseColor("#333E3E3E")
        val defaultTextColor = Color.parseColor("#000000")
        binding.button2.setBackgroundColor(defaultColor)
        binding.button2.setTextColor(defaultTextColor)
        binding.button3.setBackgroundColor(defaultColor)
        binding.button3.setTextColor(defaultTextColor)
    }
}
