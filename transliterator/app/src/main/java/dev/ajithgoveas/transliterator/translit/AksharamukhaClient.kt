package dev.ajithgoveas.transliterator.translit

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

object AksharamukhaClient {
    fun transliterate(source: String, target: String, text: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("source", source)
            put("target", target)
            put("text", text)
        }

////        Web API
//        val web_request = Request.Builder()
//            .url("http://aksharamukha-plugin.appspot.com/api/public?source=${source}&target=${target}&text=${text}")
//            .build()

//        Custom API
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("http://localhost:5000/transliterate")
            .post(body)
            .build()

        client.newCall(request = request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val result = JSONObject(response.body?.string() ?: "").getString("result")
                    callback(result)
                } else {
                    callback("Error: ${response.code}")
                }
            }
        })
    }
}