package com.stameni.com.whatshouldiwatch.di.modules

import android.content.Context
import com.stameni.com.whatshouldiwatch.common.interceptors.ConnectivityInterceptor
import com.stameni.com.whatshouldiwatch.common.interceptors.ConnectivityInterceptorImpl
import com.stameni.com.whatshouldiwatch.data.API_KEY
import com.stameni.com.whatshouldiwatch.data.BASE_URL
import com.stameni.com.whatshouldiwatch.data.MovieApi
import com.stameni.com.whatshouldiwatch.data.networkData.lists.FetchGenreListUseCase
import com.stameni.com.whatshouldiwatch.data.networkData.lists.FetchGenreListUseCaseImpl
import com.stameni.com.whatshouldiwatch.data.networkData.lists.FetchListMoviesUseCase
import com.stameni.com.whatshouldiwatch.data.networkData.lists.FetchListMoviesUseCaseImpl
import com.stameni.com.whatshouldiwatch.data.networkData.movies.*
import com.stameni.com.whatshouldiwatch.data.networkData.search.SearchByTermUseCase
import com.stameni.com.whatshouldiwatch.data.networkData.search.SearchByTermUseCaseImpl
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    @Provides
    internal fun getConnectivityInterceptor(context: Context): ConnectivityInterceptor =
        ConnectivityInterceptorImpl(context)

    @Provides
    internal fun getMovieApi(retrofit: Retrofit, connectivityInterceptor: ConnectivityInterceptor): MovieApi {
        val requestInterceptor = Interceptor { chain ->
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()
            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
            return@Interceptor chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(requestInterceptor)
            .addInterceptor(connectivityInterceptor)
            .build()

        return retrofit.newBuilder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }

    @Provides
    fun getFetchGenresUseCase(movieApi: MovieApi): FetchGenreListUseCase {
        return FetchGenreListUseCaseImpl(movieApi)
    }

    @Provides
    fun getFetchListMoviesUseCase(movieApi: MovieApi): FetchListMoviesUseCase {
        return FetchListMoviesUseCaseImpl(movieApi)
    }

    @Provides
    fun getFetchMoviesByGenreUseCase(movieApi: MovieApi): FetchMoviesByGenreUseCase {
        return FetchMoviesByGenreImpl(movieApi)
    }

    @Provides
    fun getFetchUpcomingMoviesUseCase(movieApi: MovieApi): FetchUpcomingMovies =
        FetchUpcomingMoviesImpl(movieApi)

    @Provides
    fun getFetchNowPlayingMoviesUseCase(movieApi: MovieApi): FetchNowPlayingMovies =
        FetchNowPlayingMoviesImpl(movieApi)

    @Provides
    fun getSearchByTermUseCase(movieApi: MovieApi): SearchByTermUseCase = SearchByTermUseCaseImpl(movieApi)
}
