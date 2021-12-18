package com.example.attendancetestproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class listRecentAttended(private val context: Activity, private val recent: ArrayList<RecentAttended_data>) : BaseAdapter() {

    private val mContext: Context

    init {
        mContext = context
    }
    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getCount(): Int {
        return recent.size
    }

    override fun getItem(position: Int): RecentAttended_data? {
        return recent[position]
    }

    override fun getItemId(position: Int) : Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val rowMain = inflater.inflate(R.layout.layout_list_recentattended, parent, false)

        val txtViewR_Name: TextView = rowMain.findViewById(R.id.txtView_RecentEventName)
        val txtViewR_Faci: TextView = rowMain.findViewById(R.id.txtView_RecentFaci)
        val txtViewR_Date: TextView = rowMain.findViewById(R.id.txtView_RecentDate)


        val ad: RecentAttended_data = recent[position]
        txtViewR_Name.text = ad.EventName
        txtViewR_Faci.text = ad.EventFaci
        txtViewR_Date.text = ad.EventDate




        return rowMain
    }




    private inner class ViewHolder {

    }

}