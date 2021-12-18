package com.example.attendancetestproject

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.signup_facilitator.view.*
import kotlinx.android.synthetic.main.signup_student.view.*
import org.json.JSONException
import org.json.JSONObject





class MainActivity : AppCompatActivity() {
    //for LogIn Text
    private var username: EditText? = null
    private var password: EditText? = null
    lateinit var btnLogIn: Button

    //for facilatator
    private var f_txtFullname: EditText? = null
    private var f_txtEmail: EditText? = null
    private var f_txtUsername: EditText? = null
    private var f_txtPassword: EditText? = null
    private var f_txtConPassword: EditText? = null
    //lateinit var btnAddFaciStu: Button

    //for student
    //for facilatator
    private var s_txtFullname: EditText? = null
    private var s_txtEmail: EditText? = null
    private var s_txtUsername: EditText? = null
    private var s_txtPassword: EditText? = null
    private var s_txtConPassword: EditText? = null
    //lateinit var btnAddFaciStu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        popUpMenuSignIn()
        LogIn()
    }

    private fun popUpMenuSignIn() {

        val popupMenu = PopupMenu(applicationContext, signBtn)
        popupMenu.inflate(R.menu.popmenu_signin)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.teacher -> {
                    val mDialogView_F =
                        LayoutInflater.from(this).inflate(R.layout.signup_facilitator, null)
                    val mBuiilder_F = AlertDialog.Builder(this)
                        .setView(mDialogView_F)
                    val mAlertDialog = mBuiilder_F.show()

                    mDialogView_F.btnF_Cancel.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialogView_F.btnF_sign.setOnClickListener {
                        //addFaciStu()
                        //getting text from facilitator xml
                        f_txtFullname = mDialogView_F.f_txtEditFullname
                        f_txtEmail = mDialogView_F.f_txtEditEmail
                        f_txtUsername = mDialogView_F.f_txtEditUsername
                        f_txtPassword = mDialogView_F.f_txtEditPassword
                        f_txtConPassword = mDialogView_F.f_txtEditConfirmPassword
                        //btnAddFaciStu = findViewById<Button>(R.id.btnF_sign)
                        val Roles = "Facilitator"

                        //getting the record values
                        val fname = f_txtFullname?.text.toString()
                        val email = f_txtEmail?.text.toString()
                        val username = f_txtUsername?.text.toString()
                        val password = f_txtPassword?.text.toString()
                        val conPass = f_txtConPassword?.text.toString()
                        val roling = "$Roles"

                        if(fname.isNullOrBlank() || email.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() || conPass.isNullOrBlank()){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Required Parameter Missing!").setContentText("Input requirement completely").show()
                        }
                        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Email is invalid!").setContentText("Input email properly").show()
                        }
                        else if(password != conPass){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Password is Mismatch!").setContentText("Input password and confirm password again").show()
                        }
                        else {

                            //creating volley string request
                            val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_ADD_FACISTU,
                                Response.Listener<String> { response ->
                                    try {
                                        val obj = JSONObject(response)
                                        /*Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG)
                                            .show()*/
                                        if(obj.getString("message") == "Required parameter missing"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Required Parameter Missing!").setContentText("Input requirement completely").show()
                                        }
                                        else if (obj.getString("message") == "Could not sign up"){
                                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Could not sign up !").setContentText("Something error").show()
                                        }
                                        else if (obj.getString("message") == "The Username is already taken"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Username is already taken!").setContentText("Please input the username again").show()
                                        }
                                        else if (obj.getString("message") == "The Email is already taken"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Email is already taken!").setContentText("Please input the email again").show()
                                        }
                                        else if(obj.getString("message") == "Sign Up is succesfully"){
                                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE ).setTitleText("The Sign up is Succesfully!").setContentText("You can proceed").show()
                                            txtInEdit_UserLogIn.setText(username)
                                            editxt_PasswordLogin.setText(password)
                                            mAlertDialog.dismiss()
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
                                    params["Fullname"] = fname
                                    params["Email"] = email
                                    params["Username"] = username
                                    params["Passwords"] = password
                                    params["Roles"] = roling
                                    return params
                                }
                            }
                            //adding request to queue
                            VolleySingleton.instance?.addToRequestQueue(stringRequest)
                        }

                    }

                    Toast.makeText(
                        applicationContext, "You are signing up as a facilitator",
                        Toast.LENGTH_SHORT
                    ).show()
                    true

                }
                R.id.student -> {
                    val mDialogView_S =
                        LayoutInflater.from(this).inflate(R.layout.signup_student, null)
                    val mBuilder_S = AlertDialog.Builder(this)
                        .setView(mDialogView_S)
                    val mAlertDialog = mBuilder_S.show()


                    mDialogView_S.btnS_Cancel.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialogView_S.btnS_sign.setOnClickListener {
                        //addFaciStu()
                        //getting text from facilitator xml
                        s_txtFullname = mDialogView_S.s_txtEditFullname
                        s_txtEmail = mDialogView_S.s_txtEditEmail
                        s_txtUsername = mDialogView_S.s_txtEditUsername
                        s_txtPassword = mDialogView_S.s_txtEditPassword
                        s_txtConPassword = mDialogView_S.s_txtEditConfirmPassword
                        //btnAddFaciStu = findViewById<Button>(R.id.btnF_sign)
                        val Roles = "Student"

                        //getting the record values
                        val fname = s_txtFullname?.text.toString()
                        val email = s_txtEmail?.text.toString()
                        val username = s_txtUsername?.text.toString()
                        val password = s_txtPassword?.text.toString()
                        val conPass = s_txtConPassword?.text.toString()
                        val roling = "$Roles"

                        if(fname.isNullOrBlank() || email.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() || conPass.isNullOrBlank()){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Required Parameter Missing!").setContentText("Input requirement completely").show()
                        }
                        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Email is invalid!").setContentText("Input email properly").show()
                        }
                        else if(password != conPass){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Password is Mismatch!").setContentText("Input password and confirm password again").show()
                        }
                        else {
                            //creating volley string request
                            val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_ADD_FACISTU,
                                Response.Listener<String> { response ->
                                    try {
                                        val obj = JSONObject(response)
                                        /*Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG)
                                            .show()*/
                                        if(obj.getString("message") == "Required parameter missing"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Required Parameter Missing!").setContentText("Input requirement completely").show()
                                        }
                                        else if (obj.getString("message") == "Could not sign up"){
                                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Could not sign up !").setContentText("Something error").show()
                                        }
                                        else if (obj.getString("message") == "The Username is already taken"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Username is already taken!").setContentText("Please input the username again").show()
                                        }
                                        else if (obj.getString("message") == "The Email is already taken"){
                                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Email is already taken!").setContentText("Please input the email again").show()
                                        }
                                        else{
                                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE ).setTitleText("The Sign up is Succesfully!").setContentText("You can proceed").show()
                                            txtInEdit_UserLogIn.setText(username)
                                            editxt_PasswordLogin.setText(password)
                                            mAlertDialog.dismiss()
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
                                    params["Fullname"] = fname
                                    params["Email"] = email
                                    params["Username"] = username
                                    params["Passwords"] = password
                                    params["Roles"] = roling
                                    return params
                                }
                            }
                            //adding request to queue
                            VolleySingleton.instance?.addToRequestQueue(stringRequest)
                        }

                    }

                    Toast.makeText(
                        applicationContext, "You are signing up as a student",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                else -> {
                    true
                }

            }
        }
        signBtn.setOnClickListener {
            try {
                val popUp = PopupMenu::class.java.getDeclaredField("mPopup")
                popUp.isAccessible = true
                val menu = popUp.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }

            true
        }
    }

   /* private fun addFaciStu() {

    }*/

    private fun LogIn(){

        btnLogIn = findViewById<Button>(R.id.BTN_logIn)

        btnLogIn.setOnClickListener{
            /*Toast.makeText(
                applicationContext, "You're Log In",
                Toast.LENGTH_SHORT
            ).show()
            true*/

           username = txtInEdit_UserLogIn
            password = editxt_PasswordLogin

            val user = username?.text.toString()
            val pass = password?.text.toString()

            //creating volley string request
            val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_LogInCheck_FACISTU,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        /*Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG)
                            .show()*/
                        if(obj.getString("message") == "Log in Succesfully") {
                            val jsonarray = obj.getJSONArray("FaciStu_tbl")
                            val data: JSONObject = jsonarray.getJSONObject(0)
                                when {
                                    data.getString("Roles") == "Facilitator" -> {
                                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Log In Succesfully!")
                                            .setContentText("You can proceed")
                                            .setConfirmText("Okay")
                                            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                                                val intent = Intent(this, Dashboard_Facilititor::class.java)
                                                intent.putExtra("TextBoxUser", username?.text.toString())
                                                startActivity(intent)
                                                this.finish()
                                            }
                                            .show()

                                    }
                                    data.getString("Roles") == "Student" -> {

                                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Log In Succesfully!")
                                            .setContentText("You can proceed")
                                            .setConfirmText("Okay")
                                            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                                                val intent = Intent(this, Dashboard_Student::class.java)
                                                intent.putExtra("TextBoxUser", username?.text.toString())
                                                startActivity(intent)
                                                this.finish()
                                            }
                                            .show()

                                    }
                                    else -> {
                                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Some error !").setContentText("The Roles is alien").show()
                                    }
                                }
                                //SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("The log in  is success!").setContentText("").show()
                        }
                        else if (obj.getString("message") == "Username or Password is incorrect" ) {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("You're not allowed to log in !").setContentText("The Password or Username is incorrect").show()
                        }
                        else if (user.isNullOrBlank() || pass.isNullOrBlank() ) {
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Some input is missing!").setContentText("Please input the username and password").show()
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

                    params["Username"] = user
                    params["Passwords"] = pass
                    return params
                }
            }
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        }


    }

}