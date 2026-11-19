package com.skinmanager

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skinmanager.adapters.SkinAdapter
import com.skinmanager.models.Skin

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.skin_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = SkinAdapter(
            listOf(
                Skin("Alucard", "Default"),
                Skin("Miya", "Default")
            )
        )
    }
}
