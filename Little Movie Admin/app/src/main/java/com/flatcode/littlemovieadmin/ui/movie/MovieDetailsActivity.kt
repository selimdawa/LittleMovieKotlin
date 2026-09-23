package com.flatcode.littlemovieadmin.ui.movie

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.Application
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityMovieDetailsBinding
import com.flatcode.littlemovieadmin.databinding.DialogCommentAddBinding
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.ui.cast.CastDetailsActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.checkFavorite
import com.flatcode.littlemovieadmin.utils.convertDuration
import com.flatcode.littlemovieadmin.utils.createProgressDialog
import com.flatcode.littlemovieadmin.utils.isFavorite
import com.flatcode.littlemovieadmin.utils.loadCategory
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.nrLoves
import com.flatcode.littlemovieadmin.utils.openActivity
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private var progressDialog: AlertDialog? = null

    private lateinit var adapterComment: CommentAdapter
    private lateinit var adapterCast: CastMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra(DATA.MOVIE_ID) ?: ""
        viewModel.setMovieId(movieId)

        binding.toolbar.nameSpace.setText(R.string.details_movie)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.loves.nrLoves(movieId)
        binding.favorite.setOnClickListener { binding.favorite.checkFavorite(movieId) }

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

        adapterCast = CastMovieAdapter(
            onItemClick = { cast ->
                openActivity<CastDetailsActivity>(
                    extras = arrayOf(
                        DATA.CAST_ID to cast.id,
                        DATA.CAST_NAME to cast.name,
                        DATA.CAST_IMAGE to cast.image,
                        DATA.CAST_ABOUT to cast.aboutMy
                    )
                )
            })
        binding.recyclerCast.adapter = adapterCast

        adapterComment = CommentAdapter(
            onItemClick = { comment ->
                if (comment.publisher == DATA.FirebaseUserUid) {
                    val commentId = comment.id ?: DATA.EMPTY
                    val movieId = comment.movieId ?: DATA.EMPTY
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Delete Comment")
                        .setMessage("Are you sure you want to delete this comment?")
                        .setPositiveButton("DELETE") { _, _ ->
                            val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                            ref.child(movieId).child(DATA.COMMENTS).child(commentId).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Deleted...", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Failed to delete due to " + e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }.show()
                }
            })
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
                        binding.image.loadImage(movie.image, isUser = false)
                        binding.cover.loadImage(movie.image, isUser = false)

                        binding.favorite.isFavorite(movie.id, DATA.FirebaseUserUid)
                    }

                    state.publisher?.let { user ->
                        binding.publisherName.text = user.username
                        binding.publisherImage.loadImage(user.profileImage, isUser = true)
                    }

                    adapterComment.submitList(state.comments)
                    adapterCast.submitList(state.castList)
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
                viewModel.addComment(comment) { _, message ->
                    progressDialog?.dismiss()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                progressDialog = createProgressDialog("Adding comment...", "Please wait...")
                progressDialog?.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}