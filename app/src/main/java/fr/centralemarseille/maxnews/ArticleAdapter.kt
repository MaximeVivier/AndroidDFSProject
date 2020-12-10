package fr.centralemarseille.maxnews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.article_item.view.*

class ArticleAdapter(private val exampleList: List<Article>, private val onArticleClickListener: onArticleClickListener) :
    RecyclerView.Adapter<ArticleAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.article_item,
            parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val Article = exampleList[position]
        Log.d("ADAPTER", "article --> " + Article.toString())

        holder.textView1.text = Article.title
        holder.textView2.text = Article.description
        holder.itemView.see_more.setOnClickListener {
            onArticleClickListener.onArticleItemClicked(position)
        }
    }

    override fun getItemCount() = exampleList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.text_view_1
        val textView2: TextView = itemView.text_view_2
    }
}