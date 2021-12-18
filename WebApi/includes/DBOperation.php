<?php

class DBOperation
{
    private $con;

    function __construct()
    {
        require_once dirname(__FILE__).'/DBConnect.php';
        $db = new DBConnect;
        $this->con = $db->connect();
    }

    //adding record database
    public function createFaciStu($username, $fullname, $password, $email, $role)
    {
        $check = 'a';
        $stmt = $this->con->prepare("INSERT INTO FaciStu_tbl(Username, Fullname, Passwords, Email, Roles) Values(?, ?, ?, ?, ?)");
        $stmt->bind_param("sssss", $username, $fullname, $password, $email, $role);

        $chkEmail="select * from FaciStu_tbl where  Email='$email'";
        $resEmail=mysqli_query($this->con,$chkEmail);
        $rowEmail = mysqli_fetch_assoc($resEmail);

        $chkUsername="select * from FaciStu_tbl where  Username='$username'";
        $resUsername=mysqli_query($this->con,$chkUsername);
        $rowUsername = mysqli_fetch_assoc($resUsername);
        
        if($email==$rowEmail['Email'])
        {
            $check = 'b';
        }
        else if($username==$rowUsername['Username'])
        {
            $check = 'u';
        }
        else if ($stmt->execute())
        {
            $check = 'c';
        }
        return $check;
    }

    //fetching record to database
    public function getFaciStu()
    {
       $stmt = $this->con->prepare("SELECT id, Username, Email, Passwords  FROM FaciStu_tbl");
        $stmt->execute();
        $stmt->bind_result($id, $username, $email, $password);
        $faciStu = array();

        while($stmt->fetch())
        {
            $temp = array();
            $temp ['id'] = $id;
            $temp['Username'] = $username;
            $temp['Email'] = $email;
            $temp['Password'] = $password;
            array_push($faciStu, $temp); 
        }  
        return $faciStu;
    }
    //In log in
    public function loginCheck($user, $pass)
    {
        
        $stmt = "SELECT * FROM FaciStu_tbl WHERE Username = '$user' AND Passwords = '$pass'";
        $result = mysqli_query($this->con,$stmt); 
        
        $row = mysqli_fetch_array($result, MYSQLI_ASSOC);  
        $count = mysqli_num_rows($result); 
        if($count == 1){ 
            return true;  
        }   
        return false;

    }
    //Displaying fullname, email, and role from database
   public function displayInfo_1($username)
    {
       $stmt = $this->con->prepare("SELECT Fullname, Email, Roles FROM  FaciStu_tbl WHERE Username = '$username'");
        $stmt->execute();
        $stmt->bind_result($fullname, $email, $role);
        $faciStu = array();
        
        while($stmt->fetch()){
            $temp = array(); 
            $temp['Fullname'] = $fullname; 
            $temp['Email'] = $email; 
            $temp['Roles'] = $role; 
            array_push($faciStu, $temp);
        }
        return $faciStu; 
    }

    //updateInfo
   public function updatingInfo($user, $full, $pass)
    {
        
        $this->con->query("UPDATE FaciStu_tbl SET Fullname = '$full', Passwords = '$pass' WHERE Username='$user'");
        $checkUpdate = 'y';
        
        return $checkUpdate;
       
    }

    //uploading profile pic
    
    public function profilePic ($user, $photo)
    {
        $path = "profile_image/$user.jpeg";
        $finalPath = "https://developers.aris-gail.com/21221ST_CS3101_G3/public_html/ProfileImage/".$path;

        $ftp_server = "ftp.aris-gail.com";
        $ftp_user_name = "cs3101g3@developers.aris-gail.com";
        $ftp_user_pass = "cs3101g3";
        $destination_file = "ftp://cs3101g3%2540developers.aris-gail.com@ftp.aris-gail.com/public_html/ProfileImage/";
        //$source_file = $_POST['file']['tmp_name'];

        // set up basic connection
        $conn_id = ftp_connect($ftp_server);
        ftp_pasv($conn_id, true); 

        // login with username and password
        $login_result = ftp_login($conn_id, $ftp_user_name, $ftp_user_pass);
    
        // upload the file
        $upload = ftp_put($conn_id, $destination_file, $finalPath, FTP_BINARY); 

    
        $sql = "UPDATE FaciStu_tbl SET ProfilePic = '$finalPath' WHERE Username='$user'";
        //UPDATE FaciStu_tbl SET ProfilePic WHERE Username='$user'
    
        if (mysqli_query($this->con, $sql) && file_put_contents( $path, base64_decode($photo)) 
        &&ftp_connect($ftp_server) && $login_result && $upload) {
            
          return true;
    
        }
        return false;
    }

    //create event
    public function createEvent($email, $eventName, $eventDescipt, $eventCode, $eventDateStart, $eventTimeStart, $eventTimeEnd)
    {
        $check = 'a';
        $stmt = $this->con->prepare("INSERT INTO Event_tbl(EventCode, EventName, EventDesc, FacilitatorId,
        EventDateFrom, EventTimeStart, EventTimeEnd) Values(?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssssss", $eventCode, $eventName, $eventDescipt, $email, $eventDateStart, $eventTimeStart, $eventTimeEnd);
    
        if ($stmt->execute())
        {
            $check = 'c';
        }
        return $check;
    }

    //viewing event created
    public function viewEvent($email)
    {
        $stmt = $this->con->prepare("SELECT EventName, EventDesc, EventCode, EventDateFrom, EventTimeStart,
        EventTimeEnd FROM Event_tbl WHERE FacilitatorId = '$email'");
        $stmt->execute();
        $stmt->bind_result($eventName, $eventDescrip, $eventCode, $eventDate, $eventTimeStart, $eventTimeEnd);
        $event = array();
        
        while($stmt->fetch()){
            $temp = array(); 
            $temp['EventName'] = $eventName; 
            $temp['EventDesc'] = $eventDescrip;
            $temp['EventCode'] = $eventCode;
            $temp['EventDateFrom'] = $eventDate;
            $temp['EventTimeStart'] = $eventTimeStart;
            $temp['EventTimeEnd'] = $eventTimeEnd;
           
            array_push($event, $temp);
        }
        return $event; 
    }

    //view event by eventCode
    public function viewEventByCode($eventCode)
    {
        $stmt = $this->con->prepare("SELECT EventName, EventDesc, EventCode, EventDateFrom, EventTimeStart,
        EventTimeEnd, FacilitatorId FROM Event_tbl WHERE EventCode = '$eventCode'");
        $stmt->execute();
        $stmt->bind_result($eventName, $eventDescrip, $eventCodeSet, $eventDate, $eventTimeStart, $eventTimeEnd, $faciId);
        $event = array();
        
        while($stmt->fetch()){
            $temp = array(); 
            $temp['EventName'] = $eventName; 
            $temp['EventDesc'] = $eventDescrip;
            $temp['EventCode'] = $eventCodeSet;
            $temp['EventDateFrom'] = $eventDate;
            $temp['EventTimeStart'] = $eventTimeStart;
            $temp['EventTimeEnd'] = $eventTimeEnd;
            $temp['FacilitatorId'] = $faciId;
           
            array_push($event, $temp);
        }
        return $event; 
    }
    //attending
    public function attendance($eventCode, $studentId)
    {
        $checkAttendance = 'a';
        $stmt_1 = $this->con->prepare("INSERT INTO EventStudent_tbl(StudentId, EventId) Values( ?, (SELECT EventId FROM Event_tbl WHERE EventCode = '$eventCode'))");
        $stmt_1->bind_param("s", $studentId);

        $chkEvent="SELECT *  FROM (EventStudent_tbl INNER JOIN Event_tbl ON Event_tbl.EventId = EventStudent_tbl.EventId) WHERE
        (EventStudent_tbl.StudentId = '$studentId' AND Event_tbl.EventCode = '$eventCode')";
        $resEvent=mysqli_query($this->con,$chkEvent);
        //$rowEvent = mysqli_fetch_assoc($resEvent);
        $numrows=mysqli_num_rows($resEvent);
       
        if($numrows > 0)
        {
            $checkAttendance = 'c';
        }
        else if($stmt_1->execute())
        {
            $checkAttendance = 'b';
        }
        return $checkAttendance;
    }

    //display who attend for faci
    public function viewAttend($eventCode)
    {
        $stmt = $this->con->prepare("SELECT FaciStu_tbl.Fullname, FaciStu_tbl.Email FROM ((EventStudent_tbl INNER JOIN FaciStu_tbl
        ON FaciStu_tbl.Email = EventStudent_tbl.StudentId) INNER JOIN Event_tbl ON Event_tbl.EventId = EventStudent_tbl.EventId) WHERE Event_tbl.EventCode = '$eventCode'");
        $stmt->execute();
        $stmt->bind_result($stuName, $stuEmail);
        $event = array();
        
        while($stmt->fetch()){
            $temp = array(); 
            $temp['FaciStu_tbl.Fullname'] = $stuName; 
            $temp['FaciStu_tbl.Email'] = $stuEmail;
            
            array_push($event, $temp);
        }
        return $event; 
    }

    //display who attending event/recent for stu
    public function recentAttended($student)
    {
        $stmt = $this->con->prepare("SELECT Event_tbl.EventName, Event_tbl.FacilitatorId, Event_tbl.EventDateFrom FROM Event_tbl INNER JOIN  EventStudent_tbl
        ON Event_tbl.EventId = EventStudent_tbl.EventId WHERE  EventStudent_tbl.StudentId = '$student'");
        $stmt->execute();
        $stmt->bind_result($eventName, $faci, $date);
        $event = array();
        
        while($stmt->fetch()){
            $temp = array(); 
            $temp['Event_tbl.EventName'] = $eventName; 
            $temp['Event_tbl.FacilitatorId'] = $faci;
            $temp['Event_tbl.EventDateFrom'] = $date;
            
            array_push($event, $temp);
        }
        return $event; 
    }

}


?>