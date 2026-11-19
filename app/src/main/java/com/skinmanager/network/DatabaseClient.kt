package com.skinmanager.network

import com.skinmanager.models.Skin
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DatabaseClient {

    /**
     * Downloads and parses a Sjector manifest without blocking the main thread.
     * Expected JSON: { "version": 1, "skins": [{ "heroName": "...", "skinName": "..." }] }
     */
    fun fetchSkins(manifestUrl: String, callback: (Result<List<Skin>>) -> Unit) {
        Thread {
            var connection: HttpURLConnection? = null

            try {
                require(manifestUrl.isNotBlank()) { "Database URL has not been configured yet." }

                connection = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "Sjector-Android")
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    throw IllegalStateException("Database request failed (HTTP $responseCode).")
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(response)
                val skinArray = root.optJSONArray("skins")
                    ?: throw IllegalArgumentException("Manifest is missing the 'skins' array.")

                val skins = mutableListOf<Skin>()
                for (index in 0 until skinArray.length()) {
                    val item = skinArray.optJSONObject(index) ?: continue
                    val heroName = item.optString("heroName").trim()
                    val skinName = item.optString("skinName").trim()

                    if (heroName.isNotEmpty() && skinName.isNotEmpty()) {
                        skins.add(Skin(heroName, skinName))
                    }
                }

                callback(Result.success(skins))
            } catch (error: Exception) {
                callback(Result.failure(error))
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
