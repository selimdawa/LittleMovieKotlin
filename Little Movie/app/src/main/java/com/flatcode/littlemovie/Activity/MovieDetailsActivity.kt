package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.Adapter.CastMovieAdapter
import com.flatcode.littlemovie.Adapter.CommentAdapter
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Model.Comment
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Model.User
import com.flatcode.littlemovie.MyApplication
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ActivityMovieDetailsBinding
import com.flatcode.littlemovie.databinding.DialogCommentAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MovieDetailsActivity : AppCompatActivity() {

    private var binding: ActivityMovieDetailsBinding? = null
    var activity: Activity = this@MovieDetailsActivity
    var movieId: String? = null
    var movieLink: String? = null
    var title: String? = null
    private var dialog: ProgressDialog? = null
    private var itemCast: ArrayList<String?>? = null
    private var listComment: ArrayList<Comment?>? = null
    private var listCast: ArrayList<Cast?>? = null
    private var adapterComment: CommentAdapter? = null
    private var adapterCast: CastMovieAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val intent = intent
        movieId = intent.getStringExtra(DATA.MOVIE_ID)
        movieLink = intent.getStringExtra(DATA.MOVIE_LINK)

        binding!!.toolbar.nameSpace.setText(R.string.details_movie)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.love.setOnClickListener { VOID.checkLove(binding!!.love, movieId) }
        binding!!.favorite.setOnClickListener { VOID.checkFavorite(binding!!.favorite, movieId) }
        binding!!.view.setOnClickListener {
            VOID.IntentExtra2(
                activity, CLASS.MOVIE_VIEW, DATA.MOVIE_LINK, movieLink, DATA.MOVIE_ID, movieId
            )
        }
        binding!!.addComment.setOnClickListener {
            if (DATA.FIREBASE_USER == null) {
                Toast.makeText(activity, "You're not logged in...", Toast.LENGTH_SHORT).show()
            } else {
                addCommentDialog()
            }
        }

        //binding.recyclerView.setHasFixedSize(true);
        listCast = ArrayList()
        binding!!.recyclerCast.adapter = adapterCast
        adapterCast = CastMovieAdapter(activity, listCast!!)
    }

    private fun start() {
        loadDetails()
        loadComments()
        cast
        VOID.isLoves(binding!!.love, movieId)
        VOID.nrLoves(binding!!.loves, movieId)
        VOID.isFavorite(binding!!.favorite, movieId, DATA.FirebaseUserUid)
    }

    private fun loadComments() {
        listComment = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.child(movieId!!).child(DATA.COMMENTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listComment!!.clear()
                    for (data in snapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        listComment!!.add(comment)
                    }
                    adapterComment = CommentAdapter(activity, listComment!!)
                    binding!!.recyclerComment.adapter = adapterComment
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private val cast: Unit
        get() {
            itemCast = ArrayList()
            val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE).child(movieId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    itemCast!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        itemCast!!.add(snapshot.key)
                    }
                    loadCast()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun loadCast() {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.CAST)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listCast!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val cast = snapshot.getValue(Cast::class.java)
                    for (id in itemCast!!) {
                        assert(cast != null)
                        if (cast!!.id != null) if (cast.id == id) {
                            listCast!!.add(cast)
                        }
                    }
                }
                binding!!.recyclerCast.adapter = adapterCast
                adapterCast!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var comment = DATA.EMPTY
    private fun addCommentDialog() {
        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentAddBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()
        commentAddBinding.back.setOnClickListener { v: View? -> alertDialog.dismiss() }
        commentAddBinding.submit.setOnClickListener { v: View? ->
            comment = commentAddBinding.comment.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(activity, "Enter your comment...", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                addComment()
            }
        }
    }

    private fun addComment() {
        dialog!!.setMessage("Adding comment...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        val id = ref.push().key
        //setup data to add in db for comment
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.ID] = id
        hashMap[DATA.MOVIE_ID] = DATA.EMPTY + movieId
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.COMMENT] = DATA.EMPTY + comment
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        assert(id != null)
        ref.child(movieId!!).child(DATA.COMMENTS).child(id!!).setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "Comment Added...", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
                loadComments()
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to add comment duo to  " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadDetails() {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.child(movieId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get data
                val item = snapshot.getValue(Movie::class.java)!!
                title = DATA.EMPTY + item.name
                val description = DATA.EMPTY + item.description
                val categoryId = DATA.EMPTY + item.categoryId
                val viewsCount = DATA.EMPTY + item.viewsCount
                val timestamp = DATA.EMPTY + item.timestamp
                val image = DATA.EMPTY + item.image
                val publisher = DATA.EMPTY + item.publisher
                val durations = DATA.EMPTY + item.duration
                val year = DATA.EMPTY + item.year

                //format date
                val date: String = MyApplication.formatTimestamp(timestamp.toLong())
                VOID.loadCategory(DATA.EMPTY + categoryId, binding!!.category)
                //set data
                VOID.GlideImage(false, activity, image, binding!!.image)
                VOID.GlideImage(false, activity, image, binding!!.cover)
                binding!!.title.text = title
                binding!!.description.text = description
                binding!!.views.text = viewsCount
                binding!!.date.text = date
                binding!!.duration.text = VOID.convertDuration(durations.toLong())
                binding!!.year.text = year
                userInfo(publisher)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun userInfo(userId: String) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(userId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get data
                val item = snapshot.getValue(User::class.java)!!
                val userId = DATA.EMPTY + item.id
                val imageProfile = DATA.EMPTY + item.profileImage
                val username = DATA.EMPTY + item.username

                binding!!.publisherName.text = username
                VOID.GlideImage(true, activity, imageProfile, binding!!.publisherImage)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onRestart() {
        start()
        super.onRestart()
    }

    override fun onResume() {
        start()
        super.onResume()
    }
}