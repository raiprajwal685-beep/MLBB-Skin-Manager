package com.skinmanager

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skinmanager.adapters.SkinAdapter
import com.skinmanager.network.DatabaseClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var statusText: TextView
    private lateinit var retryButton: Button
    private lateinit var adapter: SkinAdapter
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.database_status)
        retryButton = findViewById(R.id.retry_button)

        val list = findViewById<RecyclerView>(R.id.skin_list)
        list.layoutManager = LinearLayoutManager(this)
        adapter = SkinAdapter()
        list.adapter = adapter

        retryButton.setOnClickListener { loadDatabase() }
        loadDatabase()
    }

    private fun loadDatabase() {
        statusText.text = "Loading skin catalogue..."
        retryButton.visibility = View.GONE

        executor.execute {
            try {
                val skins = DatabaseClient.fetchSkins()
                mainHandler.post {
                    if (isFinishing || isDestroyed) return@post
                    adapter.submitSkins(skins)
                    if (skins.isEmpty()) {
                        statusText.text = "The database is connected, but no skins have been added yet."
                        retryButton.text = "Refresh"
                        retryButton.visibility = View.VISIBLE
                    } else {
                        statusText.text = "${skins.size} skin(s) loaded."
                        retryButton.text = "Refresh"
                        retryButton.visibility = View.VISIBLE
                    }
                }
            } catch (error: Exception) {
                mainHandler.post {
                    if (isFinishing || isDestroyed) return@post
                    statusText.text = error.message ?: "Unable to load the skin catalogue."
                    retryButton.text = "Retry"
                    retryButton.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
