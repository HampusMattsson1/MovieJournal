package com.moviejournal2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DAOMovie {
    @Query("SELECT * FROM table_movies")
    fun getAll(): List<MovieUnit>

    @Insert
    fun insert(movie: MovieUnit)

    @Query("SELECT * FROM table_movies WHERE id = :id LIMIT 1")
    fun findById(id: Long): MovieUnit?

    @Query("DELETE FROM table_movies WHERE id = :id")
    fun delete(id:Long)
}