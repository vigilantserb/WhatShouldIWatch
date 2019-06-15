package com.stameni.com.whatshouldiwatch.screens.discover.genre.moviegridlist

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stameni.com.whatshouldiwatch.R
import com.stameni.com.whatshouldiwatch.common.CustomSnackbar
import com.stameni.com.whatshouldiwatch.common.ViewModelFactory
import com.stameni.com.whatshouldiwatch.common.baseClasses.BaseActivity
import kotlinx.android.synthetic.main.activity_movie_grid.*
import javax.inject.Inject

class MovieGridActivity : BaseActivity() {

    private val FIRST_PAGE = 1
    private var currentPage = 1

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MovieGridViewModel

    private var totalPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_grid)
        getControllerComponent().inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gridLayoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        val adapter = MovieGridAdapter(ArrayList())

        recycler_view.layoutManager = gridLayoutManager
        recycler_view.adapter = adapter

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieGridViewModel::class.java)


        if(intent.extras != null){
            val genreId = intent.extras!!.getInt("genreId", 0)
            val genreName = intent.extras!!.getString("genreName", "")

            supportActionBar!!.title = genreName

            var snackbar = CustomSnackbar.make(root_view)
            snackbar.duration = 1000

            viewModel.getListMovies(genreId, FIRST_PAGE)

            recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.itemCount - 1) {
                        if(currentPage <= totalPages){
                            currentPage++
                            viewModel.getListMovies(genreId, currentPage)
                            if (!snackbar.isShown) {
                                snackbar.show()
                            }
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })

            viewModel.totalPages.observe(this, Observer {
                if(it != null){
                    totalPages = it
                }
            })

            viewModel.fetchedMovies.observe(this, Observer {
                if (it != null) {
                    adapter.addAll(it)
                }
            })

            viewModel.fetchError.observe(this, Observer {
                Log.e("Error", Log.getStackTraceString(it))
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}