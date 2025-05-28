package com.example.registroseries.bbdd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.registroseries.modelo.Serie
import kotlinx.coroutines.flow.Flow

@Dao
interface SerieDAO {
    @Query("SELECT * FROM Series ORDER BY fechaCreacion")
    fun mostrarSeries(): Flow<List<Serie>>

    @Query("DELETE FROM series")
    suspend fun borrarTodasLasSeries()


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSerie(miSerie: Serie)

    @Update
    suspend fun actualizarSerie(miSerie: Serie)

    @Delete
    suspend fun borrarSerie(miSerie: Serie)

    //comprobar titulo serie existente
    @Query("SELECT COUNT(*) FROM Series WHERE LOWER(titulo) = LOWER(:titulo)")
    suspend fun existeSerieConTitulo(titulo: String): Int

    @Query("SELECT COUNT(*) FROM Series WHERE LOWER(titulo) = LOWER(:titulo) AND id != :id")
    suspend fun existeOtroConTitulo(titulo: String, id: Int): Int


}