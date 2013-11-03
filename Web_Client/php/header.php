<?php
require 'fbLogin.php';
?>


<div class="navbar navbar-default navbar-fixed-top my-nav">
      <div class="container nav-container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="index.php">EZRide</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav my-nav-nav">
            <li class="active" id="home-li"><a href="index.php">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
         <?php if ($user): ?>
           <ul class="nav navbar-nav navbar-right my-nav-right">
            <li class="dropdown">

               <a href="#" class="dropdown-toggle" data-toggle="dropdown">
               <span><img style="width:32px; height:32px; margin:0px; padding:2px;" class="img-rounded" src="https://graph.facebook.com/<?php echo $user; ?>/picture"><span><?php echo $user_profile['name']?> </span><b class="caret"></b></span>
               </a>
                <ul class="dropdown-menu">
                  <li><a href="logout.php">Logout</a></li>
                  <li><a href="#">Another action</a></li>
                  <li><a href="#">Something else here</a></li>
                  <li><a href="#">Separated link</a></li>
                </ul>

            </li>
           
           </ul>
          

          <?php else: ?>
            <?php if($ezuser_username != false): ?>
                 <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">

                       <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                       <span><span><?php echo $ezuser_username?></span><b class="caret"></b></span>
                       </a>
                        <ul class="dropdown-menu">
                          <li><a href="ezlogout.php">Logout</a></li>
                          <li><a href="#">Another action</a></li>
                          <li><a href="#">Something else here</a></li>
                          <li><a href="#">Separated link</a></li>
                        </ul>

                    </li>
                   
                   </ul>
            <?php else: ?>
              <form class="navbar-form navbar-right" action="./ezlogin.php" method = "POST">
                <div class="form-group">
                  <input type="text" placeholder="Username" class="form-control" name="username" size=32>
                </div>
                <div class="form-group">
                  <input type="password" placeholder="Password" class="form-control" name="password" size=32>
                </div>
                <button type="submit" class="btn btn-success login-btn" >Sign in</button>
              </form>
            <?php endif ?>
         <?php endif ?>

        </div><!--/.navbar-collapse -->
      </div>
    </div>
<?php 
session_start();
if(!isset($_SESSION['update'])):?>

<?php else:?>
<div id="my-alert-wrapper">
  <div class="alert alert-info"><span>Updated the information successfully!</span><span class="pull-right close-x">×</span></div>

</div>
<script type="text/javascript">
  $(".close-x").click(function(){
    $("#my-alert-wrapper").remove();
  });

</script>
<?php unset($_SESSION['update']);?>
<?php endif?>

