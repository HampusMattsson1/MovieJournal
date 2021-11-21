package com.moviejournal2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>
}


