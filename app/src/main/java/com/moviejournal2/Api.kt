package com.moviejournal2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface Api {

    //Popular movies endpoint
    @GET("/3/movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Top rated movies endpoint
    @GET("/3/movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Top rated movies endpoint
    @GET("/3/movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Requested movie (search) endpoint
    @GET("/3/search/movie")
    fun getRequestedMovie(
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("query") name: String,
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    //Recommended movies
    @GET("/3/movie/{movie_id}/recommendations")
    fun getRecommendedMovies(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = "fb2a92edb783f7e99d17bc938877aacb",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>
}




