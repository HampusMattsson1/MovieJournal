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
class UnitTestWatchListAdapter {

    // LikedList object
    var item: WatchList = WatchList(
        123,"TestTitle","TestOverview","TestPath",
        "TestBackdrop", 0.5.toFloat(), "2021/05/30", WatchListType.MovieType)

    // Temp function used to initialize the MoviesAdapter
    fun temp(l:WatchList): Unit {
        Log.d("test", "test")
    }

    @Test
    fun initAdapter() {

        // Create array
        val movieList: MutableList<WatchList> = ArrayList()
        movieList.add(0, item)

        var wa: WatchListAdapter = WatchListAdapter(
            movieList,
            ::temp
        )

        // No movies should be inside the MoviesAdapter
        Assertions.assertThat(wa.itemCount).isEqualTo(0)
    }

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
}