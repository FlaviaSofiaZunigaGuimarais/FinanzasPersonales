package com.app.finanzaspersonales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovimientoAdapter(
    private val lista: List<Movimiento>,
    private val onItemClick: (Movimiento) -> Unit
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    inner class MovimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipoItem)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMontoItem)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaItem)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)
        return MovimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        val movimiento = lista[position]
        holder.tvTipo.text = movimiento.tipo
        holder.tvMonto.text = "$${movimiento.monto}"
        holder.tvCategoria.text = movimiento.categoria
        holder.tvFecha.text = movimiento.fecha
        holder.itemView.setOnClickListener { onItemClick(movimiento) }
    }

    override fun getItemCount() = lista.size
}