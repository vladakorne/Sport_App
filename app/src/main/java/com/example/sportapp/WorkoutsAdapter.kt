package com.example.sportapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutsAdapter(
    private val workouts: List<Pair<String, String>>,
    private val weekPosition: Int, // Номер недели
    private val completedWorkoutsPerWeek: MutableMap<Int, MutableSet<Int>>, // Карта выполненных тренировок для каждой недели
    private val workoutViewModel: WorkoutViewModel, // Получаем доступ к ViewModel для подсчета выполненных тренировок
    private val userId: String // userId для сохранения данных в Firebase
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()
    private val completedPositions: MutableSet<Int> =
        completedWorkoutsPerWeek[weekPosition] ?: mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.titleTextView.text = workout.first
        holder.descriptionTextView.text = workout.second

        if (expandedPositions.contains(position)) {
            holder.descriptionTextView.visibility = View.VISIBLE
            holder.markAsDoneButton.visibility = View.VISIBLE
        } else {
            holder.descriptionTextView.visibility = View.GONE
            holder.markAsDoneButton.visibility = View.GONE
        }

        // Используем кастомный зеленый цвет для фона, если тренировка выполнена
        if (completedPositions.contains(position)) {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.custom_green))  // Используем кастомный зеленый для фона элемента
            // Меняем цвет текста на белый, если тренировка выполнена
            holder.titleTextView.setTextColor(Color.WHITE)
            holder.descriptionTextView.setTextColor(Color.WHITE)
            // Устанавливаем кастомный зеленый для фона кнопки
            holder.markAsDoneButton.setBackgroundColor(holder.itemView.context.getColor(R.color.custom_green_2))
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.titleTextView.setTextColor(Color.BLACK)
            holder.descriptionTextView.setTextColor(Color.BLACK)
            // Устанавливаем обычный серый фон для кнопки, если тренировка не выполнена
            holder.markAsDoneButton.setBackgroundColor(Color.LTGRAY)
        }

        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }

        holder.markAsDoneButton.setOnClickListener {
            if (!completedPositions.contains(position)) {
                completedPositions.add(position) // Добавляем тренировку в выполненные
                // Увеличиваем общий счетчик выполненных тренировок
                workoutViewModel.totalCompletedWorkouts++
                // Сохраняем количество выполненных тренировок в Firebase
                workoutViewModel.saveTotalCompletedWorkoutsToFirebase(userId)
            }
            // Сохраняем выполнение тренировки для этой недели
            completedWorkoutsPerWeek[weekPosition] = completedPositions
            // Меняем цвет текста на белый после того, как тренировка выполнена
            holder.titleTextView.setTextColor(Color.WHITE)
            holder.descriptionTextView.setTextColor(Color.WHITE)
            // Устанавливаем кастомный зеленый для фона кнопки
            holder.markAsDoneButton.setBackgroundColor(holder.itemView.context.getColor(R.color.custom_green))
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.workoutTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.workoutDescription)
        val markAsDoneButton: Button = view.findViewById(R.id.markAsDoneButton)
    }
}
