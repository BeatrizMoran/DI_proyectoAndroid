package com.example.registroseries

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var ordenSeries: String = "reciente"
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager

    var isGridView = false // inicialmente lista vertical


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentSeriesListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.tfrvSeries

        linearLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager = GridLayoutManager(requireContext(), 2)

        recyclerView.layoutManager = linearLayoutManager
        mostrarSeries()

        //Filtro series por estado visualizacion
        binding.slfsFiltroSeries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacionFiltro = parent.getItemAtPosition(position).toString()
                mostrarSeries()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        //Filtro orden series
        binding.slfsOrdenSeries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                ordenSeries = parent.getItemAtPosition(position).toString()
                mostrarSeries()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }




    }

    fun mostrarSeries(){
        val adaptador = Adaptador(isGridView)
        recyclerView.layoutManager = if (isGridView) gridLayoutManager else linearLayoutManager
        recyclerView.adapter = adaptador

        (activity as MainActivity).serieViewModel.listaSeries.observe(viewLifecycleOwner) { lista ->

            binding.cardSinSeries.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE

            // 1. Filtrar si es necesario
            val filtradas = if (estadoVisualizacionFiltro != "Todas") {
                lista.filter { it.estadoVisualizacion == estadoVisualizacionFiltro }
            } else {
                lista
            }

            // 2. Ordenar según selección
            val ordenadas = when (ordenSeries) {
                "Fecha de creación (más reciente)" -> filtradas.sortedByDescending { it.fechaCreacion }
                "Fecha de creación (más antigua)" -> filtradas.sortedBy { it.fechaCreacion }
                "Puntuación (mayor primero)" -> filtradas.sortedByDescending { it.puntuacion }
                "Puntuación (menor primero)" -> filtradas.sortedBy { it.puntuacion }
                else -> filtradas
            }

            // 3. Mostrar lista en el RecyclerView
            adaptador.submitList(ordenadas)
        }

    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // ocultar opciones del menu
        menu.findItem(R.id.action_cargar_datos_formulario)?.isVisible = false
        menu.findItem(R.id.action_limpiar_campos_formulario)?.isVisible = false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_cambiar_vista -> {
                toggleLayout()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    fun toggleLayout() {
        isGridView = !isGridView
        recyclerView.layoutManager = if (isGridView) gridLayoutManager else linearLayoutManager
        mostrarSeries()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}