package com.stameni.com.whatshouldiwatch.data.networkData.movies.singleMovie.recommendations

import androidx.lifecycle.LiveData
import com.stameni.com.whatshouldiwatch.data.models.Movie
import io.reactivex.disposables.Disposable

interface FetchSingleMovieRecommendations {

    val fetchedData: LiveData<ArrayList<Movie>>

    val fetchError: LiveData<Exception>

    fun singleMovieRecommendations(movieId: Int): Disposable
}