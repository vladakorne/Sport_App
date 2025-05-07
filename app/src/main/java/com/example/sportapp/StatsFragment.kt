package com.example.sportapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class StatsFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var totalWorkoutsText: TextView
    private lateinit var plannedWorkoutsText: TextView
    private lateinit var averageNpsText: TextView  // Для среднего балла
    private lateinit var pieChart: PieChart

    private lateinit var userId: String
    private lateinit var databaseRef: DatabaseReference

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "AppPrefs"
    private val KEY_NPS_SHOWN = "nps_shown"

    private var totalPlannedWorkouts = 0
    private var totalCompletedWorkouts = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        totalWorkoutsText = view.findViewById(R.id.totalWorkoutsText)
        plannedWorkoutsText = view.findViewById(R.id.plannedWorkoutsText)
        pieChart = view.findViewById(R.id.pieChart)

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        userId = activity?.intent?.getStringExtra("USER_ID") ?: return view
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        getUserData()
        fetchAverageNpsScore()

        if (!sharedPreferences.getBoolean(KEY_NPS_SHOWN, false)) {
            showNpsSurvey()
        }

        view.findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            // Обработать возврат
        }

        return view
    }

    private fun showNpsSurvey() {
        val intent = Intent(requireContext(), NPSActivity::class.java)
        startActivity(intent)
        sharedPreferences.edit().putBoolean(KEY_NPS_SHOWN, true).apply()
    }

    private fun getUserData() {
        val userRef = databaseRef.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val planFor = snapshot.child("plan_for").getValue(String::class.java)
                totalCompletedWorkouts =
                    snapshot.child("totalCompletedWorkouts").getValue(Int::class.java) ?: 0

                val selectedDays = snapshot.child("selected_days").children.mapNotNull {
                    it.getValue(Int::class.java)?.toString()
                }
                val selectedDaysCount = selectedDays.size

                totalPlannedWorkouts = calculatePlannedWorkouts(selectedDaysCount, planFor)
                totalWorkoutsText.text = "Выполнено: $totalCompletedWorkouts"
                plannedWorkoutsText.text = "Планировано: $totalPlannedWorkouts"

                updateProgress()
                updatePieChart()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calculatePlannedWorkouts(selectedDaysCount: Int, planFor: String?): Int {
        val weeksInPlan = when (planFor?.trim()?.lowercase(Locale.getDefault())) {
            "неделя" -> 1
            "месяц" -> 4
            "3 месяца" -> 12
            else -> 0
        }
        return selectedDaysCount * weeksInPlan
    }

    private fun updateProgress() {
        val progress = if (totalPlannedWorkouts > 0) {
            (totalCompletedWorkouts.toFloat() / totalPlannedWorkouts.toFloat()) * 100
        } else {
            0f
        }
        progressBar.progress = progress.toInt()

        val progressPercent = String.format("%.1f", progress)
        progressText.text = "Прогресс: $progressPercent%"
    }

    private fun updatePieChart() {
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(totalCompletedWorkouts.toFloat(), "Выполненные"))
        entries.add(
            PieEntry(
                (totalPlannedWorkouts - totalCompletedWorkouts).toFloat(),
                "Оставшиеся"
            )
        )

        val colors = listOf(
            resources.getColor(R.color.completed_color),
            resources.getColor(R.color.remaining_color)
        )

        val dataSet = PieDataSet(entries, "Прогресс")
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        val description = pieChart.description
        description.isEnabled = false

        val legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        pieChart.invalidate()
    }

    // Новый метод для подсчета среднего балла
    private fun fetchAverageNpsScore() {
        val npsRef = FirebaseDatabase.getInstance().getReference("nps_feedback")

        npsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalScore = 0
                var count = 0

                for (child in snapshot.children) {
                    val score = child.getValue(Int::class.java)
                    if (score != null) {
                        totalScore += score
                        count++
                    }
                }

                if (count > 0) {
                    val average = totalScore.toFloat() / count.toFloat()
                    val averageFormatted = String.format("%.1f", average)

                    Log.d("NPS", "Средний балл удовлетворенности: $averageFormatted / 10")

                    // Сохраняем среднюю оценку и количество отзывов в базу
                    val averageRef = FirebaseDatabase.getInstance().getReference("nps_average")
                    val averageData = mapOf(
                        "average" to averageFormatted,
                        "totalResponses" to count
                    )

                    averageRef.setValue(averageData)
                } else {
                    Log.d("NPS", "Нет данных по отзывам")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NPS", "Ошибка загрузки отзывов: ${error.message}")
            }
        })
    }
}
