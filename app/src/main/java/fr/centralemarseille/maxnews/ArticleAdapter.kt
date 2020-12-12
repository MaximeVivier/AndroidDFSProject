package fr.centralemarseille.maxnews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.article_item.view.*

class ArticleAdapter(val articlesList: ArrayList<Article>, private val onArticleClickListener: onArticleClickListener) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.article_item,
            parent, false)

        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val Article = articlesList[position]

        holder.textView1.text = Article.title
        holder.textView2.text = Article.description
        holder.itemView.see_more.setOnClickListener {
            onArticleClickListener.onArticleItemClicked(position)
        }
    }

    override fun getItemCount() = articlesList.size

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.text_view_1
        val textView2: TextView = itemView.text_view_2
    }

    fun addArticlesToList (articles: Array<Article>) {
        for (article in articles){
            articlesList.add(article)
        }
    }

}