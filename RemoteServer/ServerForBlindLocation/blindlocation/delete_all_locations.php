<?php
/*
 * Following code will delete all the location
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// mysql update row with matched pid
    $result = mysql_query("TRUNCATE TABLE location;");


        // check if row deleted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "locations successfully deleted";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No location found";

        // echo no users JSON
        echo json_encode($response);
    }

?>