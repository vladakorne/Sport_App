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
import com.example.sportapp.databinding.ActivityMain3Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button4.isEnabled = false
        binding.button4.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333E3E3E"))

        binding.editTextText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnabled = !s.isNullOrEmpty()
                binding.button4.isEnabled = isEnabled
                val color = if (isEnabled) "#FD3433" else "#333E3E3E"
                binding.button4.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.button4.setOnClickListener {
            val name = binding.editTextText.text.toString()
            saveNameToFirebase(name)

            val intent = Intent(this@MainActivity3, MainActivity4::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton3.setOnClickListener {
            finish()
        }
    }

    private fun saveNameToFirebase(name: String) {
        userId?.let {
            usersRef.child(it).child("name").setValue(name)
        }
    }
}
