package com.example.registroseries.recyclerView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import com.example.registroseries.R
import com.example.registroseries.databinding.ItemSerieBinding
import com.example.registroseries.modelo.Serie

class Adaptador : ListAdapter<Serie, Adaptador.SerieVH>(ComparadorSeries()) {

    inner class SerieVH(val binding: ItemSerieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serie: Serie) {
            binding.tvisTituloSerie.text = serie.titulo
            binding.tvisCategoriaSerie.text = serie.genero
            binding.tvisEstadoSerie.text = serie.estadoVisualizacion

            binding.isbVerInfo.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("id", serie.id)
                }
                binding.rviClPrincipal.findNavController()
                    .navigate(R.id.action_seriesListFragment_to_serieDetailFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieVH {
        val binding = ItemSerieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SerieVH(binding)
    }

    override fun onBindViewHolder(holder: SerieVH, position: Int) {
        holder.bind(getItem(position))
    }
}

// Clase para comparar elementos de la lista y optimizar cambios
class ComparadorSeries : DiffUtil.ItemCallback<Serie>() {
    override fun areItemsTheSame(antigua: Serie, nueva: Serie): Boolean {
        return antigua.titulo == nueva.titulo
    }

    override fun areContentsTheSame(antigua: Serie, nueva: Serie): Boolean {
        return antigua == nueva
    }
}
