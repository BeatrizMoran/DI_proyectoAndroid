package com.example.registroseries.modelo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.registroseries.bbdd.Repositorio
import kotlinx.coroutines.launch

class SerieVM(private val miRepositorio: Repositorio): ViewModel(){
    val listaSeries: LiveData<List<Serie>> = miRepositorio.mostrarSeries().asLiveData()


    fun insertarSerie(miSerie: Serie, onResultado: (Boolean) -> Unit) = viewModelScope.launch {
        val resultado = miRepositorio.insertarSerie(miSerie)
        onResultado(resultado)
    }


    fun actualizarSerie(miSerie: Serie, onResultado: (Boolean) -> Unit)=viewModelScope.launch {
        val resultado = miRepositorio.actualizarSerie(miSerie)
        onResultado(resultado)
    }

    fun borrarSerie(miSerie: Serie)=viewModelScope.launch {
        miRepositorio.borrarSerie(miSerie)
    }

    fun obtenerSeriePorId(id: Int): Serie? {
        return listaSeries.value?.find { it.id == id }
    }


}


class SerieViewModelFactory(private val miRepositorio: Repositorio): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerieVM::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SerieVM(miRepositorio) as T
        }
        throw IllegalArgumentException("ViewModel class desconocida")
    }
}

