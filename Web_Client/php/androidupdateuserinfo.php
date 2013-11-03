<?php
	$message = "success";
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
    $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die($message = "connect to sql fail");
    mysql_select_db('ezride') or die($message = "Select DB ezride fail");
    
    $username = $_POST['username'];
	$password = $_POST['password'];
    $name = $_POST['name'];
    $email = $_POST['email'];
    $phonenumber = $_POST['phonenumber'];
    $address = $_POST['address'];
    $profile = $_POST['profile'];
    
    $query = "SELECT * FROM userinfo WHERE username='" . $username . "' AND password='" . sha1($password) . "'";
    $result = mysql_query($query) or die($message = "query fail" . mysql_error());
    if(mysql_num_rows($result) == 0){
        $message = "Invalid username or password";
    } else {
        $query_update = "UPDATE userinfo SET name='" . $name .
                        "', email='" . $email .
                        "', phonenumber='" . $phonenumber .
                        "', address='" . $address .
                        "', profile='" . $profile .
                        "' WHERE username='" . $username . "' AND password='" . sha1($password) . "'";
        mysql_query($query_update) or die($message = "update fail" . mysql_error());
    }
echo $message;
?>