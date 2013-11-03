<?php
    session_start();
    error_reporting(E_ALL);
    ini_set('display_errors','1');
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
    $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail');
    mysql_select_db('ezride') or die('Select DB ezride fail.');
    
    $username = $_POST['username'];
    $name = $_POST['name'];
    $email = $_POST['email'];
    $phonenumber = $_POST['phonenumber'];
    $address = $_POST['address'];
    $profile = $_POST['profile'];
    
    $query = "SELECT * FROM userinfo WHERE username='" . $username . "'";
    $result = mysql_query($query) or die('query fail' . mysql_error());
    if(mysql_num_rows($result) == 0){
        echo 'User does not exist';
    }else{
        $query_update = "UPDATE userinfo SET name='" . $name .
                        "', email='" . $email .
                        "', phonenumber='" . $phonenumber .
                        "', address='" . $address .
                        "', profile='" . $profile .
                        "' WHERE username='" . $username . "'";
        mysql_query($query_update) or die('update fail' . mysql_error());
        echo 'success';
        $_SESSION['update'] = $username;
        header('Location: http://ezride-weiqing.rhcloud.com/profile.php');
    }
    
?>