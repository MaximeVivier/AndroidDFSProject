package fr.centralemarseille.maxnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        titre.text = intent.getStringExtra("titre")
        date.text = intent.getStringExtra("date")
        source.text = intent.getStringExtra("source")
        description.text = intent.getStringExtra("description")
        auteur.text = intent.getStringExtra("auteur")

        lire_article.setOnClickListener{
            val intentArticle = Intent(this, WebViewActivity::class.java)
            intentArticle.putExtra("lien_article", intent.getStringExtra("lien_article"))
            startActivity(intentArticle)
        }

        if (intent.getStringExtra("urlImage") != null) {
            Picasso.with(this).load(intent.getStringExtra("urlImage")).into(image_article)
        }
    }
}