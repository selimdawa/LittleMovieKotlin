package com.flatcode.littlemovieadmin.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.Adapter.MainAdapter
import com.flatcode.littlemovieadmin.Model.Main
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.MainViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val list = mutableListOf<Main>()
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.image.setOnClickListener {
            VOID.IntentExtra(this, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        adapter = MainAdapter(this, list as ArrayList<Main>)
        binding.recyclerView.adapter = adapter

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    Timber.d("Observe State: isLoading=${state.isLoading}, items=${state.items.size}")
                    
                    binding.bar.visibility = if (state.isLoading && state.items.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (state.items.isNotEmpty()) View.VISIBLE else View.GONE
                    
                    state.userProfileImage?.let {
                        VOID.GlideImage(true, this@MainActivity, it, binding.toolbar.image)
                    }

                    list.clear()
                    list.addAll(state.items)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
