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
    <script src="js/jquery-2.0.3.min.js"></script>
    <!-- Custom styles for this template -->
    <link href="home.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

  <div id="fb-root"></div>
       
  <?php include 'header.php'; ?>

  <div class="container main ">
      <!-- Example row of columns -->
      <div class="row">
         <div class="col-lg-6">
            <div class="project">
                <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
                <hr>
                <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          
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
       $(document).ready(function() {

          $("#home-li").removeClass("active");
          $("#app-li").addClass("active");

       });

    </script>
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
