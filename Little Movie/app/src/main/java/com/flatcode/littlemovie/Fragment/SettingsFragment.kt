package com.flatcode.littlemovie.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flatcode.littlemovie.Adapter.SettingAdapter
import com.flatcode.littlemovie.Model.Setting
import com.flatcode.littlemovie.Model.User
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.FragmentSettingsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Objects

class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private var list: ArrayList<Setting>? = null
    private var adapter: SettingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(LayoutInflater.from(context), container, false)

        //binding!!.recyclerView.setHasFixedSize(true)
        list = ArrayList()
        adapter = SettingAdapter(context, list!!)
        binding!!.recyclerView.adapter = adapter

        binding!!.toolbar.item.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }
        return binding!!.root
    }

    var CAST = 0
    var CAT = 0
    var FAV = 0

    private fun nrItems() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(DATA.CAST)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                CAST = dataSnapshot.childrenCount.toInt()
                nrCategories()
            }

            private fun nrCategories() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                    .child(DATA.FirebaseUserUid).child(DATA.CATEGORIES)
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CAT = dataSnapshot.childrenCount.toInt()
                        nrFavorites()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrFavorites() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                    .child(DATA.FirebaseUserUid)
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        FAV = dataSnapshot.childrenCount.toInt()
                        loadSettings(CAST, CAT, FAV)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(User::class.java)!!
                    val ProfileImage = item.profileImage
                    val Username = item.username
                    val Contact = item.email

                    VOID.GlideImage(true, context, ProfileImage, binding!!.toolbar.imageProfile)
                    binding!!.toolbar.username.text = Username
                    binding!!.toolbar.email.text = Contact
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadSettings(myCast: Int, myCategories: Int, favorites: Int) {
        list!!.clear()
        val item = Setting("1", "Edit Profile", R.drawable.ic_edit_white, 0, CLASS.PROFILE_EDIT)
        val item2 = Setting("2", "My Cast", R.drawable.ic_cast, myCast, CLASS.MY_CAST)
        val item3 = Setting(
            "3", "My Categories", R.drawable.ic_category_gray,
            myCategories, CLASS.MY_CATEGORIES
        )
        val item4 =
            Setting("4", "Favorites", R.drawable.ic_star_selected, favorites, CLASS.FAVORITES)
        val item5 = Setting("5", "About App", R.drawable.ic_info, 0, null)
        val item6 = Setting("6", "Logout", R.drawable.ic_logout_white, 0, null)
        val item7 = Setting("7", "Share App", R.drawable.ic_share, 0, null)
        val item8 = Setting("8", "Rate APP", R.drawable.ic_heart_selected, 0, null)
        val item9 =
            Setting("9", "Privacy Policy", R.drawable.ic_privacy_policy, 0, CLASS.PRIVACY_POLICY)
        list!!.add(item)
        list!!.add(item2)
        list!!.add(item3)
        list!!.add(item4)
        list!!.add(item5)
        list!!.add(item6)
        list!!.add(item7)
        list!!.add(item8)
        list!!.add(item9)
        adapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        loadUserInfo()
        nrItems()
        super.onResume()
    }
}