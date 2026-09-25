package com.flatcode.littlemovie.ui.cast

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityCastDetailsBinding
import com.flatcode.littlemovie.ui.movie.MovieAdapter
import com.flatcode.littlemovie.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.dialogAboutArtist
import com.flatcode.littlemovie.utils.loadImage
import com.flatcode.littlemovie.utils.loadImageBlur
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class CastDetailsActivity : BaseActivity() {

    private var binding: ActivityCastDetailsBinding? = null
    private val activity: Activity = this@CastDetailsActivity
    private val viewModel: CastDetailsViewModel by viewModels()

    private lateinit var adapter: MovieAdapter

    private var type: String = DATA.TIMESTAMP
    private var castId: String? = null
    private var castName: String? = null
    private var castImage: String? = null
    private var castAbout: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        castId = intent.getStringExtra(DATA.CAST_ID)
        castName = intent.getStringExtra(DATA.CAST_NAME)
        castImage = intent.getStringExtra(DATA.CAST_IMAGE)
        castAbout = intent.getStringExtra(DATA.CAST_ABOUT)

        setupUI()
        setupAdapter()
        observeViewModel()

        castId?.let { viewModel.checkInterest(it) }
    }

    private fun setupUI() {
        binding!!.image.loadImage(true, castImage)
        binding!!.imageBlur.loadImageBlur(true, castImage, 50)

        binding!!.toolbar.nameSpace.setText(R.string.cast_details)
        binding!!.name.text = castName
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding!!.toolbar.toolbar.visibility = View.VISIBLE
                    binding!!.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding!!.toolbar.textSearch.setText(DATA.EMPTY)
                } else if (DATA.isChange) {
                    loadData()
                    DATA.isChange = false
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        binding!!.add.setOnClickListener { castId?.let { viewModel.toggleInterest(it) } }

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering movies")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.go.setOnClickListener {
            activity.dialogAboutArtist(castImage, castName, castAbout)
        }

        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            loadData()
        }
        binding!!.switchBar.mostViews.setOnClickListener {
            type = DATA.VIEWS_COUNT
            loadData()
        }
        binding!!.switchBar.mostLoves.setOnClickListener {
            type = DATA.LOVES_COUNT
            loadData()
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            loadData()
        }
    }

    private fun setupAdapter() {
        adapter = MovieAdapter(true) { movie ->
            activity.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.movies.collect { movies ->
                adapter.setFullList(movies)

                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", movies.size)
                binding!!.progress.visibility = View.GONE
                if (movies.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isInterested.collect { isInterested ->
                if (isInterested) {
                    binding!!.add.setImageResource(R.drawable.ic_star_selected)
                } else {
                    binding!!.add.setImageResource(R.drawable.ic_star_unselected)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun loadData() {
        castId?.let { viewModel.loadMovies(it, type) }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
