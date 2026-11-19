package com.skinmanager.network

import com.skinmanager.models.Skin
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DatabaseClient {
    // Replace this with the raw manifest.json URL from the Sjector database repository.
    const val MANIFEST_URL = "https://raw.githubusercontent.com/raiprajwal685-beep/MLBB-Skin-Database/main/manifest.json"

    fun fetchSkins(): List<Skin> {
        require(MANIFEST_URL.isNotBlank()) {
            "Database URL is not configured yet."
        }

        val connection = (URL(MANIFEST_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                throw IllegalStateException("Database request failed (HTTP $responseCode).")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val manifest = JSONObject(body)
            val skinsJson = manifest.optJSONArray("skins")
                ?: throw IllegalStateException("Invalid database manifest: missing skins array.")

            return buildList {
                for (index in 0 until skinsJson.length()) {
                    val item = skinsJson.optJSONObject(index) ?: continue
                    val heroName = item.optString("heroName").trim()
                    val skinName = item.optString("skinName").trim()

                    if (heroName.isNotEmpty() && skinName.isNotEmpty()) {
                        add(Skin(heroName, skinName))
                    }
                }
            }
        } finally {
            connection.disconnect()
        }
    }
}
