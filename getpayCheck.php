<?php  
$con=mysqli_connect("localhost","test","1234","carnum");


if(!$con){
   die('Not connect: '.mysql_error());
}

mysqli_set_charset($con,"utf8");

$res=mysqli_query($con, "select * from cartable where payCheck=1");
$result=array();

while($row=mysqli_fetch_array($res)){
   array_push($result,
   array('carId'=>$row[0],'inTime'=>$row[1],'payCheck'=>$row[2],'outCheck'=>$row[3]));
}

 header('Content-Type: application/json; charset=utf8');
   $json = json_encode(array("test"=>$result), 
   JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE|JSON_NUMERIC_CHECK);
   echo $json;
   
mysqli_close($con);
   
?>