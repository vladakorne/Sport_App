package com.example.sportapp

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WorkoutViewModel : ViewModel() {
    // Карта для хранения выполненных тренировок по неделям
    val completedWorkoutsPerWeek = mutableMapOf<Int, MutableSet<Int>>()

    // Переменная для подсчета количества выполненных тренировок
    var totalCompletedWorkouts = 0

    // Firebase reference
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    // Метод для сохранения количества выполненных тренировок в Firebase
    fun saveTotalCompletedWorkoutsToFirebase(userId: String) {
        // Сохраняем количество выполненных тренировок в Firebase
        usersRef.child(userId).child("totalCompletedWorkouts").setValue(totalCompletedWorkouts)
    }
}
