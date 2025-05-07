package com.example.sportapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.os.AsyncTask
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ChatFragment : Fragment() {

    private lateinit var chatHistory: TextView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imageButtonBack: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Инициализация UI элементов через view
        chatHistory = view.findViewById(R.id.chatHistory)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        progressBar = view.findViewById(R.id.progressBar)
        imageButtonBack = view.findViewById(R.id.imageButtonBack)

        // Обработчик нажатия кнопки назад
        imageButtonBack.setOnClickListener {
            (activity as? MainActivity13)?.showArticlesFragment()
        }

        // Обработчик нажатия кнопки "Отправить"
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessageToChat("Вы: $message")
                messageInput.text.clear()
                sendMessageToDeepSeek(message)
            }
        }

        return view
    }

    // Метод для добавления сообщений в чат
    private fun addMessageToChat(message: String) {
        activity?.runOnUiThread {
            val currentText = chatHistory.text.toString()
            chatHistory.text = if (currentText.isEmpty()) {
                message
            } else {
                "$currentText\n\n$message"
            }
        }
    }

    // Метод для отправки сообщений в DeepSeek API
    private fun sendMessageToDeepSeek(message: String) {
        progressBar.visibility = View.VISIBLE
        sendButton.isEnabled = false

        // Выполнение запроса к API
        val apiTask = object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg params: String): String {
                return try {
                    val client = OkHttpClient()

                    // Убедитесь, что используете правильный URL API
                    val url = "https://api.deepseek.com/v1/chat/completions"

                    // API ключ должен храниться безопасно (НЕ в коде!)
                    val apiKey =
                        "sk-b180269c94934bb3b0ab812f25df4b04" // Замените на реальный ключ из безопасного хранилища

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
                    response.body?.string() ?: throw Exception("Пустой ответ от сервера")
                } catch (e: Exception) {
                    "Ошибка: ${e.message}"
                }
            }

            override fun onPostExecute(result: String) {
                progressBar.visibility = View.GONE
                sendButton.isEnabled = true

                try {
                    if (result.startsWith("Ошибка:")) {
                        addMessageToChat(result)
                        return
                    }

                    val json = JSONObject(result)
                    when {
                        json.has("choices") -> {
                            val choices = json.getJSONArray("choices")
                            if (choices.length() > 0) {
                                val firstChoice = choices.getJSONObject(0)
                                if (firstChoice.has("message")) {
                                    val messageObj = firstChoice.getJSONObject("message")
                                    val content = messageObj.getString("content")
                                    addMessageToChat("Помощник АЛГУС: $content")
                                } else {
                                    addMessageToChat("Ошибка: в ответе нет сообщения")
                                }
                            } else {
                                addMessageToChat("Ошибка: массив choices пуст")
                            }
                        }

                        json.has("error") -> {
                            val error = json.optJSONObject("error")?.getString("message")
                                ?: json.getString("error")
                            addMessageToChat("Ошибка API: $error")
                        }

                        else -> {
                            addMessageToChat("Неожиданный формат ответа: $result")
                        }
                    }
                } catch (e: Exception) {
                    addMessageToChat("Ошибка обработки ответа: ${e.message}\nПолный ответ: $result")
                }
            }
        }

        apiTask.execute(message)
    }
}
