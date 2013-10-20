<?php


require 'src/facebook.php';

// Create our Application instance (replace this with your appId and secret).
$facebook = new Facebook(array(
  'appId'  => '542324812506927',
  'secret' => 'f21110855e1edbfbd541c4cfa373df18',
));

// Get User ID
$user = $facebook->getUser();

// We may or may not have this data based on whether the user is logged in.
//
// If we have a $user id here, it means we know the user is logged into
// Facebook, but we don't know if the access token is valid. An access
// token is invalid if the user logged out of Facebook.

if ($user) {
  try {
    // Proceed knowing you have a logged in user who's authenticated.
    $user_profile = $facebook->api('/me');
    $friends = $facebook->api('/me/friends');
    
    
  } catch (FacebookApiException $e) {
    error_log($e);
    $user = null;
  }
}

// Login or logout url will be needed depending on current user state.
if ($user) {
  $logoutUrl = $facebook->getLogoutUrl();
} else {
  $loginUrl = $facebook->getLoginUrl(array('scope' => 'publish_stream, email,friends_about_me,friends_education_history,friends_hometown,friends_location,friends_work_history,read_stream'));
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

    <!-- Custom styles for this template -->
    <link href="signin.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>
  <div id="fb-root"></div>
       
  <?php if ($user): ?>
      <a href="logout.php"><img src="https://graph.facebook.com/<?php echo $user; ?>/picture">
Logout</a>
    <?php else: ?>
      <span id="signin-btn">login</span>
      
    <?php endif ?>

  
  <div class="jumbotron">
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
            <center><a class="btn btn-lg btn-outline-inverse" href="#"><span id="my-signup-btn">Sign up today</span></a></center>


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
    
  </body>
</html>
