package fr.centralemarseille.maxnews

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), onArticleClickListener {
    val url_sources_news: String = "https://newsapi.org/v2/sources?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr"
    var sources_list = ArrayList<Source>()
    var gson = Gson()
    val fm: FragmentManager = supportFragmentManager

    lateinit var articles_adapter: ArticleAdapter
    lateinit var source_spinner: Spinner;
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        var current_page: Int = 1

        fm.beginTransaction()
            .show(progressBarFragment)
            .commit()

        articlesLayout.visibility = LinearLayout.INVISIBLE;

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

        source_spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, arg3: Long
            ) {
                if (sources_list.size > 1) {
                    current_page = getArticlesFromSource(
                        sources_list[source_spinner.getSelectedItemPosition()].id,
                        1,
                        sources_list[source_spinner.getSelectedItemPosition()]
                    )
                    saveCurrentSource(sources_list[source_spinner.getSelectedItemPosition()])
                    if (articlesLayout.visibility == LinearLayout.INVISIBLE) {
                        fm.beginTransaction()
                            .hide(progressBarFragment)
                            .commit()
                        articlesLayout.visibility = LinearLayout.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        articles_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == 0) {
                    current_page += 1
                    current_page = getArticlesFromSource(
                        sources_list[source_spinner.getSelectedItemPosition()].id,
                        current_page,
                        sources_list[source_spinner.getSelectedItemPosition()]
                    )
                }
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
            },
            {
                popUpConnectionProblem(lastSavedSource)
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

    fun getArticlesFromSource(source: String, page: Int, lastSavedSource: Source?): Int {
        // Instantiate the RequestQueue.
        var url_articles: String = "https://newsapi.org/v2/everything?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr&sources=$source"
        if (page > 1) {
            url_articles = url_articles + "&page=$page"
        }
        val queue = Volley.newRequestQueue(this)
        Log.d(TAG, "get articles")

        // Request a string response from the provided URL.
        val stringReq = object: StringRequest(Request.Method.GET, url_articles,
            { response ->
                var articlesObject = gson.fromJson(
                    response.toString(),
                    ArticlesObjectFromAPINews::class.java
                ).articles
                if (page == 1) {
                    setUpRecyclerView()
                }
                articles_adapter.addArticlesToList(articlesObject)
                articles_adapter.notifyDataSetChanged()
            },
            {
                popUpConnectionProblem(lastSavedSource)
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
        return page
    }

    private fun saveCurrentSource(sourceToSave: Source) {
        with(preferences.edit()) {
            putString("current_source", gson.toJson(sourceToSave))
            apply()
        }
    }

    private fun getLastSavedSource(): Source? {
        return gson.fromJson(preferences.getString("current_source", null), Source::class.java)
    }

    fun setUpRecyclerView () {
        articles_adapter = ArticleAdapter(ArrayList<Article>(), this)
        articles_recycler_view.adapter = articles_adapter
        articles_recycler_view.layoutManager = LinearLayoutManager(this)
        articles_recycler_view.setHasFixedSize(true)
    }

    fun popUpConnectionProblem(lastSavedSource: Source?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("An issue occured when fetching data")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("Retry") { _, _ ->
            getSources(url_sources_news, lastSavedSource)
        }

        builder.setNegativeButton("OK") { _, _ ->
            fm.beginTransaction()
                .hide(progressBarFragment)
                .commit()
        }
        builder.show()
    }

    override fun onArticleItemClicked(position: Int) {
        val article = articles_adapter.articlesList[position]
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
