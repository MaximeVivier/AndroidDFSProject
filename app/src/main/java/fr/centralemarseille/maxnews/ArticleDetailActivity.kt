package fr.centralemarseille.maxnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        titre.text = intent.getStringExtra("titre")
        date.text = intent.getStringExtra("date")
        source.text = intent.getStringExtra("source")

        /*
        findViewById<Button>(R.id.linkButton).setOnClickListener{
            val webViewIntent = Intent(this, WebViewActivity::class.java)
            webViewIntent.putExtra("link", intent.getStringExtra("link"))
            startActivity(webViewIntent)
        }
        */

        // Picasso.get().load(intent.getStringExtra("urlToImage")).into(findViewById<ImageView>(R.id.imageView))
    }
}