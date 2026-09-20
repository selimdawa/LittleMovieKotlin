package com.flatcode.littlemovieadmin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.main.MainAdapter
import com.flatcode.littlemovieadmin.model.Main
import com.flatcode.littlemovieadmin.ui.profile.ProfileActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.ui.main.MainViewModel
import com.flatcode.littlemovieadmin.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.image.setOnClickListener {
            requireContext().openActivity<ProfileActivity>(
                extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid)
            )
        }

        adapter = MainAdapter(requireContext())
        binding.recyclerView.adapter = adapter

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    Timber.d("Observe State: isLoading=${state.isLoading}, items=${state.items.size}")
                    
                    if (isAdded) {
                        binding.bar.visibility = if (state.isLoading && state.items.isEmpty()) View.VISIBLE else View.GONE
                        binding.recyclerView.visibility = if (state.items.isNotEmpty()) View.VISIBLE else View.GONE
                        
                        state.userProfileImage?.let {
                            binding.toolbar.image.loadGlideImage(it, true)
                        }

                        adapter.submitList(state.items)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
