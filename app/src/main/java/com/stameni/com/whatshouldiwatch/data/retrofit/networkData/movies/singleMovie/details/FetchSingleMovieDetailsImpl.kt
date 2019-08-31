package com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stameni.com.whatshouldiwatch.data.models.movie.Actor
import com.stameni.com.whatshouldiwatch.data.models.movie.Movie
import com.stameni.com.whatshouldiwatch.data.models.movie.MovieDetails
import com.stameni.com.whatshouldiwatch.data.models.movie.MovieImage
import com.stameni.com.whatshouldiwatch.data.retrofit.MovieApi
import com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.cast.FetchSingleMovieActors
import com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.certification.FetchSingleMovieCertification
import com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.images.FetchSingleMovieImages
import com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.recommendations.FetchSingleMovieRecommendations
import com.stameni.com.whatshouldiwatch.data.retrofit.networkData.movies.singleMovie.trailer.FetchSingleMovieTrailer
import com.stameni.com.whatshouldiwatch.data.retrofit.schemas.movie.details.SingleMovieDetailsSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class FetchSingleMovieDetailsImpl(
    private val movieApi: MovieApi,
    private val fetchSingleMovieImages: FetchSingleMovieImages,
    private val fetchSingleMovieActors: FetchSingleMovieActors,
    private val fetchSingleMovieRecommendations: FetchSingleMovieRecommendations,
    private val fetchSingleMovieTrailer: FetchSingleMovieTrailer,
    private val fetchSingleMovieCertification: FetchSingleMovieCertification
) : FetchSingleMovieDetails {

    private val _fetchError = MutableLiveData<String>()
    override val fetchError: LiveData<String>
        get() = _fetchError

    private val _movieDetails = MutableLiveData<MovieDetails>()
    override val movieDetails: LiveData<MovieDetails>
        get() = _movieDetails

    private val _fetchedImages = MutableLiveData<ArrayList<MovieImage>>()

    override val fetchedImages: LiveData<ArrayList<MovieImage>>
        get() = _fetchedImages

    private val _fetchedActors = MutableLiveData<ArrayList<Actor>>()

    override val fetchedActors: LiveData<ArrayList<Actor>>
        get() = _fetchedActors

    private val _fetchedRecommendations = MutableLiveData<ArrayList<Movie>>()
    override val fetchedRecommendations: LiveData<ArrayList<Movie>>
        get() = _fetchedRecommendations

    private val _fetchedTrailerUrl = MutableLiveData<String>()
    override val fetchedTrailerUrl: LiveData<String>
        get() = _fetchedTrailerUrl

    private val _fetchedCertification = MutableLiveData<String>()
    override val fetchedCertification: LiveData<String>
        get() = _fetchedCertification

    override fun getSingleMovieDetails(movieId: Int): Disposable {
        return movieApi.getSingleMovieDetails(movieId)
            .subscribeOn(Schedulers.io())
            .map {
                formatResponse(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onDetailsFetched(it) }, { onDetailsFetchFailed(it) })
    }

    private fun onDetailsFetchFailed(it: Throwable) {
        _fetchError.value = "An error occurred while fetching single movie data - ${it.localizedMessage}"
    }

    private fun onDetailsFetched(respone: MovieDetails?) {
        _movieDetails.value = respone
    }

    private fun formatResponse(it: Response<SingleMovieDetailsSchema>): MovieDetails? {
        if (it.isSuccessful) {
            val data = it.body()!!
            if(data.images != null){
                _fetchedImages.postValue(fetchSingleMovieImages.formatMovieImagesDataFromResponse(data.images))
            }
            if(data.credits.cast != null){
                _fetchedActors.postValue(fetchSingleMovieActors.formatSingleMovieActorsResponse(data.credits.cast))
            }
            if(data.recommendations != null){
                _fetchedRecommendations.postValue(fetchSingleMovieRecommendations.formatSingleMovieRecommendationsData(data.recommendations.results))
            }
            if(data.videos.results != null){
                _fetchedTrailerUrl.postValue(fetchSingleMovieTrailer.getTrailerLink(data.videos.results))
            }
            if(data.releaseDates.results != null){
                _fetchedCertification.postValue(fetchSingleMovieCertification.onCertificationFetched(data.releaseDates.results))
            }
            val genreString = ArrayList<String>()
            data.genres.forEach { genreString.add(it.name) }
            val genres = genreString.joinToString(", ")
            var directorName = ""
            var directorImageUrl = ""
            var directorId = 0
            data.credits.crew.forEach {
                if (it.job == "Director") {
                    directorName = it.name
                    directorImageUrl = it.profilePath
                    directorId = it.id
                }
            }
            return MovieDetails(
                data.id,
                data.title,
                data.posterPath,
                data.overview,
                data.imdbId,
                data.voteAverage,
                data.releaseDate,
                data.runtime,
                genres,
                directorName,
                directorImageUrl,
                directorId
            )
        }
        return null
    }

}