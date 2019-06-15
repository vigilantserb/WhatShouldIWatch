package com.stameni.com.whatshouldiwatch.data.networkData

import androidx.lifecycle.LiveData
import com.stameni.com.whatshouldiwatch.data.models.Movie
import io.reactivex.disposables.Disposable

interface FetchMoviesByGenreUseCase {

    val fetchedMovies: LiveData<ArrayList<Movie>>

    val fetchError: LiveData<Exception>

    val totalPages: LiveData<Int>

    fun getMoviesWithGenre(genreId: Int, page: Int): Disposable
}