package com.stameni.com.whatshouldiwatch.screens.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stameni.com.whatshouldiwatch.data.models.Genre
import com.stameni.com.whatshouldiwatch.data.models.Movie
import com.stameni.com.whatshouldiwatch.data.networkData.FetchListMoviesUseCase
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception

class MovieListViewModel(
    private val fetchListMoviesUseCase: FetchListMoviesUseCase
): ViewModel() {


    val fetchedGenres: LiveData<List<Movie>>
        get() {
            return fetchListMoviesUseCase.fetchedMovies
        }

    val fetchError: LiveData<Exception>
        get() {
            return fetchListMoviesUseCase.fetchError
        }

    var disposable = CompositeDisposable()

    fun getListMovies(listId: String) {
        disposable.add(fetchListMoviesUseCase.getListMovies(listId))
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        disposable.clear()
    }
}