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
import com.example.registroseries.databinding.FragmentSerieDetailBinding
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

    // Supón que tienes un método para obtener las series ordenadas
    private fun obtenerUltimasSeries(): List<Serie> {
        // Devuelve series ordenadas por fecha de creación descendente
        return (activity as MainActivity).series.sortedByDescending { it.fecha_creacion }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPagerUltimasSeries)



        val ultimasSeries = obtenerUltimasSeries()

        adapter = CarruselSeriesAdaptador(ultimasSeries) { serieClicked ->
            // Acción cuando se toca una serie: abrir detalle, por ejemplo
            Toast.makeText(requireContext(), "Serie: ${serieClicked.titulo}", Toast.LENGTH_SHORT).show()
        }

        viewPager.adapter = adapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutDots)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // No necesitas configurar nada, solo sirve para mostrar dots
        }.attach()
        viewPager.setPadding(60, 0, 60, 0)
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3

        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false

        binding.dfbVerListaSeries.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFragment_to_seriesListFragment)
        }
        binding.btnAddSerie.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFragment_to_serieCreateFragment)
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}