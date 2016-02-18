<?php


$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all products from products table
$result = mysql_query("SELECT max(id) FROM `location`") or die(mysql_error());

$sql = mysql_query(" SELECT MAX(id) FROM location ") or die(mysql_error());

$row = mysql_fetch_array($sql) or die(mysql_error());

         //echo "Maximum value is ::  ".$row[0];
$result = mysql_query("SELECT id,lattitude, longitude,time FROM `location` WHERE id=$row[0]") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
	$response["location"] = array();
	while ($row = mysql_fetch_array($result)) {
		$location = array();
	$location["id"] = $row["id"];
	$location["lattitude"] = $row["lattitude"];
	$location["longitude"] = $row["longitude"];
	$location["time"] = $row["time"];
	// push single product into final response array
        array_push($response["location"], $location);
    }
	$response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);

}

else{

     
	// no products found
    $response["success"] = 0;
    $response["message"] = "No products found";

    // echo no users JSON
    echo json_encode($response);
    
}
?>