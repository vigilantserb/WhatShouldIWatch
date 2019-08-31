package com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stameni.com.whatshouldiwatch.data.models.movie.Movie
import com.stameni.com.whatshouldiwatch.data.retrofit.MovieApi
import com.stameni.com.whatshouldiwatch.data.retrofit.schemas.movie.SearchSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class FetchUpcomingMoviesImpl(
    private val movieApi: MovieApi
) : FetchUpcomingMovies {

    private val _fetchedMovies = MutableLiveData<ArrayList<Movie>>()

    override val fetchedMovies: LiveData<ArrayList<Movie>>
        get() = _fetchedMovies

    private val _totalPages = MutableLiveData<Int>()

    override val totalPages: LiveData<Int>
        get() = _totalPages

    private val _fetchError = MutableLiveData<String>()

    override val fetchError: LiveData<String>
        get() = _fetchError

    override fun getUpcomingMovies(page: Int): Disposable {
        return movieApi.getUpcomingMovies(page)
            .subscribeOn(Schedulers.io())
            .map {
                getTotalpages(it)
            }
            .map {
                formatResponseData(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onGenreMoviesFetch(it) }, { onGenreMovieFetchFail(it) })

    }

    private fun getTotalpages(response: Response<SearchSchema>): Response<SearchSchema> {
        if (response.body() != null)
            _totalPages.postValue(response.body()!!.totalPages)
        return response
    }

    private fun onGenreMovieFetchFail(exception: Throwable) {
        _fetchError.value = exception.localizedMessage
    }

    private fun onGenreMoviesFetch(movies: ArrayList<Movie>) {
        _fetchedMovies.value = movies
    }

    private fun formatResponseData(response: Response<SearchSchema>): ArrayList<Movie> {
        val movieData = ArrayList<Movie>()

        if (response.body() != null) {
            response.body()!!.results.forEach {
                if (it.posterPath != null)
                    movieData.add(
                        Movie(
                            it.id,
                            it.title,
                            "",
                            "",
                            it.posterPath,
                            0.0
                        )
                    )
            }
        }

        return movieData
    }
}