package com.flatcode.littlemovieadmin.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.flatcode.littlemovieadmin.Adapter.MainAdapter
import com.flatcode.littlemovieadmin.Model.Main
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Model.User
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    private var binding: ActivityMainBinding? = null
    var list: MutableList<Main>? = null
    var adapter: MainAdapter? = null
    var context: Context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.getDefaultSharedPreferences(baseContext)
            .registerOnSharedPreferenceChangeListener(this)
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        // Color Mode ----------------------------- Start
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        // Color Mode -------------------------------- End
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(baseContext)
        if (sharedPreferences.getString(DATA.COLOR_OPTION, "ONE") == "ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.sun)
        } else if (sharedPreferences.getString(DATA.COLOR_OPTION, "NIGHT_ONE") == "NIGHT_ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.moon)
        }
        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = MainAdapter(context, list as ArrayList<Main>)
        binding!!.recyclerView.adapter = adapter
    }

    var U = 0
    var MO = 0
    var EC = 0
    var CA = 0
    var SL = 0
    var CAST = 0
    var FA = 0
    private fun nrItems() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                U = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(User::class.java)!!
                    if (item.id != null && item.id != DATA.FirebaseUserUid) U++
                }
                nrMovies()
            }

            private fun nrMovies() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        MO = 0
                        EC = 0
                        for (data in dataSnapshot.children) {
                            val item = data.getValue(Movie::class.java)!!
                            if (item.id != null) {
                                MO++
                                if (item.editorsChoice != 0) if (item.publisher == DATA.FirebaseUserUid) EC++
                            }
                        }
                        nrCategories()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrCategories() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CA = 0
                        for (data in dataSnapshot.children) {
                            val item = data.getValue(Movie::class.java)!!
                            if (item.id != null) if (item.publisher == DATA.FirebaseUserUid) CA++
                        }
                        nrSliderShow()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrSliderShow() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        SL = 0
                        SL = dataSnapshot.childrenCount.toInt()
                        nrCast()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrCast() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.CAST)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CAST = 0
                        CAST = dataSnapshot.childrenCount.toInt()
                        nrFavorites()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrFavorites() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                    .child(DATA.FirebaseUserUid)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        FA = 0
                        FA = dataSnapshot.childrenCount.toInt()
                        IdeaPosts(U, MO, EC, CA, SL, CAST, FA)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val reference =
            FirebaseDatabase.getInstance().getReference(DATA.USERS).child(DATA.FirebaseUserUid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)!!
                VOID.GlideImage(true, context, user.profileImage, binding!!.toolbar.image)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun IdeaPosts(
        users: Int, movies: Int, editorsChoice: Int, categories: Int, sliderShow: Int,
        cast: Int, favorites: Int,
    ) {
        list!!.clear()
        val item1 = Main(R.drawable.ic_person, "Users", users, CLASS.USERS)
        val item2 = Main(R.drawable.ic_add, "Add Movie", 0, CLASS.MOVIE_ADD)
        val item3 = Main(R.drawable.ic_movie, "Movies", movies, CLASS.MOVIES)
        val item4 =
            Main(R.drawable.ic_users, "Editors Choice", editorsChoice, CLASS.EDITORS_CHOICE)
        val item5 = Main(R.drawable.ic_add_category, "Add Category", 0, CLASS.CATEGORY_ADD)
        val item6 = Main(R.drawable.ic_category_gray, "Categories", categories, CLASS.CATEGORIES)
        val item7 = Main(R.drawable.ic_slider, "Slider Show", sliderShow, CLASS.SLIDER_SHOW)
        val item8 = Main(R.drawable.ic__add, "Add Cast", 0, CLASS.CAST_ADD)
        val item9 = Main(R.drawable.ic_cast, "Cast", cast, CLASS.CAST)
        val item10 = Main(R.drawable.ic_star_selected, "Favorites", favorites, CLASS.FAVORITES)
        val item11 = Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, CLASS.PRIVACY_POLICY)
        list!!.add(item1)
        list!!.add(item2)
        list!!.add(item3)
        list!!.add(item4)
        list!!.add(item5)
        list!!.add(item6)
        list!!.add(item7)
        list!!.add(item8)
        list!!.add(item9)
        list!!.add(item10)
        list!!.add(item11)
        adapter!!.notifyDataSetChanged()
        binding!!.bar.visibility = View.GONE
        binding!!.recyclerView.visibility = View.VISIBLE
    }

    // Color Mode ----------------------------- Start
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "color_option") {
            recreate()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_CODE) {
            recreate()
        }
    }

    // Color Mode -------------------------------- End
    override fun onResume() {
        userInfo()
        nrItems()
        super.onResume()
    }

    companion object {
        private const val SETTINGS_CODE = 234
    }
}