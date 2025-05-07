package com.example.sportapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: "Статья"
        val content = intent.getStringExtra("content") ?: "Нет данных"

        binding.articleTitle.text = title
        binding.articleContent.text = content
    }


}
