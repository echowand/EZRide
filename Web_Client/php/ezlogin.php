<?php
    session_start();

    /*** check if the users is already logged in ***/
    if(isset( $_SESSION['user_id'] ))
    {
        $message = 'Users is already logged in';
    }
    /*** check that both the username, password have been submitted ***/
    if(!isset( $_POST['phpro_username'], $_POST['phpro_password']))
    {
        $message = 'Please enter a valid username and password';
    }

    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
    $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail');
    mysql_select_db('ezride') or die('Select DB ezride fail.');
    $username = $_POST['username'];
    $password = $_POST['password'];
    $password = sha1( $password );
    $query = "SELECT * FROM userinfo WHERE username='" . $username . "'";
    $result = mysql_query($query) or die('ezlogin query fail');
    $row = mysql_fetch_array($result);
    if(mysql_num_rows($result) == 0){
        echo "fail to find user";
    }else{
        if($password == $row['password']){
            $_SESSION['user_id'] = $row['id'];
            //echo $_SESSION['user_id'];
            echo "You are now logged in. Redirecting...";
            header('Location: http://ezride-weiqing.rhcloud.com');

        }
        else{
            echo "wrong password";
        }
    }




?>

