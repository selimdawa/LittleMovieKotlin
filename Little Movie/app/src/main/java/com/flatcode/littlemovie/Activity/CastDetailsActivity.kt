package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Adapter.MovieAdapter
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.CastDetailsViewModel
import com.flatcode.littlemovie.databinding.ActivityCastDetailsBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import dagger.hilt.android.AndroidEntryPoint
import java.text.MessageFormat

@AndroidEntryPoint
class CastDetailsActivity : AppCompatActivity() {

    private var binding: ActivityCastDetailsBinding? = null
    private val activity: Activity = this@CastDetailsActivity
    private val viewModel: CastDetailsViewModel by viewModels()
    
    private val list = ArrayList<Movie?>()
    private lateinit var adapter: MovieAdapter
    
    private var type: String = DATA.TIMESTAMP
    private var castId: String? = null
    private var castName: String? = null
    private var castImage: String? = null
    private var castAbout: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(left = systemBars.left, right = systemBars.right, bottom = systemBars.bottom)
            binding!!.toolbar.root.updatePadding(top = systemBars.top)
            insets
        }

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
        VOID.GlideImage(true, activity, castImage, binding!!.image)
        VOID.GlideBlur(true, activity, castImage, binding!!.imageBlur, 50)

        binding!!.toolbar.nameSpace.setText(R.string.cast_details)
        binding!!.name.text = castName
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        
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
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering movies")
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
        
        binding!!.go.setOnClickListener {
            VOID.dialogAboutArtist(activity, castImage, castName, castAbout)
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
        adapter = MovieAdapter(activity, list, true)
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.movies.collect { movies ->
                list.clear()
                list.addAll(movies)
                adapter.notifyDataSetChanged()
                
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", list.size)
                binding!!.progress.visibility = View.GONE
                if (list.isNotEmpty()) {
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

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            loadData()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
