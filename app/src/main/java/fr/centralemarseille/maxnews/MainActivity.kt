package fr.centralemarseille.maxnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    val url_news: String = "https://newsapi.org/v2/sources?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        getSources(url_news)
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
}

data class SourcesObjectFromAPINews(
    val status: String,
    val sources: Array<Source>
)

data class ArticlesObjectFromAPINews(
    val status: String,
    val articles: Array<Article>
)

data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val urlToImage: String,
    val description: String,
    val url: String,
    val publishedAt: String,
    val content: String,
)

data class Source(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String,
)