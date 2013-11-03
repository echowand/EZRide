
<?php
/*** begin our session ***/
//session_start();
echo "adding";
/*** first check that both the username, password and form token have been sent ***/
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
    $message = 'Incorrect Length for Username';
}
/*** check the password is the correct length ***/
elseif (strlen( $_POST['password']) > 32 || strlen($_POST['password']) < 4)
{
    $message = 'Incorrect Length for Password';
}

else
{
    /*** if we are here the data is valid and we can insert it into database ***/
    $ezride_username = filter_var($_POST['username'], FILTER_SANITIZE_STRING);
    $ezride_password = filter_var($_POST['password'], FILTER_SANITIZE_STRING);
    $ezride_email = filter_var($_POST['email'], FILTER_SANITIZE_STRING);

    /*** now we can encrypt the password ***/
    $ezride_password = sha1( $ezride_password );
    
    /*** connect to database ***/
    /*** mysql hostname ***/
    define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
 
    define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
   
    define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
   
    define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );

    try
    {
        $dbh = new PDO("mysql:host=DB_SERVER;dbname=DB_DATABASE", DB_SERVER,DB_USER,DB_PASSWORD);
        /*** $message = a message saying we have connected ***/

        /*** set the error mode to excptions ***/
        $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        /*** prepare the insert ***/
        $stmt = $dbh->prepare("INSERT INTO userinfo (username, password ) VALUES (:ezride_username, :ezride_password )");

        /*** bind the parameters ***/
        $stmt->bindParam(':ezride_username', $ezride_username, PDO::PARAM_STR);
        $stmt->bindParam(':ezride_password', $ezride_password, PDO::PARAM_STR, 40);

        /*** execute the prepared statement ***/
        $stmt->execute();

        /*** unset the form token session variable ***/
        //unset( $_SESSION['form_token'] );

        /*** if all is done, say thanks ***/
        $message = 'New user added';

    }
    catch(Exception $e)
    {
        /*** check if the username already exists ***/
        if( $e->getCode() == 23000)
        {
            $message = 'Username already exists';
        }
        else
        {
            /*** if we are here, something has gone wrong with the database ***/
            $message = 'We are unable to process your request. Please try again later"';
        }
    }
}

echo $message;
header('Location: http://ezride-weiqing.rhcloud.com');
?>