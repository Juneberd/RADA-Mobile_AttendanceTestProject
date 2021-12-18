package com.example.attendancetestproject

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class StudentRecentListAttended : AppCompatActivity() {

    private lateinit var listView_Attended: ListView
    internal lateinit var AttendedList: ArrayList<RecentAttended_data>

    private var Mailing:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_recent_list_attended)

        val i = intent
        var m = i.getStringExtra("TextBoxE")
        Mailing = m.toString()

        //initialize the listview screen
        listView_Attended = findViewById(R.id.listView_RecentAttended)

        //Toast.makeText(this, Mailing , Toast.LENGTH_SHORT).show()

        attended(Mailing)
    }

    private fun attended(m:String){

        showSimpleProgressDialog(this, "Loading...", "Getting history", false)

        val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Request.Method.POST, EndPoints.URL_RECENT_ATTENDED,
            Response.Listener<String> { response ->
                try {
                    Log.d("Debugging: ",response)
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {

                        AttendedList = ArrayList()
                        val array = obj.getJSONArray("Event_tbl")

                        for (i in 0 until array.length()) {
                            val objectEvent = array.getJSONObject(i)
                            val atnd =  RecentAttended_data(
                                objectEvent.getString("Event_tbl.EventName"),
                                objectEvent.getString("Event_tbl.FacilitatorId"),
                                objectEvent.getString("Event_tbl.EventDateFrom"),

                            )
                            AttendedList.add(atnd)

                        }
                        setupListview()


                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Nothing attended found")
                            .setContentText("No attended event yet")
                            .setConfirmText("Okay")
                            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                                this.finish()
                            }
                            .show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError ->
                Toast.makeText(
                    applicationContext,
                    volleyError.message,
                    Toast.LENGTH_LONG
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["StudentEmail"] = m

                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListview() {
        removeSimpleProgressDialog() //will remove progress dialog
        listView_Attended.adapter = listRecentAttended(this, AttendedList)
    }



    //loading dialog
    companion object{
        //@SuppressLint("StaticFieldLeak")
        private var mProgressDialog: ProgressDialog? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun removeSimpleProgressDialog() {
            try {
                if (mProgressDialog != null) {
                    if (mProgressDialog!!.isShowing) {
                        mProgressDialog!!.dismiss()
                        mProgressDialog = null
                    }
                }
            } catch (ie: IllegalArgumentException) {
                ie.printStackTrace()
            } catch (re: RuntimeException) {
                re.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showSimpleProgressDialog(
            context: Context, title: String,
            msg: String, isCancelable: Boolean
        ){
            try {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, title, msg)
                    mProgressDialog!!.setCancelable(isCancelable)
                }

                if (!mProgressDialog!!.isShowing) {
                    mProgressDialog!!.show()
                }
            } catch (ie: IllegalArgumentException) {
                ie.printStackTrace()
            } catch (re: RuntimeException) {
                re.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}