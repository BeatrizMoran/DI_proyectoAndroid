package com.example.registroseries.recyclerView

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.registroseries.R
import com.example.registroseries.modelo.Serie

class CarruselSeriesAdaptador (
    private val seriesList: List<Serie>,
    private val onSerieClick: (Serie) -> Unit
    ) : RecyclerView.Adapter<CarruselSeriesAdaptador.UltimasSeriesViewHolder>() {

        // Cantidad de grupos (slides) de 3 series
        override fun getItemCount(): Int = Math.ceil(seriesList.size / 3.0).toInt()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UltimasSeriesViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrusel_3series, parent, false)
            return UltimasSeriesViewHolder(view)
        }

        override fun onBindViewHolder(holder: UltimasSeriesViewHolder, position: Int) {
            val startIndex = position * 3
            val subList = seriesList.subList(
                startIndex,
                Math.min(startIndex + 3, seriesList.size)
            )
            holder.bind(subList, onSerieClick)
        }

        class UltimasSeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val containers = listOf(
                itemView.findViewById<LinearLayout>(R.id.containerSerie1),
                itemView.findViewById<LinearLayout>(R.id.containerSerie2),
                itemView.findViewById<LinearLayout>(R.id.containerSerie3)
            )
            private val images = listOf(
                itemView.findViewById<ImageView>(R.id.imgSerie1),
                itemView.findViewById<ImageView>(R.id.imgSerie2),
                itemView.findViewById<ImageView>(R.id.imgSerie3)
            )
            private val titles = listOf(
                itemView.findViewById<TextView>(R.id.tvTituloSerie1),
                itemView.findViewById<TextView>(R.id.tvTituloSerie2),
                itemView.findViewById<TextView>(R.id.tvTituloSerie3)
            )


            fun bind(series: List<Serie>, onSerieClick: (Serie) -> Unit) {
                // Itera para 3 slots
                for (i in 0 until 3) {
                    if (i < series.size) {
                        val serie = series[i]
                        containers[i].visibility = View.VISIBLE
                        titles[i].text = serie.titulo

                        if (serie.imagenUrl != null) {
                            val bitmap = BitmapFactory.decodeByteArray(serie.imagenUrl, 0, serie.imagenUrl?.size ?: 0)
                            images[i].setImageBitmap(bitmap)
                        }
                        containers[i].setOnClickListener { onSerieClick(serie) }
                    } else {
                        containers[i].visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
