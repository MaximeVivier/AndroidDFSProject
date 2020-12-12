package fr.centralemarseille.maxnews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), onArticleClickListener {
    val url_sources_news: String = "https://newsapi.org/v2/sources?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr"
    val articles_list = ArrayList<Article>()

    lateinit var source_spinner: Spinner;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        // getSources(url_news)
        getSources(url_sources_news)
        getArticlesFromSource("google-news-fr")
        source_spinner = source_chooser

        val arrayList1 = ArrayList<String>()

        arrayList1.add("google-news-fr")

        val source_adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,
                arrayList1
            )
        source_spinner.setAdapter(source_adapter);

        source_spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, arg3: Long
            ) {
                val city = "The city is " + parent.getItemAtPosition(position).toString()
                Toast.makeText(parent.context, city, Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
    }

    fun getSources(URL_sources: String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        Log.d(TAG, "get sources")
        var gson = Gson()

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, URL_sources,
            { response ->
                var sourcesObject = gson.fromJson(
                    response.toString(),
                    SourcesObjectFromAPINews::class.java
                )
                val arraySources = ArrayList<Source>()
                for (source in sourcesObject.sources) {
                    arraySources.add(source)
                }
                val source_adapter: ArrayAdapter<Source> =
                    ArrayAdapter<Source>(
                        this, android.R.layout.simple_spinner_dropdown_item,
                        arraySources
                    )
                source_spinner.setAdapter(source_adapter)
                // list_sources.text = sourcesObject.sources[1].description
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
                var articlesObject = gson.fromJson(
                    response.toString(),
                    ArticlesObjectFromAPINews::class.java
                ).articles
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
