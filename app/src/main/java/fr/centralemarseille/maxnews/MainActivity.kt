package fr.centralemarseille.maxnews

import android.content.Intent
import android.content.SharedPreferences
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
    var articles_list = ArrayList<Article>()
    var sources_list = ArrayList<Source>()
    var gson = Gson()

    lateinit var source_spinner: Spinner;
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        preferences = getPreferences(MODE_PRIVATE)

        val lastSavedSource: Source? = getLastSavedSource()
        if (lastSavedSource != null) {
            sources_list.add(lastSavedSource)
        }

        // getSources(url_news)
        source_spinner = source_chooser

        val first_source_adapter: ArrayAdapter<Source> =
            ArrayAdapter<Source>(
                this, android.R.layout.simple_spinner_dropdown_item,
                sources_list
            )
        source_spinner.setAdapter(first_source_adapter)
        getSources(url_sources_news, lastSavedSource)


        /*
        val arrayList1 = ArrayList<String>()

        arrayList1.add("google-news-fr")

        val source_adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,
                arrayList1
            )
        source_spinner.setAdapter(source_adapter);
        */

        source_spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, arg3: Long
            ) {
                if (sources_list.size > 1) {
                    getArticlesFromSource(sources_list[source_spinner.getSelectedItemPosition()].id)
                    saveCurrentSource(sources_list[source_spinner.getSelectedItemPosition()])
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
    }

    fun getSources(URL_sources: String, lastSavedSource: Source?) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        Log.d(TAG, "get sources")

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, URL_sources,
            { response ->
                var sourcesObject = gson.fromJson(
                    response.toString(),
                    SourcesObjectFromAPINews::class.java
                )
                sources_list = ArrayList<Source>()
                for (source in sourcesObject.sources) {
                    sources_list.add(source)
                }
                val source_adapter: ArrayAdapter<Source> =
                    ArrayAdapter<Source>(
                        this, android.R.layout.simple_spinner_dropdown_item,
                        sources_list
                    )
                source_spinner.setAdapter(source_adapter)
                if (lastSavedSource != null) {
                    val spinnerPosition: Int = source_adapter.getPosition(lastSavedSource)
                    source_spinner.setSelection(spinnerPosition)
                }
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

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, url_articles,
            { response ->
                var articlesObject = gson.fromJson(
                    response.toString(),
                    ArticlesObjectFromAPINews::class.java
                ).articles
                articles_list = ArrayList<Article>()
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

    private fun saveCurrentSource(sourceToSave: Source) {
        with (preferences.edit()) {
            putString("current_source", gson.toJson(sourceToSave))
            apply()
        }
    }

    private fun getLastSavedSource(): Source? {
        return gson.fromJson(preferences.getString("current_source", null), Source::class.java)
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
