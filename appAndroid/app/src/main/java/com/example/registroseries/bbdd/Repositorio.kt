package com.example.registroseries.bbdd

import androidx.annotation.WorkerThread
import com.example.registroseries.modelo.Serie
import kotlinx.coroutines.flow.Flow

class Repositorio(val miDAO: SerieDAO){
    fun mostrarSeries(): Flow<List<Serie>>{
        return miDAO.mostrarSeries()
    }

    @WorkerThread
    suspend fun insertarSerie(miSerie: Serie){
        miDAO.insertarSerie(miSerie)
    }

    suspend fun actualizarSeri(miSerie: Serie){
        miDAO.actualizarSerie(miSerie)
    }

    suspend fun borrarSerie(miSerie: Serie){
        miDAO.borrarSerie(miSerie)
    }
}