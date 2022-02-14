package com.moviejournal2

import MoviesAdapter
import android.util.Log
import org.assertj.core.api.Assertions
import org.junit.Assert.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class UnitTestMoviesAdapter {

    val movieList: MutableList<Movie> = ArrayList()
    var ma: MoviesAdapter = MoviesAdapter(
        movieList,
        ::temp
    )

    // Movie object
    var movie: Movie = Movie(
        123,"TestTitle","TestOverview","TestPath",
        "TestBackdrop", 0.5, "2021/05/30")

    fun temp(m:Movie): Unit {
        Log.d("test", "test")
    }

    @Test
    fun initMoviesAdapter() {

        // Create array
        val movieList: MutableList<Movie> = ArrayList()
//        movieList.add(0, movie)

        Assertions.assertThat(ma.itemCount).isEqualTo(0)
    }

    @Test
    fun appendMovie() {

        val list: MutableList<Movie> = ArrayList()
        list.add(0, movie)

        ma.appendMovies(list)

        Assertions.assertThat(ma.itemCount).isEqualTo(1)

    }


}