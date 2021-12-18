package com.example.attendancetestproject

object EndPoints {
    private val URL_ROOT = "https://developers.aris-gail.com/21221ST_CS3101_G3/public_html/WebApi/v1/?op="
    val URL_ADD_FACISTU = URL_ROOT + "addfaciStu"
    val URL_GET_FACISTU = URL_ROOT + "getFaciStu"
    val URL_LogInCheck_FACISTU = URL_ROOT + "checklogIn"
    val URL_DISPLAYINFO_1_FACISTU = URL_ROOT + "displayInfo_1"
    val URL_UPDATEINFO = URL_ROOT + "updateAccount"
    val URL_CREATE_EVENT = URL_ROOT + "createEvent"
    val URL_VIEWFACI_EVENT = URL_ROOT + "displayEvent"
    val URL_VIEWFACI_EVENTBYCODE = URL_ROOT + "displayEventByCode"
    val URL_ATTENDANCE = URL_ROOT + "Attend"
    val URL_ATTENDLIST = URL_ROOT + "AttendList"
    val URL_RECENT_ATTENDED = URL_ROOT + "displaylistAttended"
}