package com.ud.riddle.Service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class GeminiService {

    private val client = OkHttpClient()

    val categorias = listOf("animals", "sports", "countries", "birds", "companies")

    suspend fun generateSecretWord(categoria: String): String = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("WORD_DEBUG", "Iniciando petición con categoria: $categoria")

            val request = Request.Builder()
                .url("https://random-words-api.kushcreates.com/api?language=es&category=$categoria&words=1&type=lowercase")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""
            android.util.Log.d("WORD_RAW", responseString)

            val json = JSONArray(responseString)
            json.getJSONObject(0).getString("word").trim().lowercase()

        } catch (e: Exception) {
            android.util.Log.e("WORD_ERROR", "Causa: ${e.message}")
            "cactus"
        }
    }
}