package com.example.project_3_tcs_grupo4_dam.presentation.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.BrechaSkillDto

class BrechasAdapter(private var items: List<BrechaSkillDto>) : RecyclerView.Adapter<BrechasAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvBrechaSkill)
        val tvValue: TextView = view.findViewById(R.id.tvBrechaValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_brecha, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.nombreSkill
        holder.tvValue.text = "Brecha: ${item.brechaPromedio}"
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<BrechaSkillDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}

