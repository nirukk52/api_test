package com.niranjan.android.laharisahitya.activity.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.model.PostType

class HomeAdapter(private val context: Context, private val category: ArrayList<Category>) :
        RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return category.size
    }

    override fun getItemViewType(position: Int): Int {

        if (position.equals(category.size - 1)) {
            return PostType.LOGIN.ordinal
        } else {
            return PostType.POST.ordinal

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        val view: View = if (type == PostType.LOGIN.ordinal) {
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_grid, viewGroup, false)
        } else {
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_login, viewGroup, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.tvMainList.text = category[i].title

        viewHolder.mainListParent.setOnClickListener {
            context.startActivity(
                    com.niranjan.android.laharisahitya.activity.list.ListActivity.newIntent(context, category[i])
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMainList: TextView
//        val ivMainList: ImageView
        val mainListParent: ConstraintLayout

        init {

            tvMainList = view.findViewById(R.id.tv_main_list) as TextView
//            ivMainList = view.findViewById(R.id.iv_main_list) as ImageView
            mainListParent = view.findViewById(R.id.mainListParent) as ConstraintLayout
        }
    }

}