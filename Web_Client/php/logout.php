<?php

require 'src/facebook.php';
 
// Create our Application instance (replace this with your appId and secret).
$facebook = new Facebook(array(
  'appId'  => '535233079886936',
  'secret' => '106eadfd067a69e28c6735a47c0c4e57',
));
 
//on logout page
setcookie('fbs_'.$facebook->getAppId(), '', time()-100, '/', 'http://myfacedetect-weiqing.rhcloud.com');
session_destroy();
header('Location: http://ezride-weiqing.rhcloud.com');

?>