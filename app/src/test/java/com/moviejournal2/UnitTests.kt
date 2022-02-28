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

//    val movieList: MutableList<Movie> = ArrayList()
//    var ma: MoviesAdapter = MoviesAdapter(
//        movieList,
//        ::temp
//    )

    // Movie object
    var movie: Movie = Movie(
        123,"TestTitle","TestOverview","TestPath",
        "TestBackdrop", 0.5, "2021/05/30")

    // Temp function used to initialize the MoviesAdapter
    fun temp(m:Movie): Unit {
        Log.d("test", "test")
    }

    @Test
    fun initMoviesAdapter() {

        // Create array
//        val movieList: MutableList<Movie> = ArrayList()
//        movieList.add(0, movie)

        val movieList: MutableList<Movie> = ArrayList()
        var ma: MoviesAdapter = MoviesAdapter(
            movieList,
            ::temp
        )

        // No movies should be inside the MoviesAdapter
        Assertions.assertThat(ma.itemCount).isEqualTo(0)
    }

    @Test
    fun appendMovie() {

        val movieList: MutableList<Movie> = ArrayList()
        var ma: MoviesAdapter = MoviesAdapter(
            movieList,
            ::temp
        )

        val list: MutableList<Movie> = ArrayList()
        list.add(0, movie)

        ma.appendMovies(list)

        Assertions.assertThat(ma.itemCount).isEqualTo(1)
    }
}

//@RunWith(JUnit4::class)
//class UnitTestLikedListAdapter {
//
//    val list: MutableList<LikedList> = ArrayList()
//    var la: LikedListAdapter = LikedListAdapter(
//        list,
//        ::temp
//    )
//
//    // LikedList object
//    var item: LikedList = LikedList(
//        123,"TestTitle","TestOverview","TestPath",
//        "TestBackdrop", 0.5.toFloat(), "2021/05/30")
//
//    // Temp function used to initialize the MoviesAdapter
//    fun temp(l:LikedList): Unit {
//        Log.d("test", "test")
//    }
//
//    @Test
//    fun initAdapter() {
//
//        // Create array
////        val movieList: MutableList<Movie> = ArrayList()
////        movieList.add(0, movie)
//
//        // No movies should be inside the MoviesAdapter
//        Assertions.assertThat(la.itemCount).isEqualTo(0)
//    }
//
//    @Test
//    fun appendMovie() {
//
//        val list: MutableList<LikedList> = ArrayList()
//        list.add(0, item)
//
//        la.updateItems(list)
//
//        Assertions.assertThat(la.itemCount).isEqualTo(1)
//    }
//}