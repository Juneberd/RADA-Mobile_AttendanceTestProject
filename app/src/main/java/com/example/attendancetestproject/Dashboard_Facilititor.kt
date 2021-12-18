package com.example.attendancetestproject


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import coil.load
import coil.transform.CircleCropTransformation
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_dashboard_facilititor.*
import kotlinx.android.synthetic.main.activity_dashboard_student.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.option_get_image.*
import kotlinx.android.synthetic.main.option_get_image.view.*
import kotlinx.android.synthetic.main.resultget_img.view.*
import kotlinx.android.synthetic.main.signup_facilitator.view.*
import kotlinx.android.synthetic.main.signup_student.view.*
import kotlinx.android.synthetic.main.updateinfo_facistu.*
import kotlinx.android.synthetic.main.updateinfo_facistu.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Dashboard_Facilititor : AppCompatActivity() {

    private var username = ""
    private var u_txtFullname: EditText? = null
    private var u_txtPassword: EditText? = null
    private var u_txtConPassword: EditText? = null


    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private var EMAILING: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_facilititor)

        val i = intent
        var usertext = i.getStringExtra("TextBoxUser")
        username = usertext.toString()

        popUpMenuUpdate(username)

        val sdf = SimpleDateFormat("E, dd MMM yyyy")
        val currentDate = sdf.format(Date())
        viewDate_F.setText(currentDate)


        //textViewF.setText(username)

        display_1(username)

        viewEvent()

        createEv()


    }

    private fun display_1(u:String){
       val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_DISPLAYINFO_1_FACISTU,
            Response.Listener<String> { response ->
                try {
                    val jsonobject = JSONObject(response)
                    val jsonArray = jsonobject.getJSONArray("FaciStu_tbl")
                    val data: JSONObject = jsonArray.getJSONObject(0)

                    val fname = data.getString("Fullname")
                    val email = data.getString("Email")
                    val roles = data.getString("Roles")

                    textViewF.setText(fname.toString() +"\n" + email.toString() +"\n" + roles.toString())

                    edtIn_EmailView.setText(email.toString())
                    EMAILING = edtIn_EmailView



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

                params["Username"] = u

                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun popUpMenuUpdate(user:String) {

        val popupMenu = PopupMenu(applicationContext, imgBtn_EditProfile)
        popupMenu.inflate(R.menu.editprofile_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editproPic -> {
                    val  mOptionImageLay = LayoutInflater.from(this).inflate(R.layout.option_get_image, null)
                    val mBuilder_Image = AlertDialog.Builder(this)
                        .setView(mOptionImageLay)
                    val showBuild = mBuilder_Image.show()


                    mOptionImageLay.btnCamera.setOnClickListener{
                        cameraCheckPermission()
                        showBuild.dismiss()


                    }
                    mOptionImageLay.btnGallery.setOnClickListener{
                        galleryCheckPermission()
                        showBuild.dismiss()
                    }



                    true

                }
                R.id.editInfo -> {
                    val mDialogView_U =
                        LayoutInflater.from(this).inflate(R.layout.updateinfo_facistu, null)
                    val mBuilder_U = AlertDialog.Builder(this)
                        .setView(mDialogView_U)
                    val mAlertDialog = mBuilder_U.show()


                    mDialogView_U.xbtn_cancel.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    //update info button
                    mDialogView_U.btnF_update.setOnClickListener {
                        /*Toast.makeText(applicationContext, user , Toast.LENGTH_LONG)
                            .show()*/
                        u_txtFullname=mDialogView_U.update_FtxtEditFullname
                        u_txtPassword=mDialogView_U.update_FtxtEditPassword
                        u_txtConPassword=mDialogView_U.update_FtxtEditConfirmPassword

                        val fname = u_txtFullname?.text.toString()

                        val pass = u_txtPassword?.text.toString()
                        val conPass = u_txtConPassword?.text.toString()

                        if(fname.isNullOrBlank()  || pass.isNullOrBlank() || conPass.isNullOrBlank()){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Required Parameter Missing!").setContentText("Input requirement completely").show()
                        }
                        else if(pass != conPass){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("The Password is Mismatch!").setContentText("Input password and confirm password again").show()
                        }
                        else {
                           //creating volley string request
                               //update info with JSON Volley
                           val stringRequest = object :
                               StringRequest(Request.Method.POST, EndPoints.URL_UPDATEINFO,
                                   Response.Listener<String> { response ->
                                       try {
                                           val obj = JSONObject(response)

                                           if (obj.getString("message") == "Required parameter missing") {
                                               SweetAlertDialog(
                                                   this,
                                                   SweetAlertDialog.WARNING_TYPE
                                               ).setTitleText("Required Parameter Missing!")
                                                   .setContentText("Input requirement completely")
                                                   .show()
                                           } else if(obj.getString("message") == "The email is already taken"){
                                               SweetAlertDialog(
                                                   this,
                                                   SweetAlertDialog.WARNING_TYPE
                                               ).setTitleText("The Email is already taken!")
                                                   .setContentText("Type email again").show()
                                           } else if (obj.getString("message") == "Updating in Succesfully") {
                                               SweetAlertDialog(
                                                   this,
                                                   SweetAlertDialog.SUCCESS_TYPE
                                               ).setTitleText("The updating account is Succesfully!")
                                                   .setContentText("You need log in again").show()
                                               val intent = Intent(this, MainActivity::class.java)
                                               startActivity(intent)
                                           }
                                           //else not updating
                                           else {
                                               SweetAlertDialog(
                                                   this,
                                                   SweetAlertDialog.ERROR_TYPE
                                               ).setTitleText("Could not update!")
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
                                   params["Username"] = user
                                   params["Fullname"] = fname
                                   params["Passwords"] = pass

                                   return params
                               }
                           }
                           //adding request to queue
                           VolleySingleton.instance?.addToRequestQueue(stringRequest)
                       }
                    }

                    true
                }
                R.id.Signout->{

                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to sign out?")
                        .setContentText("")
                        .setConfirmText("Sign out!")
                        .setConfirmClickListener { sDialog ->
                            sDialog
                                .setTitleText("Log out!")
                                .setContentText("")
                                .setConfirmText("OK")
                                .setConfirmClickListener{
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        }
                        .show()

                    true
                }
                else -> {
                    true
                }

            }
        }

        imgBtn_EditProfile.setOnClickListener {
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
    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val promptView: View = layoutInflater.inflate(R.layout.resultget_img, null)
            val imgViewResult = promptView.findViewById(R.id.imgViewGet) as ImageView

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)

                    imgViewResult.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }

                }

               GALLERY_REQUEST_CODE -> {

                   imgViewResult.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                   }


                }
            }
            val alertDialogBuilderPic = AlertDialog.Builder(this)
                .setView(promptView)
            val dismissBtn = promptView.xbtnImg
            val winPop = alertDialogBuilderPic.show()
            winPop.show()

            promptView.btnSaveImg.setOnClickListener{
                when (requestCode) {

                    CAMERA_REQUEST_CODE -> {

                        val bitmap = data?.extras?.get("data") as Bitmap

                        //we are using coroutine image loader (coil)
                        imageView_Result.load(bitmap) {
                            crossfade(true)
                            crossfade(1000)
                            transformations(CircleCropTransformation())
                        }
                    }

                    GALLERY_REQUEST_CODE -> {
                        imageView_Result.load(data?.data) {
                            crossfade(true)
                            crossfade(1000)
                            transformations(CircleCropTransformation())
                        }

                    }
                }
                winPop.dismiss()
            }

            dismissBtn.setOnClickListener{
                winPop.dismiss()
            }

        }

    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@Dashboard_Facilititor,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }



    private fun viewEvent() {
        btnEventList.setOnClickListener {
            EMAILING = edtIn_EmailView

            val email = EMAILING?.text.toString()
            val intent = Intent(this, activity_FaciEvent::class.java)
            intent.putExtra("TextBoxEmail", email)
            startActivity(intent)

        }
    }

    private fun createEv(){
        btnCreatingEvent.setOnClickListener {
            EMAILING = edtIn_EmailView
            /*Toast.makeText(applicationContext, EMAILING?.text.toString() , Toast.LENGTH_LONG)
                .show()*/
            val intent = Intent(this, CreateEvent::class.java)
            intent.putExtra("TextBoxEmail", EMAILING?.text.toString())
            startActivity(intent)

        }
    }




}


