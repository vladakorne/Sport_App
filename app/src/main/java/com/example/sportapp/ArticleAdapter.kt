package com.example.sportapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.databinding.ItemArticleBinding

class ArticleAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemArticleBinding.bind(view)

        fun bind(article: Article) {
            binding.articleTitle.text = article.title
            binding.articleDescription.text = article.description

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                    putExtra("title", article.title)
                    putExtra("content", article.content)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size
}
