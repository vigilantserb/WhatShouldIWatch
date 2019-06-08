package com.stameni.com.whatshouldiwatch.data.networkData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stameni.com.whatshouldiwatch.data.MovieApi
import com.stameni.com.whatshouldiwatch.data.models.Genre
import com.stameni.com.whatshouldiwatch.data.schemas.GenreListSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject

class FetchGenreListUseCaseImpl @Inject constructor(
    private val movieApi: MovieApi
) : FetchGenreListUseCase {

    override val fetchError = MutableLiveData<Exception>()

    private val _genreListLiveData: MutableLiveData<List<Genre>> = MutableLiveData()

    override val genreListLiveData: LiveData<List<Genre>>
        get() = _genreListLiveData


    override fun getGenreList(): Disposable {
        return movieApi
            .getDbGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onGenreListFetch(it) }, { onGenreListFetchFail(it as Exception) }
            )
    }

    private fun onGenreListFetch(result: Response<GenreListSchema>) {
        val genreList = ArrayList<Genre>()
        val imageList = getGenreImages()

        if (result.body() != null) {
            if (result.code() == 200) {
                result.body()!!.genres.forEachIndexed { index, it ->
                    genreList.add(Genre(it.name, it.id, imageList[index]))
                }
                _genreListLiveData.value = genreList
            } else {
                fetchError.value = RuntimeException("Fetch failed, server code response: ${result.code()}")
            }
        } else {
            fetchError.value = RuntimeException("Fetch failed, no data fetched")
        }
    }

    private fun getGenreImages(): ArrayList<String> {
        var list = ArrayList<String>()
        list.add("/w2PMyoyLU22YvrGK3smVM9fW1jj.jpg")
        list.add("/v4yVTbbl8dE1UP2dWu5CLyaXOku.jpg")
        list.add("/lFwykSz3Ykj1Q3JXJURnGUTNf1o.jpg")
        list.add("/bi4jh0Kt0uuZGsGJoUUfqmbrjQg.jpg")
        list.add("/vVpEOvdxVBP2aV166j5Xlvb5Cdc.jpg")
        list.add("/6hfmHRaZNKzazyDDLli5CbT6L8H.jpg")
        list.add("/oAr5bgf49vxga9etWoQpAeRMvhp.jpg")
        list.add("/hziiv14OpD73u9gAak4XDDfBKa2.jpg")
        list.add("/5kYj5EOQMFBFCdnk4X8KaFUfDVR.jpg")
        list.add("/hjQp148VjWF4KjzhsD90OCMr11h.jpg")
        list.add("/AiG8iWpFtFSXAdhStWu6qBaqmv9.jpg")
        list.add("/6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg")
        list.add("/8ZNGBfGoN3uI5Akj5vEUDMxvmGO.jpg")
        list.add("/xqQztbT6KlPLQLlRtNHoXivEMZA.jpg")
        list.add("/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg")
        list.add("/zTyrr7jlOl9SC7vfA4uLq8n8j2N.jpg")
        list.add("/mFb0ygcue4ITixDkdr7wm1Tdarx.jpg")
        list.add("/aPEhtVLrZRnJufKHwbHgqwirv7J.jpg")
        list.add("/qUcmEqnzIwlwZxSyTf3WliSfAjJ.jpg")
        return list
    }

    private fun onGenreListFetchFail(exception: Exception) {
        fetchError.value = exception
    }
}