package com.example.registroseries

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registroseries.databinding.FragmentFirstBinding
import com.example.registroseries.databinding.FragmentSeriesListBinding
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.recyclerView.Adaptador


class SeriesListFragment : Fragment() {
    private var _binding: FragmentSeriesListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var estadoVisualizacionFiltro: String = "Todas"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSeriesListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mostrarSeries()


        binding.slfsFiltroSeries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacionFiltro = parent.getItemAtPosition(position).toString()
                mostrarSeries()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }




    }

    fun mostrarSeries(){
        val adaptador = Adaptador()
        binding.tfrvSeries.layoutManager = LinearLayoutManager(requireContext())
        binding.tfrvSeries.adapter = adaptador

        (activity as MainActivity).serieViewModel.listaSeries.observe(viewLifecycleOwner) { lista ->
            var listaSeries = lista.sortedByDescending { it.fechaCreacion }

            if (estadoVisualizacionFiltro != "Todas") {
                listaSeries = lista.filter { it.estadoVisualizacion == estadoVisualizacionFiltro }
            }


            adaptador.submitList(listaSeries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}