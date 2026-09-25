package com.flatcode.littlemovieadmin.ui.editorschoice

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.dialogOptionDelete
import com.flatcode.littlemovieadmin.utils.openActivity
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
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = EditorsChoiceAdapter(onAddClick = { model ->
            openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(
                    DATA.EDITORS_CHOICE_ID to model.id.toString(), DATA.OLD_ID to null
                )
            )
        }, onChangeClick = { model, movieId ->
            openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(
                    DATA.EDITORS_CHOICE_ID to model.id.toString(), DATA.OLD_ID to movieId
                )
            )
        }, onDeleteClick = { movieId, movieName ->
            dialogOptionDelete(
                movieId,
                movieName,
                DATA.EDITORS_CHOICE,
                DATA.EDITORS_CHOICE,
                isEditorsChoice = true,
                db = DATA.NULL,
                idDB = DATA.NULL,
                childDB = DATA.NULL,
                cast = false,
                movie = false,
            )
        })
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
