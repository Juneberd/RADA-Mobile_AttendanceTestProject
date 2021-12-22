<?php

//adding DBOperation file
require_once '../includes/DBOperation.php';

//response array
$response = array();

//generate random for code event
function crypto_rand_secure($min, $max)
{
    $range = $max - $min;
    if ($range < 1) return $min; // not so random...
    $log = ceil(log($range, 2));
    $bytes = (int) ($log / 8) + 1; // length in bytes
    $bits = (int) $log + 1; // length in bits
    $filter = (int) (1 << $bits) - 1; // set all lower bits to 1
    do {
        $rnd = hexdec(bin2hex(openssl_random_pseudo_bytes($bytes)));
        $rnd = $rnd & $filter; // discard irrelevant bits
    } while ($rnd > $range);
    return $min + $rnd;
}

function getToken($length)
{
    $token = "";
    $codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    $codeAlphabet.= "abcdefghijklmnopqrstuvwxyz";
    $codeAlphabet.= "0123456789";
    $max = strlen($codeAlphabet); // edited

    for ($i=0; $i < $length; $i++) {
        $token .= $codeAlphabet[crypto_rand_secure(0, $max-1)];
    }

    return $token;
}

//if a get parameter named op is set we will consider it is an api call
if(isset($_GET['op']))
{
    //switching the get op value
    switch($_GET['op'])
    {
        //if it is add faciStu
        //that mean we will add
        case 'addfaciStu':
            if(!empty($_POST['Username']) && !empty($_POST['Fullname']) && !empty($_POST['Passwords']) && !empty($_POST['Email']) && !empty($_POST['Roles']))
            {
                $db = new DBOperation();
                if($db->createFaciStu($_POST['Username'], $_POST['Fullname'], $_POST['Passwords'], $_POST['Email'], $_POST['Roles'])=='c')
                {
                    $response['error'] = false;
                    $response['message'] = 'Sign Up is succesfully';
                }
                else if($db->createFaciStu($_POST['Username'], $_POST['Fullname'], $_POST['Passwords'], $_POST['Email'], $_POST['Roles'])=='b')
                {
                    $response['error'] = true;
                    $response['message'] = 'The Email is already taken';
                }
                else if($db->createFaciStu($_POST['Username'], $_POST['Fullname'], $_POST['Passwords'], $_POST['Email'], $_POST['Roles'])=='u')
                {
                    $response['error'] = true;
                    $response['message'] = 'The Username is already taken';
                }
                else{
                    $response['error'] = true;
                    $response['message'] = 'Could not sign up';
                }
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
           
        break;

        //if getfaciStu means featch record
        case 'getfaciStu':
            $db = new DBOperation();
            $faciStu = $db->getFaciStu();
            if(count($faciStu)<=0)
            {
                $response['error'] = true;
                $response['message'] = 'Nothing found database';
            }
            else
            {
                $response['error'] = false;
                $response['faciStu'] = $faciStu;
            }
        break;
        
        //check record for login
        case 'checklogIn':
            if(!empty($_POST['Username']) && !empty($_POST['Passwords']))
            {
                
                $db = new DBOperation();
                if($db->loginCheck($_POST['Username'], $_POST['Passwords']))
                {
                    $response['error'] = false;
                    $response['message'] = 'Log in Succesfully';

                   $db1 = new DbOperation();
                    $faciStu = $db1->displayInfo_1($_POST['Username']);
                    if(count($faciStu)<=0){
                        $response['error'] = true; 
                        $response['message'] = 'Nothing found in the database';
                    }
                    else
                    {
                        $response['FaciStu_tbl'] = $faciStu;
                    }

                }else{
                    $response['error'] = true;
                    $response['message'] = 'Username or Password is incorrect';
                }
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
            
        break;

        //displaying info using username
       case 'displayInfo_1':
            if(!empty($_POST['Username']))
            {
                $db = new DbOperation();
                $faciStu = $db->displayInfo_1($_POST['Username']);
                if(count($faciStu)<=0){
                    $response['error'] = true; 
                    $response['message'] = 'Nothing found in the database';
                }
                else
                {
                    $response['error'] = false; 
                    $response['FaciStu_tbl'] = $faciStu;
                }
                
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        
        break;
        
        //updating account
       case 'updateAccount':
            if(!empty($_POST['Username']) && !empty($_POST['Fullname']) && !empty($_POST['Passwords']))
            {
                $db = new DBOperation();
               
                if ($db->updatingInfo($_POST['Username'], $_POST['Fullname'], $_POST['Passwords'])=='y')
                {
                    $response['error'] = false;
                    $response['message'] = 'Updating in Succesfully';
                }
                else
                {
                    $response['error'] = true;
                    $response['message'] = 'Could not update';
                }
            }
            else
            {
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        break;

        //uploading photo
        case 'UploadPhoto':
            if(!empty($_POST['Username']) && !empty($_POST['ProfilePic']))
            {
                $db = new DBOperation();
                if($db->profilePic($_POST['Username'], $_POST['ProfilePic'])===true)
                {
                    $response['error'] = false;
                    $response['message'] = 'Uploading picture Succesfully';
                }
                else
                {
                    $response['error'] = true;
                    $response['message'] = 'The uploading picture is not successful';
                }
            }
            else
            {
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }

        
        break;

        case 'createEvent':
            if(!empty($_POST['EventName']) && !empty($_POST['EventDescrip']) && !empty($_POST['Email']))
            {
                $db = new DBOperation();
                if($db->createEvent($_POST['Email'], $_POST['EventName'], $_POST['EventDescrip'], getToken(11),
                $_POST['EventDate'], $_POST['TimeStart'], $_POST['TimeEnd'])=='c')
                {
                    $response['error'] = false;
                    $response['message'] = 'Created event is succesfully';
                }
                else
                {
                    $response['error'] = true;
                    $response['message'] = 'Could not create';
                }
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
           
        break;

        case 'displayEvent':
            if(!empty($_POST['FacilitatorId']))
            {
                $db = new DbOperation();
                $event = $db->viewEvent($_POST['FacilitatorId']);
                if(count($event)<=0){
                    $response['error'] = true; 
                    $response['message'] = 'Nothing found in the database';
                }
                else
                {
                    $response['error'] = false; 
                    $response['Event_tbl'] = $event;
                }
                
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        
        break;

        case 'displayEventByCode':
            if(!empty($_POST['EventCode']))
            {
                $db = new DbOperation();
                $event = $db->viewEventByCode($_POST['EventCode']);
                if(count($event)<=0){
                    $response['error'] = true; 
                    $response['message'] = 'Nothing found in the database';
                }
                else
                {
                    $response['error'] = false; 
                    $response['Event_tbl'] = $event;
                }
                
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        
        break;

        case 'Attend':
            if(!empty($_POST['StudentEmail']) && !empty($_POST['EventCode']))
            {
                $db = new DBOperation();
                if($db->attendance($_POST['EventCode'], $_POST['StudentEmail'])=='c')
                {
                    $response['error'] = true;
                    $response['message'] = 'Already attend';
                }
                else if($db->attendance($_POST['EventCode'], $_POST['StudentEmail'])=='b')
                {
                    $response['error'] = true;
                    $response['message'] = 'Could not attend';
                }
                else
                {
                    $response['error'] = false;
                    $response['message'] = 'Attending is succesfully';
                }
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
           
        break;

        //display who attend
        case 'AttendList':
            if(!empty($_POST['EventCode']))
            {
                $db = new DbOperation();
                $event = $db->viewAttend($_POST['EventCode']);
                if(count($event)<=0){
                    $response['error'] = true; 
                    $response['message'] = 'Nothing found in the database';
                }
                else
                {
                    $response['error'] = false; 
                    $response['Event_tbl'] = $event;
                }
                
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        
        break;

        case 'displaylistAttended':
            if(!empty($_POST['StudentEmail']))
            {
                $db = new DbOperation();
                $event = $db->recentAttended($_POST['StudentEmail']);
                if(count($event)<=0){
                    $response['error'] = true; 
                    $response['message'] = 'Nothing found in the database';
                }
                else
                {
                    $response['error'] = false; 
                    $response['Event_tbl'] = $event;
                }
                
            }
            else{
                $response['error'] = true;
                $response['message'] = 'Required parameter missing';
            }
        
        break;

        default:
            $response['error'] = true; 
            $response['message'] = 'No Operation to perform';

    }
}
else
{
    $response['error'] = true; 
    $response['message'] = 'Invalid request';
}

//displaying data on json
echo json_encode($response);
?>