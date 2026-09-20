package com.flatcode.littlemovieadmin.ui.editorschoice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.ui.editorschoice.EditorsChoiceAdapter
import com.flatcode.littlemovieadmin.model.EditorsChoice
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.ui.editorschoice.EditorsChoiceViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorsChoiceActivity : BaseActivity() {

    private lateinit var binding: ActivityEditorsChoiceBinding
    private val viewModel: EditorsChoiceViewModel by viewModels()
    private lateinit var adapter: EditorsChoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.editors_choice)
        binding.toolbar.back.setOnClickListener { onBackPressed() }

        adapter = EditorsChoiceAdapter(this)
        binding.recyclerView.adapter = adapter

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.items)
                }
            }
        }
    }
}
