package com.example.registroseries.recyclerView


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.registroseries.R
import com.example.registroseries.databinding.ItemSerieBinding
import com.example.registroseries.modelo.Serie



class Adaptador(val series:MutableList<Serie>): RecyclerView.Adapter<Adaptador.SerieVH>(){
    //El ViewHolder es la clase de cada uno de los contenedores
    inner class SerieVH (val binding: ItemSerieBinding): RecyclerView.ViewHolder(binding.root){
        var posicion: Int = 0
        init {

            binding.isbVerInfo.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("titulo", series[posicion].titulo)
                }

                binding.rviClPrincipal.findNavController()
                    .navigate(R.id.action_seriesListFragment_to_serieDetailFragment, bundle)
            }

        }
    }
    //captura la vista que hemos creado (recyclerview_item) y crea una instancia del viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieVH {
        val binding = ItemSerieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SerieVH(binding)
    }
    //cargamos los datos en cada una de las instancias del ViewHolder
    override fun onBindViewHolder(holder: SerieVH, position: Int) {
        val serie = series[position]
        holder.binding.tvisTituloSerie.text = serie.titulo
        holder.binding.tvisCategoriaSerie.text = serie.genero
        holder.binding.tvisEstadoSerie.text = serie.estado_usuario

        holder.posicion = position



    }
    //retorna el número de elementos que vamos a querer que tenga el contenedor padre
    override fun getItemCount(): Int {
        return series.count()
    }
}