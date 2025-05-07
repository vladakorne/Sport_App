package com.example.sportapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain7Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity7 : AppCompatActivity() {

    private lateinit var binding: ActivityMain7Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button15.isEnabled = false
        binding.button15.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333E3E3E"))

        fun checkFields() {
            val n_height = binding.editTextNumber.text.toString().toIntOrNull() ?: 0
            val n_weight = binding.editTextNumber2.text.toString().toIntOrNull() ?: 0
            val n_age = binding.editTextNumber3.text.toString().toIntOrNull() ?: 0

            val isEnabled = binding.editTextNumber.text.isNotEmpty() &&
                    binding.editTextNumber2.text.isNotEmpty() &&
                    binding.editTextNumber3.text.isNotEmpty() &&
                    n_height in 51..309 &&
                    n_weight in 6..299 &&
                    n_age > 12

            binding.button15.isEnabled = isEnabled
            val color = if (isEnabled) "#FD3433" else "#333E3E3E"
            binding.button15.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editTextNumber.addTextChangedListener(textWatcher)
        binding.editTextNumber2.addTextChangedListener(textWatcher)
        binding.editTextNumber3.addTextChangedListener(textWatcher)

        binding.button15.setOnClickListener {
            val height = binding.editTextNumber.text.toString().toInt()
            val weight = binding.editTextNumber2.text.toString().toInt()
            val age = binding.editTextNumber3.text.toString().toInt()

            saveParametersToFirebase(height, weight, age)

            val intent = Intent(this@MainActivity7, MainActivity8::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton7.setOnClickListener {
            finish()
        }
    }

    private fun saveParametersToFirebase(height: Int, weight: Int, age: Int) {
        userId?.let {
            val data = mapOf(
                "height" to height,
                "weight" to weight,
                "age" to age
            )
            usersRef.child(it).updateChildren(data)
        }
    }
}
