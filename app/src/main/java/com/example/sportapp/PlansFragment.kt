package com.example.sportapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlansFragment : Fragment() {

    private lateinit var weekSpinner: Spinner
    private lateinit var plansRecyclerView: RecyclerView

    private var userId: String? = null
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var workouts: List<Pair<String, String>> = emptyList() // Для хранения тренировки

    // Получаем доступ к ViewModel
    private val workoutViewModel: WorkoutViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weekSpinner = view.findViewById(R.id.weekSpinner)
        plansRecyclerView = view.findViewById(R.id.plansRecyclerView)

        userId = activity?.intent?.getStringExtra("USER_ID")

        // Настроим RecyclerView
        plansRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchWorkoutPlan()
    }

    private fun fetchWorkoutPlan() {
        userId?.let { uid ->
            // Получаем тип плана (plan_for) из users/{userId}/plan_for
            usersRef.child(uid).child("plan_for")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val planFor = snapshot.getValue(String::class.java)

                        // Получаем текст тренировки из users/{userId}/plans/response
                        usersRef.child(uid).child("plans").child("response")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val workoutText = snapshot.getValue(String::class.java)

                                    // Настроим спиннер
                                    val numberOfWeeks = when (planFor?.lowercase()) {
                                        "неделя" -> 1
                                        "месяц" -> 4
                                        "3 месяца" -> 12
                                        else -> 0
                                    }

                                    setupSpinner(numberOfWeeks)

                                    // Преобразуем текст тренировки в список с днями и их описаниями
                                    workouts = parseWorkoutText(workoutText)

                                    // Установим тренировки в RecyclerView, передаем workoutViewModel и userId
                                    plansRecyclerView.adapter = WorkoutsAdapter(
                                        workouts,
                                        0,
                                        workoutViewModel.completedWorkoutsPerWeek,
                                        workoutViewModel,
                                        userId!!
                                    )
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Ошибка загрузки плана",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки типа плана",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun setupSpinner(weeksCount: Int) {
        val weekTitles = List(weeksCount) { "Неделя ${it + 1}" }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weekSpinner.adapter = adapter

        // Слушатель выбора недели
        weekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    requireContext(),
                    "Выбрана неделя: ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()

                // Обновляем RecyclerView с выбранной неделей
                plansRecyclerView.adapter = WorkoutsAdapter(
                    workouts,
                    position,
                    workoutViewModel.completedWorkoutsPerWeek,
                    workoutViewModel,
                    userId!!
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Метод для разбивки текста по дням недели и удаление символов **
    private fun parseWorkoutText(workoutText: String?): List<Pair<String, String>> {
        val workoutDays = mutableListOf<Pair<String, String>>()

        workoutText?.let {
            // Убираем все символы "**" из текста
            val cleanedText = workoutText.replace("**", "")

            // Регулярное выражение для поиска дней недели: Понедельник, Вторник, Среда и т. д.
            val dayRegex =
                """(Понедельник|Вторник|Среда|Четверг|Пятница|Суббота|Воскресенье)""".toRegex()
            val matches = dayRegex.findAll(cleanedText)

            var startIndex = 0
            for (match in matches) {
                val day = match.groupValues[1].trim()  // Например, "Понедельник"
                val endIndex = match.range.last

                // Ищем следующий день недели, чтобы правильно выделить описание между днями
                val nextMatch = matches.find { it.range.first > endIndex }
                val nextDayStartIndex = nextMatch?.range?.first ?: cleanedText.length

                // Получаем описание тренировки между днями
                val workoutDescription =
                    cleanedText.substring(endIndex + 1, nextDayStartIndex).trim()

                // Добавляем пару "День недели" и "Описание"
                workoutDays.add(Pair(day, workoutDescription))
                startIndex = nextDayStartIndex
            }
        }

        return workoutDays
    }
}
