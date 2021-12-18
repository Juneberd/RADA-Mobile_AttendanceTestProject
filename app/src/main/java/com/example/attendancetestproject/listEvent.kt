package com.example.attendancetestproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class listEvent(private val context: Activity, private val eventing: ArrayList<Event>) : BaseAdapter() {

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
        return eventing.size
    }

    override fun getItem(position: Int): Event? {
        return eventing[position]
    }

    override fun getItemId(position: Int) : Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val rowMain = inflater.inflate(R.layout.layout_list_event, parent, false)

        val textViewName: TextView = rowMain.findViewById(R.id.txtView_EventName)
        val textViewCode: TextView = rowMain.findViewById(R.id.txtView_EventCode)


        val ev: Event = eventing[position]
        textViewName.text = ev.EventName
        textViewCode.text = ev.EventCode




        return rowMain
    }




    private inner class ViewHolder {

    }

}