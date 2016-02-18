<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['lattitude']) && isset($_POST['longitude']) && isset($_POST['time'])) {
    
    $lattitude = $_POST['lattitude'];
    $longitude = $_POST['longitude'];
    $time = $_POST['time'];
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO location(lattitude, longitude,time) VALUES('$lattitude', '$longitude','$time')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "location successfully inserted.";

        // echoing JSON response
        echo json_encode($response);  //this response comes to phone
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>

<form action="locationinsert.php" method="post">

            <input type="text" name="lattitude" placeholder="lattitude" />
            <br /><br />
            <input type="text" name="longitude" placeholder="longitude" />
            <br /><br />
            <input type="text" name="time" placeholder="time" />
            <br /><br />
            <input type="submit" value="update" />
            </form>
