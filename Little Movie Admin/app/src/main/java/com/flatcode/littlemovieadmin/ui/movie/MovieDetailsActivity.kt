package com.flatcode.littlemovieadmin.ui.movie

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
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.ui.movie.CastMovieAdapter
import com.flatcode.littlemovieadmin.ui.movie.CommentAdapter
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.model.Comment
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.checkFavorite
import com.flatcode.littlemovieadmin.utils.convertDuration
import com.flatcode.littlemovieadmin.utils.isFavorite
import com.flatcode.littlemovieadmin.utils.loadCategory
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.utils.nrLoves
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieDetailsViewModel
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
        
        binding.loves.nrLoves(movieId)
        binding.favorite.setOnClickListener { binding.favorite.checkFavorite(movieId) }

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.view.setOnClickListener {
            val movieLink = intent.getStringExtra(DATA.MOVIE_LINK)
            openActivity<MovieViewActivity>(extras = arrayOf(DATA.MOVIE_LINK to movieLink))
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
                        binding.duration.text = (movie.duration?.toLong() ?: 0L).convertDuration()
                        binding.year.text = movie.year.toString()
                        
                        binding.category.loadCategory(movie.categoryId)
                        binding.image.loadGlideImage(movie.image, false)
                        binding.cover.loadGlideImage(movie.image, false)
                        
                        binding.favorite.isFavorite(movie.id, DATA.FirebaseUserUid)
                    }

                    state.publisher?.let { user ->
                        binding.publisherName.text = user.username
                        binding.publisherImage.loadGlideImage(user.profileImage, true)
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
