package com.example.registroseries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.registroseries.databinding.FragmentDashboardBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.recyclerView.CarruselSeriesAdaptador
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

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBarLoading
        val progressBar2 = binding.progressBarLoading2


        progressBar.visibility = View.VISIBLE // Mostrar spinner al iniciar
        progressBar2.visibility = View.VISIBLE


        viewPager = view.findViewById(R.id.viewPagerUltimasSeries)

        (activity as MainActivity).serieViewModel.listaSeries.observe(viewLifecycleOwner) { lista ->
            progressBar.visibility = View.GONE  // Ocultar spinner cuando hay datos
            progressBar2.visibility = View.GONE


            val listaOrdenada = lista.sortedByDescending { it.fechaCreacion }

            adapter = CarruselSeriesAdaptador(listaOrdenada) { serieClicked ->
                Toast.makeText(requireContext(), "Serie: ${serieClicked.titulo}", Toast.LENGTH_SHORT).show()
                val bundle = Bundle().apply {
                    putString("titulo", serieClicked.titulo)
                }
                findNavController().navigate(R.id.action_dashboardFragment_to_serieDetailFragment, bundle)
            }

            viewPager.adapter = adapter

            val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutDots)
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()


            inicializarDatosEstadisticas(lista)

        }

        viewPager.setPadding(60, 0, 60, 0)
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3

        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false

        binding.dfbVerListaSeries.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_seriesListFragment)
        }

        binding.btnAddSerie.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_serieCreateFragment)
        }



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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}