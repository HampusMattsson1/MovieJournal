package com.moviejournal2

import MoviesAdapter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class ValidatorTest {


    @Test
    fun testMoviesAdapter() {

        // Creating a movie object
        var movie: Movie = Movie(
            123,"TestTitle","TestOverview","TestPath",
            "TestBackdrop", 0.5, "2021/05/30")

        val movieList: MutableList<Movie> = ArrayList()

        val m = MoviesAdapter(movieList, movie)

//        Assertions.assertThat(test).isNotNull

    }


}