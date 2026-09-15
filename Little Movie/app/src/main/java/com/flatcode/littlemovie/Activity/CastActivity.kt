package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Adapter.CastAdapter
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.ViewModel.CastViewModel
import com.flatcode.littlemovie.databinding.ActivityCastBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

class CastActivity : AppCompatActivity() {

    private var binding: ActivityCastBinding? = null
    var activity: Activity = this@CastActivity
    var list: ArrayList<Cast?>? = null
    var adapter: CastAdapter? = null
    var type: String? = null
    private val viewModel: CastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.cast)
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        type = DATA.TIMESTAMP

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }
        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering adapter")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        list = ArrayList()
        adapter = CastAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter

        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            viewModel.getData(type)
        }
        binding!!.switchBar.mostMovies.setOnClickListener {
            type = DATA.MOVIES_COUNT
            viewModel.getData(type)
        }
        binding!!.switchBar.mostInterested.setOnClickListener {
            type = DATA.INTERESTED_COUNT
            viewModel.getData(type)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            viewModel.getData(type)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.castList.collect { castItems ->
                list!!.clear()
                list!!.addAll(castItems)
                adapter!!.notifyDataSetChanged()
                if (list!!.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.castCount.collect { count ->
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", count)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onRestart() {
        viewModel.getData(type)
        super.onRestart()
    }

    override fun onResume() {
        viewModel.getData(type)
        super.onResume()
    }
}