package com.stameni.com.whatshouldiwatch.data.networkData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stameni.com.whatshouldiwatch.data.MovieApi
import com.stameni.com.whatshouldiwatch.data.models.Movie
import com.stameni.com.whatshouldiwatch.data.schemas.SearchSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class FetchMoviesByGenreImpl(
    private val movieApi: MovieApi
) : FetchMoviesByGenreUseCase {

    private val _fetchError = MutableLiveData<Exception>()

    override val fetchError: LiveData<Exception>
        get() = _fetchError

    private val _fetchedMovies = MutableLiveData<ArrayList<Movie>>()

    override val fetchedMovies: LiveData<ArrayList<Movie>>
        get() = _fetchedMovies

    override fun getMoviesWithGenre(genreId: Int, page: Int): Disposable {
        return movieApi.getGenreMovies(genreId, page)
            .subscribeOn(Schedulers.io())
            .map {
                formatResponseData(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onGenreMoviesFetch(it) }, { onGenreMovieFetchFail(it) })
    }

    private fun formatResponseData(response: Response<SearchSchema>): ArrayList<Movie> {
        val movieData = ArrayList<Movie>()
        if(response.body() != null) {
            response.body()!!.results.forEach {
                movieData.add(Movie(it.title, "", "", it.posterPath, 0.0))
            }
        }
        return movieData
    }

    private fun onGenreMovieFetchFail(exception: Throwable) {
        _fetchError.value = exception as Exception
    }

    private fun onGenreMoviesFetch(response: ArrayList<Movie>) {
        _fetchedMovies.value = response
    }
}