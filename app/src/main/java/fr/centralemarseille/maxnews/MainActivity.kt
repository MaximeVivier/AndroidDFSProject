package fr.centralemarseille.maxnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), onArticleClickListener {
    val url_news: String = "https://newsapi.org/v2/sources?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr"
    val articles_list = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        // getSources(url_news)
        getArticlesFromSource("google-news-fr")
    }

    fun getSources(URL_sources: String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        Log.d(TAG, "get sources")
        var gson = Gson()

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, URL_sources,
            { response ->
                var sourcesObject = gson.fromJson(response.toString(), SourcesObjectFromAPINews::class.java)
                list_sources.text = sourcesObject.sources[1].description
            },
            {
                Log.d(TAG, "ERROR $it")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(stringReq)
    }

    fun getArticlesFromSource(source: String) {
        // Instantiate the RequestQueue.
        val url_articles: String = "https://newsapi.org/v2/everything?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr&sources=$source"
        val queue = Volley.newRequestQueue(this)
        Log.d(TAG, "get articles")
        var gson = Gson()

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, url_articles,
            { response ->
                var articlesObject = gson.fromJson(response.toString(), ArticlesObjectFromAPINews::class.java).articles
                for (article in articlesObject) {
                    articles_list.add(article)
                }
                recycler_view.adapter = ArticleAdapter(articles_list, this)
                recycler_view.layoutManager = LinearLayoutManager(this)
                recycler_view.setHasFixedSize(true)
            },
            {
                Log.d(TAG, "ERROR $it")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(stringReq)
    }

    override fun onArticleItemClicked(position: Int) {
        val article = articles_list[position]
        val articleIntent = Intent(this, ArticleDetailActivity::class.java)
        articleIntent.putExtra("titre", article.title)
        articleIntent.putExtra("auteur", article.author)
        articleIntent.putExtra("source", article.source.name)
        articleIntent.putExtra("description", article.description)
        articleIntent.putExtra("date", article.publishedAt)
        articleIntent.putExtra("lien_article", article.url)
        articleIntent.putExtra("urlImage", article.urlToImage)
        startActivity(articleIntent)
    }

}
