package com.example.attendancetestproject

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.activity_faci_event.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set





class activity_FaciEvent : AppCompatActivity() {
    private lateinit var listView: ListView
    internal lateinit var EventList: ArrayList<Event>

    private var Coding: String = ""


    private var Em = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faci_event)

        //initialize the listview screen
        listView = findViewById(R.id.listView_Event)

        val i = intent
        val mailtext = i.getStringExtra("TextBoxEmail")
        Em = mailtext.toString()

        loadEvent(Em)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEvent(em:String){

        showSimpleProgressDialog(this, "Loading...", "Please wait reload event list", false)

        val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_VIEWFACI_EVENT,
            Response.Listener<String> { response ->
                try {
                    Log.d("Debugging: ",response)
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {

                        EventList = ArrayList()
                        val array = obj.getJSONArray("Event_tbl")

                        for (i in 0 until array.length()) {
                            val objectEvent = array.getJSONObject(i)
                            val ent =  Event(
                                objectEvent.getString("EventName"),
                                objectEvent.getString("EventCode"),
                            )
                            EventList.add(ent)

                        }
                        setupListview()



                    } else {
                        //Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show()

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
                params["FacilitatorId"] = em

                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        val mDialogView_Event =
            LayoutInflater.from(this).inflate(R.layout.popupwindow_fulldetailevent, null)

        listView_Event.setOnItemClickListener { parent, view, position, id ->

            //val txtCode:TextView = view.txtView_EventCode
            val selectedItemText = parent.getItemAtPosition(position).toString()
            val k = selectedItemText.substring(selectedItemText.lastIndexOf("=") + 1)
            val keyCode = k.substring(0, k.length - 1)
            //Toast.makeText(this, keyCode, Toast.LENGTH_SHORT).show()

            //show full details event

            val mBuilder_Event = AlertDialog.Builder(this)
                .setView(mDialogView_Event)

            val EventName: TextView = mDialogView_Event.findViewById(R.id.txtViewEventName)
            val EventCode: TextView = mDialogView_Event.findViewById(R.id.txtViewEventCode)
            val EventDescrip: TextView = mDialogView_Event.findViewById(R.id.txtViewEventDescrip)
            val EventDateTime: TextView = mDialogView_Event.findViewById(R.id.txtViewDateTime)
            val genQRCode :  ImageView = mDialogView_Event.findViewById(R.id.imgGenQRCode)

            // event with JSON Volley
            val stringRequest =
                object : StringRequest(Request.Method.POST, EndPoints.URL_VIEWFACI_EVENTBYCODE,
                    Response.Listener<String> { response ->
                        try {

                            val jsonobject = JSONObject(response)
                            val jsonArray = jsonobject.getJSONArray("Event_tbl")
                            val data: JSONObject = jsonArray.getJSONObject(0)

                            val eventName = data.getString("EventName")
                            EventName.text = eventName.toString()

                            val eventCode = data.getString("EventCode")
                            EventCode.text = eventCode.toString()
                            Coding = eventCode.toString()

                            val text = eventCode.toString()
                            if (text.isNotBlank()) {
                                val bitmap = generateQRCode(text)
                                genQRCode.setImageBitmap(bitmap)
                            }

                            val eventDesc = data.getString("EventDesc")
                            EventDescrip.text = eventDesc.toString()
                            EventDescrip.movementMethod = ScrollingMovementMethod()

                            val eventDate = data.getString("EventDateFrom")
                            val eventTimeStart = data.getString("EventTimeStart")
                            val eventTimeEnd = data.getString("EventTimeEnd")
                            val dateT_1 = "${eventDate}  ${eventTimeStart}  to  ${eventTimeEnd}"
                            EventDateTime.text = dateT_1

                            //Toast.makeText(this, "$eventName \n $dateT_1", Toast.LENGTH_SHORT).show()

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
                        params["EventCode"] = keyCode

                        return params
                    }
                }
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

            mBuilder_Event.show()


            val btnListAttend: Button = mDialogView_Event.findViewById(R.id.btnListAttend)
            val btnCopy: Button = mDialogView_Event.findViewById(R.id.btnCopy)

            btnListAttend.setOnClickListener {

                val str = Coding
               // Toast.makeText(this, "$str", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, FaciAttendanceList::class.java)
                intent.putExtra("TextBoxCoding", str)
                startActivity(intent)
            }

            btnCopy.setOnClickListener{
                val textToCopy = Coding
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Keycode for Attending Event copied to clipboard", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 300
        val height = 300
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.RED else Color.WHITE)
                }
            }
        } catch (e: WriterException) { Log.d(TAG, "generateQRCode: ${e.message}") }
        return bitmap
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListview() {
        removeSimpleProgressDialog() //will remove progress dialog
        listView.adapter = listEvent(this, EventList)
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

    private fun popUpEventInfo() {

    }
}