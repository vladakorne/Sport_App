package com.example.sportapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportapp.databinding.ActivityMain8Binding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yourpackage.adapter.DiseaseAdapter

class MainActivity8 : AppCompatActivity() {
    private lateinit var binding: ActivityMain8Binding
    private val database = Firebase.database
    private val usersRef = database.getReference("users")
    private var userId: String? = null
    private val listDiseases: MutableList<DiseaseItem> = mutableListOf()
    private lateinit var adapter: DiseaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain8Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("USER_ID") ?: return
        binding.button17.isEnabled = false
        binding.button17.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333E3E3E"))

        adapter = DiseaseAdapter(this, listDiseases)
        binding.listView.adapter = adapter

        binding.button18.setOnClickListener {
            val text = binding.editTextText2.text.toString().trim()
            if (text.isNotEmpty()) {
                listDiseases.add(0, DiseaseItem(text))
                adapter.notifyDataSetChanged()
                binding.editTextText2.text.clear()
                updateButtonState()
            }
        }

        binding.textView13.setOnClickListener {
            if (!binding.button17.isEnabled) {
                saveDiseasesToFirebase(listOf("Нет ограничений"))
                val intent = Intent(this, MainActivity9::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
        }

        binding.button17.setOnClickListener {
            val diseaseList = listDiseases.map { it.name }
            saveDiseasesToFirebase(diseaseList)
            val intent = Intent(this@MainActivity8, MainActivity9::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.imageButton2.setOnClickListener {
            finish()
        }
    }

    private fun saveDiseasesToFirebase(diseases: List<String>) {
        userId?.let {
            usersRef.child(it).child("diseases").setValue(diseases)
        }
    }

    fun updateButtonState() {
        val hasItems = listDiseases.isNotEmpty()

        if (hasItems) {
            binding.button17.isEnabled = true
            binding.button17.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FD3433"))
        } else {
            binding.button17.isEnabled = false
            binding.button17.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#333E3E3E"))
        }

        binding.textView13.setTextColor(if (binding.button17.isEnabled) Color.GRAY else Color.BLACK)
        binding.textView13.isClickable = !binding.button17.isEnabled
    }
}
