package com.example.attendancetestproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class listAttend(private val context: Activity, private val attending: ArrayList<StudentEvent>) : BaseAdapter() {
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
        return attending.size
    }

    override fun getItem(position: Int): StudentEvent {
        return attending[position]
    }

    override fun getItemId(position: Int) : Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val rowMain = inflater.inflate(R.layout.layout_list_attending, parent, false)

        val textViewName: TextView = rowMain.findViewById(R.id.txtView_StuName)
        val textViewEmail: TextView = rowMain.findViewById(R.id.txtView_StuEmail)


        val at: StudentEvent = attending[position]
        textViewName.text = at.StudentName
        textViewEmail.text = at.StudentEmail




        return rowMain
    }




    private inner class ViewHolder {

    }
}