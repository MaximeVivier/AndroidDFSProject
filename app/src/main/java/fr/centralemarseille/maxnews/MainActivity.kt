package fr.centralemarseille.maxnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.ResponseCache
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.Throws

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    val url_news: String = "https://newsapi.org/v2/sources?apiKey=86a0af66e21e4e5a8ec29e0870d4317d&language=fr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity main is lauched")

        Thread{
            var str: String = ""
            try {
                str = requestGET(url_news)
                runOnUiThread({
                    Log.d(TAG, "Response url " + str)
                })
            } catch (e: Exception) {
                Log.d(TAG, "Error fetching data : " + e.toString())
            }
        }.start()

    }

    @Throws(IOException::class)
    fun requestGET(url: String?): String {
        val obj = URL(url)
        val con = obj.openConnection() as HttpsURLConnection
        Log.d(TAG, "OK1")
        con.setRequestProperty("User-Agent", "Mozilla/5.0")
        Log.d(TAG, "OK2")
        con.setRequestProperty("Content-Type", "application/json")
        con.requestMethod = "GET"
        Log.d(TAG, "OK3")
        val responseCode = con.responseCode
        Log.d(TAG, "Response Code :: $responseCode")
        return if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            val `in` =
                BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuffer()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            response.toString()
        } else {
            ""
        }
    }
/*
    @Throws(IOException::class)
    private fun downloadUrl(url: URL): String? {
        var connection: HttpsURLConnection? = null
        try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                readTimeout = 3000
                connectTimeout = 3000
                requestMethod = "GET"
                doInput = true
                setRequestProperty("User-Agent", "Mozilla/5.0")
                setRequestProperty("Content-Type", "application/json")
                connect()

                if (ResponseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code : $responseCode")
                }

                val data = inputStream.bufferedReader().readText()

                Log.d(TAG, "data --> " + data)

            }
        } finally {
            connection?.inputStream?.close()
            connection?.disconnect()
        }
        return 'ok'
    } */


}