<?php  
	header("Content-Type: text/html;charset=UTF-8");
	header("Content-Tyoe:text/html; cahrset=euc-kr");
	
	$con=mysqli_connect("localhost","test","1234","carnum");
	mysqli_select_db($con,'cartable');
	
	$data_pay="'".$_POST['carId']."','".$_POST['payCheck']."'";		
	$sql="UPDATE cartable SET payCheck='$data_pay[0]' where carId='$data_pay[1]'";
    $result = mysqli_query($con,$sql);
    
    if($result){
		echo "1";
    }else
      echo "-1";	
    
    mysqli_close($con);
?>