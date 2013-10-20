<?php
require 'index.php';




if(isset($_GET))
{
  //echo $_GET["name"];

   //then work with the var data
     
          $search_name=$_GET["name"];
          $target;
    
          foreach ($friends["data"] as $value) {
            
            if ($search_name == $value["name"])
              {
              //echo "Have a good day!";
              $target = $value;
              }
           
            //echo '</li>';
          }
        //echo '</ul>';
        if ($target)
              {
              
              
              echo $target["name"]; 
              $friend_feeds = $facebook->api('/'.$target["id"].'/feed');
              print_r($friend_feeds);

              }
    



}

?>