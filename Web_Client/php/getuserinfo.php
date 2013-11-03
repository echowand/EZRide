<?php
    $username = $_POST['username'];
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
    $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail' . mysql_error());
    mysql_select_db('ezride') or die('Select DB ezride fail.');
    $query = "SELECT * FROM userinfo WHERE username='" . $username . "'";
    $result = mysql_query($query) or die('query fail:' . mysql_error());
    $row = mysql_fetch_array($result, MYSQL_ASSOC);
    if(mysql_num_rows($result) == 0){
        echo "fail to find user";
    }else{
    echo '####name:' . $row['name'] . '\n' .
         '####email:' . $row['email'] . '\n' .
         '####phonenumber:' . $row['phonenumber'] . '\n' .
         '####address' . $row['address'] . '\n' .
         '####profile' . $row['profile'];
    }
    
?>
