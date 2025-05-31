package com.example.registroseries.bbdd

import androidx.annotation.WorkerThread
import com.example.registroseries.modelo.Serie
import kotlinx.coroutines.flow.Flow

class Repositorio(val miDAO: SerieDAO){
    fun mostrarSeries(): Flow<List<Serie>>{
        return miDAO.mostrarSeries()
    }

    @WorkerThread
    suspend fun insertarSerie(miSerie: Serie): Boolean{
        val existe = miDAO.existeSerieConTitulo(miSerie.titulo)
        return if (existe == 0) {
            miDAO.insertarSerie(miSerie)
            true
        } else {
            false
        }
    }

    suspend fun actualizarSerie(miSerie: Serie):Boolean{
        val existe = miDAO.existeOtroConTitulo(miSerie.titulo, miSerie.id)
        return if (existe == 0){
            miDAO.actualizarSerie(miSerie)
            true
        }else{
            false
        }
    }

    suspend fun borrarSerie(miSerie: Serie){
        miDAO.borrarSerie(miSerie)
    }
    suspend fun borrarTodasLasSeries(){
        miDAO.borrarTodasLasSeries()
    }

}