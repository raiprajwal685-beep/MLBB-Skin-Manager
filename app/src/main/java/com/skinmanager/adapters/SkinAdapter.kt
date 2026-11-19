package com.skinmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skinmanager.R
import com.skinmanager.models.Skin

class SkinAdapter(private val skins: List<Skin>) : RecyclerView.Adapter<SkinAdapter.SkinViewHolder>() {
    class SkinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heroName: TextView = view.findViewById(R.id.hero_name)
        val skinName: TextView = view.findViewById(R.id.skin_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skin, parent, false)
        return SkinViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkinViewHolder, position: Int) {
        val skin = skins[position]
        holder.heroName.text = skin.heroName
        holder.skinName.text = skin.skinName
    }

    override fun getItemCount(): Int = skins.size
}
