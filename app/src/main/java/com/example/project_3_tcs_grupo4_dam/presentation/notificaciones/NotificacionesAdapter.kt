package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.model.ColorPrioridad
import com.example.project_3_tcs_grupo4_dam.data.model.TipoOrigenAlerta

/**
 * Adapter para RecyclerView del Dashboard de Notificaciones
 */
class NotificacionesAdapter(
    private val onItemClick: (AlertaDashboard) -> Unit
) : ListAdapter<AlertaDashboard, NotificacionesAdapter.NotificacionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion_dashboard, parent, false)
        return NotificacionViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificacionViewHolder(
        itemView: View,
        private val onItemClick: (AlertaDashboard) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardContainer: CardView = itemView.findViewById(R.id.cardNotificacion)
        private val iconoTipo: ImageView = itemView.findViewById(R.id.iconoTipo)
        private val txtTitulo: TextView = itemView.findViewById(R.id.txtTitulo)
        private val txtMensaje: TextView = itemView.findViewById(R.id.txtMensaje)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val indicadorActiva: View = itemView.findViewById(R.id.indicadorActiva)

        fun bind(alerta: AlertaDashboard) {
            // Configurar textos
            txtTitulo.text = alerta.titulo
            txtMensaje.text = alerta.mensaje
            txtFecha.text = alerta.fecha

            // Configurar icono según tipo de origen
            val iconRes = when (alerta.tipoOrigen) {
                TipoOrigenAlerta.SKILL_GAP -> R.drawable.ic_trending_up
                TipoOrigenAlerta.CERTIFICACION -> R.drawable.ic_school
                TipoOrigenAlerta.VACANTE_DISPONIBLE -> R.drawable.ic_work
                TipoOrigenAlerta.GENERICA -> R.drawable.ic_notifications
            }
            iconoTipo.setImageResource(iconRes)

            // Configurar color de fondo según prioridad
            val backgroundColor = when (alerta.colorPrioridad) {
                ColorPrioridad.ROJO -> Color.parseColor("#FFEBEE")
                ColorPrioridad.AMARILLO -> Color.parseColor("#FFFDE7")
                ColorPrioridad.VERDE -> Color.parseColor("#E8F5E9")
            }
            cardContainer.setCardBackgroundColor(backgroundColor)

            // Mostrar/ocultar indicador de activa (no leída)
            indicadorActiva.visibility = if (alerta.activa) View.VISIBLE else View.GONE

            // Click listener
            itemView.setOnClickListener {
                onItemClick(alerta)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AlertaDashboard>() {
        override fun areItemsTheSame(oldItem: AlertaDashboard, newItem: AlertaDashboard): Boolean {
            return oldItem.idReferencia == newItem.idReferencia
        }

        override fun areContentsTheSame(oldItem: AlertaDashboard, newItem: AlertaDashboard): Boolean {
            return oldItem == newItem
        }
    }
}

