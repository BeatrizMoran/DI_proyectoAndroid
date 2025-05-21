package com.example.registroseries

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.registroseries.databinding.FragmentSerieDetailBinding
import com.example.registroseries.modelo.SerieVM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SerieDetailFragment : Fragment() {
    private var _binding: FragmentSerieDetailBinding? = null
    private val binding get() = _binding!!

    private var id: Int? = null
    private lateinit var viewModel: SerieVM
    private var imagenSeleccionadaBytes: ByteArray? = null // almacena la imagen para la serie
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>



    private var accion: String = "ver"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setInputsEditable(parent: ViewGroup, editable: Boolean) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)

            when (view) {
                is EditText -> view.isEnabled = editable
                is Spinner -> view.isEnabled = editable
                is Switch -> view.isEnabled = editable
                is CheckBox -> view.isEnabled = editable
                is ImageView -> view.isEnabled = editable
                is ViewGroup -> setInputsEditable(view, editable) // recursivo si hay layouts anidados
            }
        }
    }
    private fun actualizarModo() {
        val editable = accion == "editar"
        setInputsEditable(binding.serieDetailLayout, editable)
        binding.sdfbCancelarEdicion.visibility = if (accion == "ver") View.GONE else View.VISIBLE
        binding.sdfbEditar.visibility = if (accion == "ver") View.VISIBLE else View.GONE
        binding.sdfbActualizar.visibility = if (accion == "ver") View.GONE else View.VISIBLE
        binding.sdfbCambiarImagen.visibility = if (accion == "ver") View.GONE else View.VISIBLE



    }

    fun formatoFecha(fecha: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(fecha)
    }
    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Registrar el launcher para seleccionar imagen desde la galería
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.sdfivImagenSerie.setImageURI(it)
                imagenSeleccionadaBytes = uriToByteArray(it)
            }
        }

        actualizarModo()

        // Obtener el id que pasaron en el bundle
        id = arguments?.getInt("id")

        viewModel = (activity as MainActivity).serieViewModel
        val spinner = binding.sdfspinnerEstadoVisualizacion

        //TODO HACE FALTA OBSERVAR LA LISTA DE SERIES??
        /*val listaSeries = viewModel.listaSeries.value

        val serieFiltrada = listaSeries?.find {
            it.id == id
        }*/

        // Observar la lista de series
        viewModel.listaSeries.observe(viewLifecycleOwner) { lista ->
            val serieFiltrada = lista.find {
                it.id == id
            }

            serieFiltrada?.let { serie ->
                binding.sdfinputTitulo.setText(serie.titulo)
                binding.sdftilGenero.setText(serie.genero)
                binding.inputTemporadaActual.setText(serie.temporadaActual.toString())
                binding.inputCapituloActual.setText(serie.captituloActual.toString())

            //ESTADO VISUALIZACION SERIE

                // 1. Obtener el adapter del spinner
                val adapter = spinner.adapter
                val estadoActual = serie.estadoVisualizacion


                // 2. Buscar la posición del valor que quieres seleccionar
                var posicion = 0
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i) == estadoActual) {
                        posicion = i
                        break
                    }
                }

                // 3. Seleccionar el ítem en el spinner
                spinner.setSelection(posicion)

            //SERIE EN EMISION

                binding.sdfswitchFinalizada.isChecked = serie.serieEnEmision ?: false
                //binding.sdfswitchFinalizada.text = if (serie.serieEnEmision ?: false) "Serie en emisión" else "Serie finalizada"

            //FECHA EMISION PROXIMA TEMPORADA
                binding.etFechaEmision.setText(
                    serie.fechaProximoEstreno?.let { formatoFecha(it) } ?: "No próxima fecha de emisión"
                )

            //NOTAS SERIE
                if (serie.notas.isNullOrBlank()) {
                    binding.sdfetNotas.hint = "No hay notas para esta serie"
                } else {
                    binding.sdfetNotas.setText(serie.notas)
                }

                // Mostrar imagen de la serie (si existe)
                serie.imagenUrl?.let { byteArray ->
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    binding.sdfivImagenSerie.setImageBitmap(bitmap)
                }



                // Botón para cambiar imagen
                binding.sdfbCambiarImagen.setOnClickListener {
                    imagePickerLauncher.launch("image/*")
                }

            }
        }

        binding.sdfbBorrar.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmacion")
            builder.setMessage("¿Estás seguro de que quieres borrar la serie?")
            builder.setPositiveButton("Aceptar") { dialog, _ ->

                dialog.dismiss()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
        binding.sdfbEditar.setOnClickListener {
            accion = "editar"
            actualizarModo()
        }
        binding.sdfbCancelarEdicion.setOnClickListener{
            accion = "ver"
            actualizarModo()
        }
        binding.sdfbActualizar.setOnClickListener{
            //ACTUALIZAR SERIE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
