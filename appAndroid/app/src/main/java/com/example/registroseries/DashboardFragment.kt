package com.example.registroseries

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.registroseries.databinding.FragmentDashboardBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.recyclerView.CarruselSeriesAdaptador
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: CarruselSeriesAdaptador

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val progressBar2 = binding.progressBarLoading2

        progressBar2.visibility = View.VISIBLE

        val contenedorSeries = binding.contenedorSeries

        (activity as MainActivity).serieViewModel.listaSeries.observe(viewLifecycleOwner) { lista ->

            progressBar2.visibility = View.GONE

            val ultimas3Series = lista.sortedByDescending { it.fechaCreacion }.take(3)

            binding.dfSinSeries.visibility = if (ultimas3Series.isEmpty()) View.VISIBLE else View.GONE


            contenedorSeries.removeAllViews() // Limpiar por si acaso

            for (serie in ultimas3Series) {
                val itemView = layoutInflater.inflate(R.layout.item_serie_simple, contenedorSeries, false)

                val imagen = itemView.findViewById<ImageView>(R.id.imgSerie)
                val titulo = itemView.findViewById<TextView>(R.id.tvTituloSerie)

                titulo.text = serie.titulo
                if (serie.imagenUrl != null) {
                    val bitmap = BitmapFactory.decodeByteArray(serie.imagenUrl, 0, serie.imagenUrl!!.size)
                    imagen.setImageBitmap(bitmap)
                }

                itemView.setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("id", serie.id)
                    }
                    findNavController().navigate(R.id.action_dashboardFragment_to_serieDetailFragment, bundle)
                }

                contenedorSeries.addView(itemView)
            }

            inicializarDatosEstadisticas(lista)
        }

        // Botón para ir a lista completa
        binding.dfbVerListaSeries.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_seriesListFragment)
            (requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation))
                .selectedItemId = R.id.action_lista_series
        }


        // Botón para crear nueva serie
        binding.btnAddSerie.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_serieCreateFragment)
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
                .selectedItemId = R.id.action_crear_serie
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // ocultar opciones del menu
        menu.findItem(R.id.action_cargar_datos_formulario)?.isVisible = false
        menu.findItem(R.id.action_limpiar_campos_formulario)?.isVisible = false
        menu.findItem(R.id.action_cambiar_vista)?.isVisible = false
    }

    fun inicializarDatosEstadisticas(lista: List<Serie>){
        var numeroSeries = lista.size

        val seriesVistas = lista.count { it.estadoVisualizacion.equals("Viendo", ignoreCase = true) }
        val seriesPendientes = lista.count { it.estadoVisualizacion.equals("Pendiente", ignoreCase = true) }
        val seriesAbandonadas = lista.count { it.estadoVisualizacion.equals("Abandonadas", ignoreCase = true) }


        //estadisticas -> numero series
        binding.tvSeriesTotales.text = "Series totales: $numeroSeries"
        binding.tvSeriesPendientes.text = "Series pendientes: $seriesPendientes"
        binding.tvSeriesVistas.text = "Series vistas: $seriesVistas"
        binding.tvSeriesAbandonadas.text = "series abandonadas: $seriesAbandonadas"
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}