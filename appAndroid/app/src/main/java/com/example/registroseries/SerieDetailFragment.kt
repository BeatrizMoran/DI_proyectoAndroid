package com.example.registroseries

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.registroseries.databinding.FragmentSerieDetailBinding


class SerieDetailFragment : Fragment() {
    private var _binding: FragmentSerieDetailBinding? = null
    private var titulo: String? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSerieDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            titulo = it.getString("titulo")
        }

        val series = (activity as MainActivity).series

        val serieFiltrada = series.find { serie ->
            serie.titulo.equals(titulo, ignoreCase = true)

        }

        if (serieFiltrada != null) {
            binding.sdftvTituloSerie.text = serieFiltrada.titulo.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}