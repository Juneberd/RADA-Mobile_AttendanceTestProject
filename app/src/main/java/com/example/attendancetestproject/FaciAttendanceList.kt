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
import kotlinx.android.synthetic.main.activity_faci_attendance_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class FaciAttendanceList : AppCompatActivity() {

    private lateinit var listView_Student: ListView
    internal lateinit var AttendList: ArrayList<StudentEvent>

    private var Code: String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faci_attendance_list)

        val i = intent
        var code = i.getStringExtra("TextBoxCoding")
        Code = code.toString()

        listView_Student = findViewById(R.id.listView_Student)

        attendingList(Code)

    }

    private fun attendingList(c:String){
        //list who attend
        showSimpleProgressDialog(this, "Loading...", "Getting record who attend", false)

        val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Request.Method.POST, EndPoints.URL_ATTENDLIST,
            Response.Listener<String> { response ->
                try {
                    Log.d("Debugging: ",response)
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {

                        AttendList = ArrayList()
                        val array = obj.getJSONArray("Event_tbl")

                        for (i in 0 until array.length()) {
                            val objectEvent = array.getJSONObject(i)
                            val atn =  StudentEvent(
                                objectEvent.getString("FaciStu_tbl.Fullname"),
                                objectEvent.getString("FaciStu_tbl.Email"),
                            )
                            AttendList.add(atn)

                        }
                        setupListview()
                        var count = listView_Student.getAdapter().getCount().toString()
                        txtCountAttend.setText("Number of who Attend: $count")

                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Nothing event found")
                            .setContentText("No creating event yet")
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
                params["EventCode"] = c

                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListview() {
        removeSimpleProgressDialog() //will remove progress dialog
        listView_Student.adapter = listAttend(this, AttendList)
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