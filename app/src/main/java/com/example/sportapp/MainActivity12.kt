package com.example.sportapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.sportapp.databinding.ActivityMain12Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity12 : AppCompatActivity() {

    private lateinit var binding: ActivityMain12Binding
    private lateinit var userId: String
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain12Binding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imageButton12.setOnClickListener {
            finish()
        }

        val lottieView: LottieAnimationView = binding.lottieView
        lottieView.repeatCount = LottieDrawable.INFINITE
        lottieView.playAnimation()

        generateWorkoutPlan()
    }

    private fun generateWorkoutPlan() {
        val userRef = databaseRef.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val age = snapshot.child("age").getValue(Int::class.java)
                val gender = snapshot.child("gender").getValue(String::class.java)
                val height = snapshot.child("height").getValue(Int::class.java)
                val weight = snapshot.child("weight").getValue(Int::class.java)
                val experience = snapshot.child("experience").getValue(String::class.java)
                val planFor = snapshot.child("plan_for").getValue(String::class.java)
                val trainingTime = snapshot.child("training_time").getValue(String::class.java)
                val workoutType = snapshot.child("workoutType").getValue(String::class.java)

                val diseasesList = mutableListOf<String>()
                snapshot.child("diseases").children.forEach {
                    val disease = it.getValue(String::class.java)
                    if (!disease.isNullOrBlank()) {
                        diseasesList.add(disease)
                    }
                }

                val dayMap = mapOf(
                    "2" to "Понедельник", "3" to "Вторник", "4" to "Среда",
                    "5" to "Четверг", "6" to "Пятница", "7" to "Суббота", "1" to "Воскресенье"
                )
                val selectedDays = snapshot.child("selected_days").children.mapNotNull {
                    val dayNumber = it.getValue(Int::class.java)?.toString()
                    dayMap[dayNumber]
                }

                val requestText = buildRequestString(
                    age, gender, height, weight, experience,
                    planFor, trainingTime, workoutType, selectedDays, diseasesList
                )

                userRef.child("plans").child("text").setValue(requestText)

                sendMessageToDeepSeek(requestText) { responseText ->
                    userRef.child("plans").child("response").setValue(responseText)
                        .addOnCompleteListener {
                            binding.lottieView.cancelAnimation()
                            binding.lottieView.visibility = View.GONE

                            val nextIntent = Intent(this@MainActivity12, MainActivity13::class.java)
                            nextIntent.putExtra("USER_ID", userId)
                            startActivity(nextIntent)
                            finish()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // обработка ошибки при необходимости
            }
        })
    }

    private fun buildRequestString(
        age: Int?, gender: String?, height: Int?, weight: Int?, experience: String?,
        planFor: String?, trainingTime: String?, workoutType: String?,
        days: List<String>, diseases: List<String>
    ): String {
        return listOf(
            "Создай подробный персонализированный план тренировок с очень подробными пояснениями выполнения упражнений, план должен содержать только упражнения по дням недели ничего больше, без наименований тренировок,(среда: упражнения, четверг: упражнения пример), также упоминай дни недели исключительно перед планами тренировки, больше нигде, не упоминай дни недели которые не фигурируют в запросе.",
            "Возраст: ${age ?: "-"}",
            "Медицинские ограничения: ${if (diseases.isNotEmpty()) diseases.joinToString(", ") else "-"}",
            "Уровень подготовки: ${experience ?: "-"}",
            "Пол: ${gender ?: "-"}",
            "Рост: ${height ?: "-"}",
            "Продолжительность плана тренировок: ${planFor ?: "-"}",
            "Дни тренировок: ${if (days.isNotEmpty()) days.joinToString(", ") else "-"}",
            "Продолжительность одной тренировки: ${trainingTime ?: "-"}",
            "Вес: ${weight ?: "-"}",
            "Тип тренировок: ${workoutType ?: "-"}"
        ).joinToString(separator = "\n")
    }

    private fun sendMessageToDeepSeek(message: String, onResult: (String) -> Unit) {
        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()

                val url = "https://api.deepseek.com/v1/chat/completions"
                val apiKey = "sk-b180269c94934bb3b0ab812f25df4b04"

                val json = JSONObject().apply {
                    put("model", "deepseek-chat")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", message)
                        })
                    })
                    put("temperature", 0.7)
                    put("max_tokens", 2000)
                }

                val body = json.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: "Пустой ответ от нейросети"

                val parsed = JSONObject(responseBody)
                val result = if (parsed.has("choices")) {
                    val choices = parsed.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val messageObj = choices.getJSONObject(0).getJSONObject("message")
                        messageObj.getString("content")
                    } else {
                        "Ошибка: пустой список ответов"
                    }
                } else if (parsed.has("error")) {
                    "Ошибка API: ${parsed.getJSONObject("error").getString("message")}"
                } else {
                    "Ошибка: неожиданный формат ответа"
                }

                runOnUiThread {
                    onResult(result)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    onResult("Ошибка отправки запроса: ${e.message}")
                }
            }
        }.start()
    }
}
