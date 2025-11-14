package com.flatcode.littlemovieadmin.Activityimport

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Adapter.EditorsChoiceAdapter
import com.flatcode.littlemovieadmin.Model.EditorsChoice
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceBinding

class EditorsChoiceActivity : AppCompatActivity() {

    private var binding: ActivityEditorsChoiceBinding? = null
    var activity: Activity = this@EditorsChoiceActivity
    var list: ArrayList<EditorsChoice>? = null
    var adapter: EditorsChoiceAdapter? = null
    var editorsChoice = EditorsChoice()

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.editors_choice)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = EditorsChoiceAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter

        IdeaPosts()
    }

    fun IdeaPosts() {
        list!!.clear()
        for (i in 0..49) {
            list!!.add(editorsChoice)
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}