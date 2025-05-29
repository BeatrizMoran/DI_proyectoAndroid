package com.example.registroseries.recyclerView

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import com.example.registroseries.R
import com.example.registroseries.databinding.ItemSerieBinding
import com.example.registroseries.databinding.ItemSerieGridBinding
import com.example.registroseries.modelo.Serie

class Adaptador(private val isGridView: Boolean = false) :
    ListAdapter<Serie, Adaptador.SerieVH>(ComparadorSeries()) {

    inner class SerieVH(private val binding: Any) : RecyclerView.ViewHolder(
        when(binding) {
            is ItemSerieBinding -> binding.root
            is ItemSerieGridBinding -> binding.root
            else -> throw IllegalArgumentException("Binding no soportado")
        }
    ) {
        fun bind(serie: Serie) {
            when (binding) {
                is ItemSerieBinding -> {
                    binding.tvisTituloSerie.text = serie.titulo
                    binding.tvisCategoriaSerie.text = if (!serie.genero.isNullOrBlank()) serie.genero else "Sin especificar género"
                    binding.tvisEstadoSerie.text = serie.estadoVisualizacion
                    binding.tvPuntuacion.text = if (serie.puntuacion != null) serie.puntuacion.toString() else "Sin calificación"

                    serie.imagenUrl?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        binding.imageView.setImageBitmap(bitmap)
                    }

                    binding.isbVerInfo.setOnClickListener {
                        val bundle = Bundle().apply { putInt("id", serie.id) }
                        it.findNavController().navigate(R.id.action_seriesListFragment_to_serieDetailFragment, bundle)
                    }
                }
                is ItemSerieGridBinding -> {
                    binding.tvisTituloSerie.text = serie.titulo
                    binding.tvisCategoriaSerie.text = if (!serie.genero.isNullOrBlank()) serie.genero else "Sin especificar género"
                    binding.tvisEstadoSerie.text = serie.estadoVisualizacion
                    binding.tvPuntuacion.text = if (serie.puntuacion != null) serie.puntuacion.toString() else "Sin calificación"

                    serie.imagenUrl?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        binding.imageView.setImageBitmap(bitmap)
                    }

                    binding.isbVerInfo.setOnClickListener {
                        val bundle = Bundle().apply { putInt("id", serie.id) }
                        it.findNavController().navigate(R.id.action_seriesListFragment_to_serieDetailFragment, bundle)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (isGridView) {
            ItemSerieGridBinding.inflate(inflater, parent, false)
        } else {
            ItemSerieBinding.inflate(inflater, parent, false)
        }
        return SerieVH(binding)
    }

    override fun onBindViewHolder(holder: SerieVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class ComparadorSeries : DiffUtil.ItemCallback<Serie>() {
    override fun areItemsTheSame(oldItem: Serie, newItem: Serie): Boolean {
        return oldItem.id == newItem.id // mejor comparar por id único
    }

    override fun areContentsTheSame(oldItem: Serie, newItem: Serie): Boolean {
        return oldItem == newItem
    }
}
