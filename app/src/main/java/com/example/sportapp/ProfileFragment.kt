package com.example.sportapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID") ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val nameTextView = view.findViewById<TextView>(R.id.textViewUserName)
        val database = Firebase.database
        val userRef = database.getReference("users").child(userId)

        userRef.child("name").get().addOnSuccessListener { dataSnapshot ->
            val name = dataSnapshot.getValue(String::class.java)
            nameTextView.text = name ?: "Имя не указано"
        }.addOnFailureListener {
            nameTextView.text = "Ошибка загрузки имени"
        }

        // Кнопка назад
        view.findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            (activity as? MainActivity13)?.showArticlesFragment()
        }

        // Открытие других фрагментов
        view.findViewById<Button>(R.id.infoButton).setOnClickListener {
            openFragmentWithUserId(InfoFragment())
        }

        view.findViewById<Button>(R.id.scheduleButton).setOnClickListener {
            openFragmentWithUserId(ScheduleFragment())
        }

        view.findViewById<Button>(R.id.settingsButton).setOnClickListener {
            openFragmentWithUserId(SettingsFragment())
        }

        // Кнопка для перехода на MainActivity
        view.findViewById<Button>(R.id.ReorganizationPlan).setOnClickListener {
            // Переход на MainActivity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun openFragmentWithUserId(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("USER_ID", userId)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}

