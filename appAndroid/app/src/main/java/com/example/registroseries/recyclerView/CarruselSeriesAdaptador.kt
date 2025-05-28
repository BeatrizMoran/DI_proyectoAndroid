package com.example.registroseries.recyclerView

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.registroseries.R
import com.example.registroseries.modelo.Serie

class CarruselSeriesAdaptador(
    private val seriesList: List<Serie>,
    private val onSerieClick: (Serie) -> Unit
) : RecyclerView.Adapter<CarruselSeriesAdaptador.SerieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_serie_simple, parent, false)
        return SerieViewHolder(view)
    }

    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        holder.bind(seriesList[position], onSerieClick)
    }

    override fun getItemCount(): Int = seriesList.size

    class SerieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgSerie: ImageView = itemView.findViewById(R.id.imgSerie)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloSerie)

        fun bind(serie: Serie, onClick: (Serie) -> Unit) {
            tvTitulo.text = serie.titulo
            if (serie.imagenUrl != null) {
                val bitmap = BitmapFactory.decodeByteArray(serie.imagenUrl, 0, serie.imagenUrl?.size ?: 0)
                imgSerie.setImageBitmap(bitmap)
            }
            itemView.setOnClickListener { onClick(serie) }
        }
    }
}

