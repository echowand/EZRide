<?php 
   define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
 
   define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
   
   define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
   
   define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
   

 
  if(@mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD)){
    if(mysql_select_db(DB_DATABASE)){
    } 
  }else{
    die(mysql_error());
  }

  $username=$_POST["username"];
  $query=mysql_query("SELECT * from userinfo where username='$username' ");
  $find=mysql_num_rows($query);
  echo $find;
  mysql_close();
?>