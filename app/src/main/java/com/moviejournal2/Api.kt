package com.moviejournal2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {

    //Popular movies endpoint
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Top rated movies endpoint
    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Top rated movies endpoint
    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Requested movie (search) endpoint
    @GET("search/movie")
    fun getRequestedMovie(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("query") name: String,
        @Query("page") page: Int
    ): Call<GetMoviesResponse>
}




