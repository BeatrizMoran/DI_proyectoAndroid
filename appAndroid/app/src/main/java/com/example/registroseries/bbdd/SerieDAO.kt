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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSerie(miSerie: Serie)

    @Update
    suspend fun actualizarSerie(miSerie: Serie)

    @Delete
    suspend fun borrarSerie(miSerie: Serie)
}