package dev.ajithgoveas.transliterator.translit

import jakarta.inject.Inject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AksharamukhaClient @Inject constructor() {

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Transliterate text from source script to target script using API.
     *
     * @param source Source script name (e.g., "Devanagari")
     * @param target Target script name (e.g., "Kannada")
     * @param text Text to transliterate
     * @param callback Callback with the transliterated result or error message
     */
    fun transliterate(source: String, target: String, text: String, callback: (String) -> Unit) {
        // Build JSON body
        val requestBody = JSONObject().apply {
            put("source", source)
            put("target", target)
            put("text", text)
        }.toString().toRequestBody(jsonMediaType)

        // Build POST request
        val request = Request.Builder()
            .url("https://transliterator.onrender.com/transliterate")
            .post(requestBody)
            .build()

        // Execute request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message ?: "Unknown error"}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body.string()
                    if (!response.isSuccessful || responseBody.isEmpty()) {
                        callback("Error: ${response.code}")
                        return
                    }

                    val result = JSONObject(responseBody).optString("result", "No result")
                    callback(result)
                } catch (e: Exception) {
                    callback("Error parsing response: ${e.message}")
                } finally {
                    response.close()
                }
            }
        })
    }
}