package com.flatcode.littlemovieadmin.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.Adapter.CastMovieAdapter
import com.flatcode.littlemovieadmin.Adapter.CommentAdapter
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Model.Comment
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.MovieDetailsViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityMovieDetailsBinding
import com.flatcode.littlemovieadmin.databinding.DialogCommentAddBinding
import com.flatcode.littlemovieadmin.Application
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MovieDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null
    
    private val listComment = mutableListOf<Comment?>()
    private val listCast = mutableListOf<Cast?>()
    private lateinit var adapterComment: CommentAdapter
    private lateinit var adapterCast: CastMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra(DATA.MOVIE_ID) ?: ""
        viewModel.setMovieId(movieId)

        binding.toolbar.nameSpace.setText(R.string.details_movie)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        
        VOID.nrLoves(binding.loves, movieId)
        binding.favorite.setOnClickListener { VOID.checkFavorite(binding.favorite, movieId) }

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.view.setOnClickListener {
            val movieLink = intent.getStringExtra(DATA.MOVIE_LINK)
            VOID.IntentExtra(this, CLASS.MOVIE_VIEW, DATA.MOVIE_LINK, movieLink)
        }

        binding.addComment.setOnClickListener {
            if (DATA.FIREBASE_USER == null) {
                Toast.makeText(this, "You're not logged in...", Toast.LENGTH_SHORT).show()
            } else {
                addCommentDialog()
            }
        }

        adapterCast = CastMovieAdapter(this, listCast as ArrayList<Cast?>)
        binding.recyclerCast.adapter = adapterCast
        
        adapterComment = CommentAdapter(this, listComment as ArrayList<Comment?>)
        binding.recyclerComment.adapter = adapterComment

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.movie?.let { movie ->
                        binding.title.text = movie.name
                        binding.description.text = movie.description
                        binding.views.text = movie.viewsCount.toString()
                        binding.date.text = Application.formatTimestamp(movie.timestamp)
                        binding.duration.text = VOID.convertDuration(movie.duration?.toLong() ?: 0L)
                        binding.year.text = movie.year.toString()
                        
                        VOID.loadCategory(movie.categoryId, binding.category)
                        VOID.GlideImage(false, this@MovieDetailsActivity, movie.image, binding.image)
                        VOID.GlideImage(false, this@MovieDetailsActivity, movie.image, binding.cover)
                        
                        VOID.isFavorite(binding.favorite, movie.id, DATA.FirebaseUserUid)
                    }

                    state.publisher?.let { user ->
                        binding.publisherName.text = user.username
                        VOID.GlideImage(true, this@MovieDetailsActivity, user.profileImage, binding.publisherImage)
                    }

                    listComment.clear()
                    listComment.addAll(state.comments)
                    adapterComment.notifyDataSetChanged()

                    listCast.clear()
                    listCast.addAll(state.castList)
                    adapterCast.notifyDataSetChanged()
                }
            }
        }
    }

    private fun addCommentDialog() {
        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentAddBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()
        
        commentAddBinding.back.setOnClickListener { alertDialog.dismiss() }
        commentAddBinding.submit.setOnClickListener {
            val comment = commentAddBinding.comment.text.toString().trim()
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(this, "Enter your comment...", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                viewModel.addComment(comment) { success, message ->
                    progressDialog?.dismiss()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                progressDialog?.setMessage("Adding comment...")
                progressDialog?.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}
