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

  

?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
  
    <title>Signup with EZRide!</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="signin.css" rel="stylesheet">
    <script src="js/jquery-2.0.3.min.js"></script>
    
    <!-- Custom styles for this template -->
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>

<body>
    <?php include 'header.php'; ?>
    

    <div class="container">

      <form class="form-signup" action="./ezsignup.php" method="POST" id="signup-form">
        <center><h2 class="form-signin-heading">Sign Up</h2></center>
        <br>
        <div id="check-user" style="display:inline; color:#bbb"></div>
        <input type="text" class="form-control" id="user-name" name="username" placeholder="User name" maxlength="16" autofocus>
        <br>
        <input type="password" class="form-control" id="pwd" name="password" placeholder="Password" maxlength="32">
        <br>
        <input type="email" class="form-control" id="email" name="email" placeholder="Email" maxlength="32">
        <br>
        <textarea type="field" class="form-control" id="des" name="profile" placeholder="Describe yourself here..."></textarea>
        <br>
        <button class="btn btn-lg btn-primary btn-block" id="submit" type="submit" >Sign up</button>
      </form>

    </div> <!-- /container -->

    <script>
      $(document).ready(function() {
        

        $('#signup-form').submit(function(event){

          //event.preventDefault();
          var flag = 0;
            if($("#user-name").val()==''){
             $("#user-name").removeClass("idleField").removeClass("focusField").addClass("warnField");
              flag = 1;
            }
            if($("#pwd").val()==''){
              $("#pwd").removeClass("idleField").removeClass("focusField").addClass("warnField");
              //event.preventDefault();
              flag = 1;
            }
             if($("#email").val()==''){
              $("#email").removeClass("idleField").removeClass("focusField").addClass("warnField");
              flag = 1;
             // event.preventDefault();
            }
            if(flag == 1){return false;}
            if($("#user-name").val()!=''&& $("#pwd").val()!='' && $("#pwd-confirm").val()!='' && $("#email").val()!=''){
              
              $("#signup-from").submit();

            }

        });
        $('.form-control').focus(function() {
             $(this).removeClass("idleField").addClass("focusField");
        });

        $('.form-control').blur(function() {
           $(this).removeClass("focusField").addClass("idleField");
        });

        setInterval(function(){

                username_check();
                //console.log('test');
            }, 1000);

        function username_check(){
     
          var username = $('#user-name').val();
          //console.log(username);
          if($('#user-name').val()!=''){
            jQuery.ajax({
               type: "POST",
               url: "check.php",
               data: 'username='+ username,
               cache: false,
               success: function(response){
                //console.log(response);
                if(response != 0){
                  $("#user-name").removeClass("idleField").removeClass("focusField").addClass("warnField");
                  document.getElementById("check-user").innerHTML = "User name exists";
                }else{
                   $("#user-name").removeClass("warnField");
                    document.getElementById("check-user").innerHTML = "";
                }
             
            }
            });
          }
          }

      });


      
    </script>
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
  <?php include 'footer.php'; ?>
