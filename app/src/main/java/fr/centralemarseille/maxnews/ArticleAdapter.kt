package fr.centralemarseille.maxnews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.IOException

class ArticleAdapter(val articlesList: ArrayList<Article>, private val onArticleClickListener: onArticleClickListener) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val title_view: TextView = itemView.findViewById(R.id.title_view)
        val auteur_view: TextView = itemView.findViewById(R.id.auteur_view)
        val date_view: TextView = itemView.findViewById(R.id.date_view)
        val see_more_button: Button = itemView.findViewById(R.id.see_more_button)
        val image_article: ImageView = itemView.findViewById(R.id.image_article)
        val holder_context: Context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        if (viewType == 0) {
            return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item_left, parent, false), parent.context)
        } else {
            return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item_right, parent, false), parent.context)
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val Article = articlesList[position]

        holder.title_view.text = Article.title
        holder.auteur_view.text = Article.author
        holder.date_view.text = Article.publishedAt
        holder.see_more_button.setOnClickListener {
            onArticleClickListener.onArticleItemClicked(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount() = articlesList.size

    fun addArticlesToList (articles: Array<Article>) {
        for (article in articles){
            articlesList.add(article)
        }
    }

}