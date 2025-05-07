package com.example.sportapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsFragment : Fragment() {

    private lateinit var nameField: EditText
    private lateinit var heightField: EditText
    private lateinit var weightField: EditText
    private lateinit var ageField: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    private var userId: String? = null
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        nameField = view.findViewById(R.id.editTextName)
        heightField = view.findViewById(R.id.editTextHeight)
        weightField = view.findViewById(R.id.editTextWeight)
        ageField = view.findViewById(R.id.editTextAge)
        saveButton = view.findViewById(R.id.buttonSave)
        backButton = view.findViewById(R.id.imageButtonBack)

        // Получение userId из activity
        userId = activity?.intent?.getStringExtra("USER_ID")

        loadUserData()

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        saveButton.setOnClickListener {
            saveUserData()
        }

        return view
    }

    private fun loadUserData() {
        userId?.let { uid ->
            usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nameField.setText(snapshot.child("name").getValue(String::class.java) ?: "")
                    heightField.setText(
                        snapshot.child("height").getValue(Int::class.java)?.toString() ?: ""
                    )
                    weightField.setText(
                        snapshot.child("weight").getValue(Int::class.java)?.toString() ?: ""
                    )
                    ageField.setText(
                        snapshot.child("age").getValue(Int::class.java)?.toString() ?: ""
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserData() {
        val name = nameField.text.toString()
        val height = heightField.text.toString().toIntOrNull()
        val weight = weightField.text.toString().toIntOrNull()
        val age = ageField.text.toString().toIntOrNull()

        if (name.isBlank() || height == null || weight == null || age == null) {
            Toast.makeText(requireContext(), "Проверьте правильность ввода", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val updates = mapOf(
            "name" to name,
            "height" to height,
            "weight" to weight,
            "age" to age
        )

        userId?.let {
            usersRef.child(it).updateChildren(updates).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
