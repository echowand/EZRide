<?php

/*** begin the session ***/
session_start();

if(!isset($_SESSION['user_id']))
{
    $message = 'You must be logged in to access this page';
    
}
else
{
    try
    {
        /*** connect to database ***/
       
      define( "DB_SERVER",    getenv('OPENSHIFT_MYSQL_DB_HOST') );
      define( "DB_USER",      getenv('OPENSHIFT_MYSQL_DB_USERNAME') );
      define( "DB_PASSWORD",  getenv('OPENSHIFT_MYSQL_DB_PASSWORD') );
      define( "DB_DATABASE",  getenv('OPENSHIFT_APP_NAME') );
      $link = mysql_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die('connect to sql fail');
      mysql_select_db('ezride') or die('Select DB ezride fail.');  
      $query = "SELECT * FROM userinfo WHERE id='" . $_SESSION['user_id'] . "'";
      $result = mysql_query($query) or die('ezlogin query fail');
      $row = mysql_fetch_array($result);
      if(mysql_num_rows($result) == 0){
          echo "fail to find user";
      }else{
              $ezuser_username = $row['username'];
      }
      


    }
    catch (Exception $e)
    {
        /*** if we are here, something is wrong in the database ***/
        $message = 'We are unable to process your request. Please try again later"';
    }
}

?>


<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="ico/favicon.png">

    <title>EZRide!</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="nav.css" rel="stylesheet">
    
    <script src="js/jquery-2.0.3.min.js"></script>
    <!-- Custom styles for this template -->
    
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

  <div id="fb-root"></div>
       
  <?php include 'header.php'; ?>
    

  <?php if ($user || $ezuser_username): ?>
  
    <div class="container main " style="padding-top:53px; height:100%;">
      <!-- Example row of columns -->
     <div class="row">
      <div class="col-md-3">

        <div class="bs-sidebar hidden-print affix-top" role="complementary">
         
         <ul class="nav bs-sidenav">
            <li class="side-li"><a class="active" href="#">All Groups</a></li>
            <li class="side-li"><a href="#">My Groups</a></li>
            <li class="side-li"><a href="#">Calendar</a></li>
            <li class="side-li"><a href="#">Nearby</a></li>
            <li class="side-li"><a href="profile.php">Profile</a></li>
            
          </ul>

        </div>
      </div>

       <div class="col-md-9">
        <div class="bs-sidebar hidden-print affix-top" role="complementary">
          <div class="list-group my-list">
            <a href="#" class="list-group-item">
              <span class="badge">10</span>
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">...</p>
            </a>
            <a href="#" class="list-group-item">
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">...</p>
            </a>
            <a href="#" class="list-group-item">
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">...</p>
            </a>
          </div>
        </div>

      </div>

     </div>
    
    </div>

  <?php else: ?>
  <div class="jumbotron my-jum">
      <div class="container info">
        <h1>Welcome To EZRide!</h1>
        <p>This is a social based carpooling app. You have a few sign in options. You can signin with Google+, Facebook or an EZRide account. You can always create an EZRide only aacount if you are not into the whole social networking thing.</p>

        <div class="social">
               <a href="<?php echo $loginUrl; ?>" class="facebook">
                <i class="entypo-facebook"></i><span>facebook</span></a>
                          
                <a href="#" id="customBtn" class="gplus customGPlusSignIn">
                    <i class="entypo-gplus"></i><span>google+</span></a>
            </div>  
            <hr>
            <center><span id="signup-text">Or you can</span></center>
            <center><a class="btn btn-lg btn-outline-inverse" href="signup.php"><span id="my-signup-btn">Sign up today</span></a></center>


        </div>
    </div>

  <div class="container main ">
      <!-- Example row of columns -->
      <div class="row">
         <div class="col-lg-7">
            <div class="project">
                <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
                <hr>
                <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          
            </div>
         </div>

        <div class="col-lg-5">
          <div class="about-me">
            
          </div>
          
         
        </div>

       
      </div>
      <br>
      <br>
       <footer>
        <p>&copy; EZRide 2013</p>
      </footer>
    
    </div> <!-- /container -->
    <?php endif?>
     
  

  


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
     <script type="text/javascript">
        (function() {
          var po = document.createElement('script');
          po.type = 'text/javascript'; po.async = true;
          po.src = 'https://apis.google.com/js/client:plusone.js?onload=render';
          var s = document.getElementsByTagName('script')[0];
          s.parentNode.insertBefore(po, s);
        })();

        function render() {
          gapi.signin.render('customBtn', {
            //'callback': 'signinCallback',
            'clientid': '29183140587.apps.googleusercontent.com',
            'cookiepolicy': 'single_host_origin',
            'requestvisibleactions': 'http://schemas.google.com/AddActivity',
            'scope': 'https://www.googleapis.com/auth/plus.login'
          });
        }
    </script>
    <script src="js/bootstrap.min.js"></script>
  </body>
  <?php if ($user || $ezuser_username): ?>
    <?php else: ?>
      <link href="home.css" rel="stylesheet">
    <?php endif?>
</html>
