package com.example.registroseries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registroseries.databinding.FragmentFirstBinding
import com.example.registroseries.databinding.FragmentSeriesListBinding
import com.example.registroseries.recyclerView.Adaptador


class SeriesListFragment : Fragment() {
    private var _binding: FragmentSeriesListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSeriesListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tfrvSeries.layoutManager=LinearLayoutManager(activity as MainActivity)
        binding.tfrvSeries.adapter= Adaptador((activity as MainActivity).series)    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}