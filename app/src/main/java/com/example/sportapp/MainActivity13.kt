package com.example.sportapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.databinding.ActivityMain13Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class MainActivity13 : AppCompatActivity() {
    private lateinit var binding: ActivityMain13Binding
    private lateinit var userId: String

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain13Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getStringExtra("USER_ID") ?: return

        loadArticles()

        binding.imageButton.setOnClickListener {
            showFragmentWithUserId(ProfileFragment())
            hideArticles()
        }

        binding.imageButton2.setOnClickListener {
            showFragmentWithUserId(PlansFragment())
            hideArticles()
        }


        binding.imageButton3.setOnClickListener {
            showFragmentWithUserId(ChatFragment())
            hideArticles()
        }

        binding.imageButton4.setOnClickListener {
            showFragmentWithUserId(StatsFragment())
            hideArticles()
        }
    }

    private fun showFragmentWithUserId(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("USER_ID", userId)
        fragment.arguments = bundle

        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        updateUI()
    }

    private fun hideArticles() {
        binding.articlesRecyclerView.visibility = View.GONE
        binding.textView20.visibility = View.GONE
    }

    private fun showArticles() {
        binding.articlesRecyclerView.visibility = View.VISIBLE
        binding.textView20.visibility = View.VISIBLE
    }

    private fun updateUI() {
        if (currentFragment == null) {
            showArticles()
        } else {
            hideArticles()
        }
    }

    private fun loadArticles() {
        val articles = loadArticlesFromJson()
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.articlesRecyclerView.adapter = ArticleAdapter(articles)
    }

    private fun loadArticlesFromJson(): List<Article> {
        return try {
            val inputStream: InputStream = assets.open("articles.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, StandardCharsets.UTF_8)
            val listType = object : TypeToken<List<Article>>() {}.type

            Gson().fromJson(json, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun showArticlesFragment() {
        supportFragmentManager.beginTransaction()
            .remove(currentFragment ?: return)
            .commit()
        currentFragment = null
        showArticles()
    }
}
