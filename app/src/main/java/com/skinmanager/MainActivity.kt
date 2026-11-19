package com.skinmanager

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skinmanager.adapters.SkinAdapter
import com.skinmanager.network.DatabaseClient

class MainActivity : Activity() {

    // Set this after the separate Sjector database repository has been created.
    private val manifestUrl = ""

    private lateinit var skinAdapter: SkinAdapter
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.database_status)

        val list = findViewById<RecyclerView>(R.id.skin_list)
        list.layoutManager = LinearLayoutManager(this)
        skinAdapter = SkinAdapter()
        list.adapter = skinAdapter

        loadSkins()
    }

    private fun loadSkins() {
        if (manifestUrl.isBlank()) {
            statusText.text = "Database not connected yet. Add the manifest URL to connect."
            return
        }

        statusText.text = "Loading skin catalogue..."

        DatabaseClient.fetchSkins(manifestUrl) { result ->
            runOnUiThread {
                result.onSuccess { skins ->
                    skinAdapter.submitSkins(skins)
                    statusText.text = if (skins.isEmpty()) {
                        "The database is connected, but no skins have been added yet."
                    } else {
                        "${skins.size} skin(s) available"
                    }
                }.onFailure { error ->
                    statusText.text = "Couldn't load the catalogue: ${error.message ?: "Unknown error"}"
                }
            }
        }
    }
}
