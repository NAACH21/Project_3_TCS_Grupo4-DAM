package com.example.project_3_tcs_grupo4_dam.presentation.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.SkillDemandadoDto

class TopSkillsAdapter(private var items: List<SkillDemandadoDto>) : RecyclerView.Adapter<TopSkillsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvSkillName)
        val tvCount: TextView = view.findViewById(R.id.tvSkillCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skill, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.nombreSkill
        holder.tvCount.text = item.cantidadVacantes.toString()
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<SkillDemandadoDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}

