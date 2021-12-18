package com.example.attendancetestproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_create_event.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CreateEvent : AppCompatActivity() {

    private var eventingName: EditText? = null
    private var eventingDescrip: EditText? = null
    private var eventingDate: EditText? = null
    private var eventingTimeStart: EditText? = null
    private var eventingTimeEnd: EditText? = null

    private var Usermail: String = ""

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val i = intent
        var usermail = i.getStringExtra("TextBoxEmail")
        Usermail = usermail.toString()


        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        imgBtn_datePicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@CreateEvent,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

        //time picker
        val mTimePickerS: TimePickerDialog
        val mTimePickerE: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePickerS = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                txtEdit_EventTimeStart.setText(String.format("%d : %d", hourOfDay, minute))
            }
        }, hour, minute, false)

        mTimePickerE = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                txtEdit_EventTimeEnd.setText(String.format("%d : %d", hourOfDay, minute))
            }
        }, hour, minute, false)

        imgBtn_TimeStartPicker.setOnClickListener { v ->
            mTimePickerS.show()
        }
        imgBtn_TimePickerEnd.setOnClickListener { v ->
            mTimePickerE.show()
        }


        creatingEvent(Usermail)

    }

    private fun creatingEvent(em:String) {
        btnCreateEvent_2.setOnClickListener{



            eventingName = txtEdit_EventName
            eventingDescrip = txtEdit_EventDescip
            //EMAILING = edtIn_EmailView
            eventingDate = txtEdit_EventDate
            eventingTimeStart = txtEdit_EventTimeStart
            eventingTimeEnd = txtEdit_EventTimeEnd

            val eventN = eventingName?.text.toString()
            val eventD = eventingDescrip?.text.toString()
            //val em = EMAILING?.text.toString()
            val date = eventingDate?.text.toString()
            val timeS = eventingTimeStart?.text.toString()
            val timeE = eventingTimeEnd?.text.toString()


            //creating event with JSON Volley

            val stringRequest = object : StringRequest(
                Request.Method.POST, EndPoints.URL_CREATE_EVENT,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)

                        if (obj.getString("message") == "Required parameter missing") {
                            SweetAlertDialog(
                                this,
                                SweetAlertDialog.WARNING_TYPE
                            ).setTitleText("Required Parameter Missing!")
                                .setContentText("Something missing in data")
                                .show()
                        }  else if (obj.getString("message") == "Created event is succesfully") {
                            SweetAlertDialog(
                                this,
                                SweetAlertDialog.SUCCESS_TYPE
                            ).setTitleText("Event is created!")
                                .setContentText("").show()
                            //val intent = Intent(this, MainActivity::class.java)
                            //startActivity(intent)
                        }
                        //else not updating
                        else {
                            SweetAlertDialog(
                                this,
                                SweetAlertDialog.ERROR_TYPE
                            ).setTitleText("The event not create!")
                                .setContentText("Something error").show()
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
                    params["Email"] = em
                    params["EventName"] = eventN
                    params["EventDescrip"] = eventD
                    params["EventDate"] = date
                    params["TimeStart"] = timeS
                    params["TimeEnd"] = timeE

                    return params
                }
            }
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)


        }

    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txtEdit_EventDate.setText(sdf.format(cal.getTime()))
    }
}