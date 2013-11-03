
<?php
/*** begin our session ***/
//session_start();
//echo "adding";
/*** first check that both the username, password and form token have been sent ***/
$message = 'Something broke';
if(!isset( $_POST['username'], $_POST['password']))
{
    $message = 'Please enter a valid username and password';
}
/*** check the form token is valid ***/
/*elseif( $_POST['form_token'] != $_SESSION['form_token'])
{
    $message = 'Invalid form submission';
}*/
/*** check the username is the correct length ***/
elseif (strlen( $_POST['username']) > 20 || strlen($_POST['username']) < 4)
{
    $message = "Incorrect Length for Username";
}
/*** check the password is the correct length ***/
elseif (strlen( $_POST['password']) > 32 || strlen($_POST['password']) < 4)
{
    $message = "Incorrect Length for Password";
}

else
{
    /*** if we are here the data is valid and we can insert it into database ***/
    $ezride_username = filter_var($_POST['username'], FILTER_SANITIZE_STRING);
    $ezride_password = filter_var($_POST['password'], FILTER_SANITIZE_STRING);

    /*** now we can encrypt the password ***/
    $ezride_password = sha1( $ezride_password );
    
    /*** connect to database ***/
    /*** mysql hostname ***/
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
	
	$link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail');
	mysql_select_db('ezride') or die('Select DB ezride fail.');
	$query = "INSERT INTO userinfo (username, password) VALUES ('$ezride_username', '$ezride_password')";
	$result = mysql_query($query) or die($message = "query fail");
	if (strcmp($message, "query fail") != 0)
	{
		$message = "New user added";
	}
}

echo $message;
?>