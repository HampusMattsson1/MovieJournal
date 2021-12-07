package com.moviejournal2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieUnit::class], version = 1)

abstract class AppDB: RoomDatabase() {
    abstract fun daoMovie(): DAOMovie
}