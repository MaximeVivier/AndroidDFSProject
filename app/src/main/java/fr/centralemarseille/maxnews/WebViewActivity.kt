package fr.centralemarseille.maxnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    val fm: FragmentManager = supportFragmentManager

    private lateinit var webView: WebView
    lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = webview_article
        webView.settings.setJavaScriptEnabled(true)

        webView.visibility = View.INVISIBLE
        fm.beginTransaction()
            .show(progressBarFragmentWeb)
            .commit()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.visibility = View.VISIBLE
                fm.beginTransaction()
                    .hide(progressBarFragmentWeb)
                    .commit()
            }
        }
        val url: String? = intent.getStringExtra("lien_article")
        if (url != null) {
            webView.loadUrl(url)
        }
    }
}