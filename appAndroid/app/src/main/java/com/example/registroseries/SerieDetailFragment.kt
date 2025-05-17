package com.example.registroseries

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.registroseries.databinding.FragmentSerieDetailBinding
import com.example.registroseries.modelo.SerieVM


class SerieDetailFragment : Fragment() {
    private var _binding: FragmentSerieDetailBinding? = null
    private val binding get() = _binding!!

    private var titulo: String? = null
    private lateinit var viewModel: SerieVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el título que pasaron en el bundle
        titulo = arguments?.getString("titulo")

        viewModel = (activity as MainActivity).serieViewModel

        // Observar la lista de series
        viewModel.listaSeries.observe(viewLifecycleOwner) { lista ->
            val serieFiltrada = lista.find {
                it.titulo.equals(titulo, ignoreCase = true)
            }

            serieFiltrada?.let { serie ->
                binding.sdftvTituloSerie.text = serie.titulo
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
