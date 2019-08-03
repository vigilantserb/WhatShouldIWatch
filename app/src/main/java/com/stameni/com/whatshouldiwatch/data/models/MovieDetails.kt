package com.stameni.com.whatshouldiwatch.data.models

data class MovieDetails(
    val movieTitle: String?,
    val movieDescription: String?,
    val imdbRating: Double?,
    val tmdbRating: Double?,
    val releaseDate: String?,
    val runtime: Int?,
    val genres: String?,
    val directorName: String?,
    val directorImageUrl: String?
)