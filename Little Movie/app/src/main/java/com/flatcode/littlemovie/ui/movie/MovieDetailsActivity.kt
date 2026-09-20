package com.flatcode.littlemovie.ui.movie

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.ui.cast.CastDetailsActivity
import com.flatcode.littlemovie.ui.cast.CastMovieAdapter
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.Application
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.convertDuration
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ActivityMovieDetailsBinding
import com.flatcode.littlemovie.databinding.DialogCommentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private var binding: ActivityMovieDetailsBinding? = null
    private val activity: Activity = this@MovieDetailsActivity
    private val viewModel: MovieDetailsViewModel by viewModels()
    
    private var movieId: String? = null
    private var movieLink: String? = null
    
    private var dialog: ProgressDialog? = null
    
    private lateinit var adapterComment: CommentAdapter
    private lateinit var adapterCast: CastMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            binding!!.toolbar.root.updatePadding(top = systemBars.top)
            insets
        }

        movieId = intent.getStringExtra(DATA.MOVIE_ID)
        movieLink = intent.getStringExtra(DATA.MOVIE_LINK)

        setupUI()
        setupAdapters()
        observeViewModel()
        
        movieId?.let { viewModel.loadDetails(it) }
    }

    private fun setupUI() {
        binding!!.toolbar.nameSpace.setText(R.string.details_movie)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        
        dialog = ProgressDialog(activity).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.love.setOnClickListener { movieId?.let { viewModel.toggleLove(it) } }
        binding!!.favorite.setOnClickListener { movieId?.let { viewModel.toggleFavorite(it) } }
        binding!!.view.setOnClickListener {
            activity.openActivity<MovieViewActivity>(DATA.MOVIE_LINK to movieLink, DATA.MOVIE_ID to movieId)
        }
        binding!!.addComment.setOnClickListener {
            if (DATA.FIREBASE_USER == null) {
                Toast.makeText(activity, "You're not logged in...", Toast.LENGTH_SHORT).show()
            } else {
                addCommentDialog()
            }
        }
    }

    private fun setupAdapters() {
        adapterCast = CastMovieAdapter { cast ->
            activity.openActivity<CastDetailsActivity>(
                DATA.CAST_ID to cast.id,
                DATA.CAST_NAME to cast.name,
                DATA.CAST_IMAGE to cast.image,
                DATA.CAST_ABOUT to cast.aboutMy
            )
        }
        binding!!.recyclerCast.adapter = adapterCast
        
        adapterComment = CommentAdapter { comment ->
            showDeleteCommentDialog(comment)
        }
        binding!!.recyclerComment.adapter = adapterComment
    }

    private fun showDeleteCommentDialog(comment: Comment) {
        AlertDialog.Builder(activity)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("DELETE") { _, _ ->
                val movieId = comment.movieId ?: ""
                val commentId = comment.id ?: ""
                viewModel.deleteComment(movieId, commentId)
            }
            .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.movie.collect { movie ->
                movie?.let {
                    val date: String = Application.formatTimestamp(it.timestamp)
                    binding!!.image.GlideImage(false, it.image)
                    binding!!.cover.GlideImage(false, it.image)
                    binding!!.title.text = it.name
                    binding!!.description.text = it.description
                    binding!!.views.text = it.viewsCount.toString()
                    binding!!.date.text = date
                    binding!!.duration.text = (it.duration?.toLong() ?: 0L).convertDuration()
                    binding!!.year.text = it.year.toString()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.category.collect { category ->
                binding!!.category.text = category?.name ?: ""
            }
        }

        lifecycleScope.launch {
            viewModel.comments.collect { comments ->
                adapterComment.submitList(comments)
            }
        }

        lifecycleScope.launch {
            viewModel.cast.collect { castMembers ->
                adapterCast.submitList(castMembers)
            }
        }

        lifecycleScope.launch {
            viewModel.publisher.collect { user ->
                user?.let {
                    binding!!.publisherName.text = it.username
                    binding!!.publisherImage.GlideImage(true, it.profileImage)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding!!.favorite.setImageResource(
                    if (isFavorite) R.drawable.ic_star_selected else R.drawable.ic_star_unselected
                )
            }
        }

        lifecycleScope.launch {
            viewModel.isLoved.collect { isLoved ->
                binding!!.love.setImageResource(
                    if (isLoved) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected
                )
            }
        }

        lifecycleScope.launch {
            viewModel.lovesCount.collect { count ->
                binding!!.loves.text = count.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.addCommentStatus.collect { result ->
                result?.let {
                    dialog!!.dismiss()
                    if (it.isSuccess) {
                        Toast.makeText(activity, "Comment Added...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Failed to add comment: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetAddCommentStatus()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deleteCommentStatus.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(activity, "Deleted...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Failed to delete: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetDeleteCommentStatus()
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
            val commentText = commentAddBinding.comment.text.toString().trim()
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(activity, "Enter your comment...", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                addComment(commentText)
            }
        }
    }

    private fun addComment(commentText: String) {
        dialog!!.setMessage("Adding comment...")
        dialog!!.show()
        movieId?.let { viewModel.addComment(it, commentText) }
    }
}
