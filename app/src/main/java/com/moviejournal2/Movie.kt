package com.moviejournal2

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("release_date") val releaseDate: String
    )

