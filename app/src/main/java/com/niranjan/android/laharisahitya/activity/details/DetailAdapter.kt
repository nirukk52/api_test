package com.niranjan.android.laharisahitya.activity.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.activity.home.Category

/**
 * Created by Depression on 22-11-2018.
 */

class DetailAdapter(private val context: Context, private val category: ArrayList<Category>) :
        RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return category.size
    }

    override fun getItemViewType(position: Int): Int {

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DetailAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_grid, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: DetailAdapter.ViewHolder, i: Int) {
        viewHolder.tvMainList.text = category[i].title

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

    companion object {
        private const val TITLE = 2
        private const val TOOLBAR = 1
        private const val BODY = 3
        private const val PHOTO = 4

    }

}