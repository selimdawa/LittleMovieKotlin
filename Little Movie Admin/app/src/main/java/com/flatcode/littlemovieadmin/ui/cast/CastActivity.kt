package com.flatcode.littlemovieadmin.ui.cast

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityCastBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.moreDeleteCast
import com.flatcode.littlemovieadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class CastActivity : BaseActivity() {

    private lateinit var binding: ActivityCastBinding
    private val viewModel: CastViewModel by viewModels()
    private lateinit var adapter: CastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.cast)
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                    binding.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                } else if (DATA.isChange) {
                    onResume()
                    DATA.isChange = false
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Filter error")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        adapter = CastAdapter(onItemClick = { cast ->
            openActivity<CastDetailsActivity>(
                extras = arrayOf(
                    DATA.CAST_ID to cast.id,
                    DATA.CAST_NAME to cast.name,
                    DATA.CAST_IMAGE to cast.image,
                    DATA.CAST_ABOUT to cast.aboutMy
                )
            )
        }, onMoreClick = { cast ->
            moreDeleteCast(cast, DATA.NULL, DATA.NULL, DATA.NULL, cast = true, movie = false)
        })
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener { viewModel.getData(DATA.TIMESTAMP) }
        binding.switchBar.mostMovies.setOnClickListener { viewModel.getData(DATA.MOVIES_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.getData(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.getData(DATA.NAME) }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", state.count)

                    adapter.list = state.castList
                    adapter.submitList(state.castList)

                    if (state.castList.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyText.visibility = View.GONE
                    } else if (!state.isLoading) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData(viewModel.uiState.value.currentType)
    }
}
