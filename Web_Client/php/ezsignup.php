<?php
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
    $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail');
    mysql_select_db('ezride') or die('Select DB ezride fail.'); 
    $username = $_POST['username'];
    $password = $_POST['password'];
    $email = $_POST['email'];
    $profile = $_POST['profile'];
    
    $password = sha1( $password );

    $query = "SELECT * FROM userinfo WHERE username='" . $username . "'";
    $result = mysql_query($query) or die('query fail');

    
    if(mysql_num_rows($result) == 0){
        $query_insert = "INSERT INTO userinfo (username, password, email, profile) VALUES ('" . $username . "', '" . $password . "','" . $email . "','" . $profile . "')" or die('insert fail');
        mysql_query($query_insert) or die('Insert to userinfo failed');
        echo "success";
        header('Location: http://ezride-weiqing.rhcloud.com');
    }else{
        echo "user already exist";
    }


?>
