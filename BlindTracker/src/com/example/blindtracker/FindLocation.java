package com.example.blindtracker;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FindLocation extends Activity{
	
	TextView latText,longText,timeText,deleterowsValue;
	Button getLocation,showmap,btnDelete;
	
	 String id;
	   	String lattitude;
	   	String longitude;
	   	String time;
	   	
	 // Progress Dialog
	 	private ProgressDialog pDialog;

	 	// JSON parser class
	 	JSONParser jsonParser = new JSONParser();
	   	
	 // single product url
	 	//private static final String url_product_detials = "http://192.168.193.1/blindlocation/getlocation.php";
	 	private static final String url_product_detials = "http://demo.techspree.net/blindlocation/getlocation.php";

	 	//private static final String url_product_detials = "http://myeyemateproject.fh2web.com/blindlocation/getlocation.php";
	 	//private static  String url_delete_all_products="http://192.168.193.1/blindlocation/delete_all_locations.php";
	 	private static  String url_delete_all_products="http://demo.techspree.net/blindlocation/delete_all_locations.php";
 
	 	String pid,deleteresult;
	 // JSON Node names
	 		private static final String TAG_SUCCESS = "success";
	 		private static final String TAG_LOCATION = "location";
	 		private static final String TAG_ID = "id";
	 		private static final String TAG_LATTITUDE = "lattitude";
	 		private static final String TAG_LONGITUDE = "longitude";
	 		private static final String TAG_TIME = "time";
	 		
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.getlocation);
	
	latText = (TextView)findViewById(R.id.lattitude);
	longText = (TextView)findViewById(R.id.longitude);
	timeText = (TextView)findViewById(R.id.time);
	deleterowsValue = (TextView)findViewById(R.id.deleterows);
	
	getLocation = (Button)findViewById(R.id.btnlocation);
	showmap = (Button)findViewById(R.id.showmap);
	btnDelete = (Button) findViewById(R.id.btnDelete);
	
	getLocation.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new GetProductDetails().execute();
		}
	});
	
	showmap.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			String lattitude = latText.getText().toString();
			String longitude = longText.getText().toString();
			String time = timeText.getText().toString();
			
			// Starting new intent
			Intent in = new Intent(getApplicationContext(),
					MainActivity.class);
			// sending pid to next activity
			in.putExtra(TAG_LATTITUDE, lattitude);
			in.putExtra(TAG_LONGITUDE, longitude);
			in.putExtra(TAG_TIME, time);
			
			// starting new activity and expecting some response back
			startActivityForResult(in, 100);
		}
	});
	
	btnDelete.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new DeleteAllProducts().execute();
		}
	});
	
}

/**
 * Background Async Task to Get Blind person Location details
 * */
class GetProductDetails extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(FindLocation.this);
		pDialog.setMessage("Loading Location details. Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		//Toast.makeText(MainActivity.this, "pid: "+id, Toast.LENGTH_SHORT).show();
		pDialog.show();
	}

	/**
	 * Getting Location in background thread
	 * */
	
	protected String doInBackground(String... args) {
		
		
		
				int success;
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));
				
				
				// getting product details by making HTTP request
				// Note that product details url will use GET request
				Log.e("JSON_CALL", "I am calling json");
				JSONObject json = jsonParser.makeHttpRequest(url_product_detials, "GET", params);

				// check your log for json response
			/*	Log.d("Single Product Details", json.toString()); */
				try {
					
					
					// json success tag
				success = json.getInt(TAG_SUCCESS); 
				Log.e("json success", ""+success);								
					if (success == 1) {
						// successfully received product details
						JSONArray productObj = json
								.getJSONArray(TAG_LOCATION); // JSON Array
						
						// get first product object from JSON Array
						JSONObject product = productObj.getJSONObject(0);
						Log.e("TAG_NAME ", product.getString(TAG_LATTITUDE));
						Log.e("TAG_PRICE ", product.getString(TAG_LONGITUDE));
						Log.e("TAG_DESCRIPTION ", product.getString(TAG_TIME));
						//txtName = (EditText) findViewById(R.id.inputName);
						
						 lattitude = product.getString(TAG_LATTITUDE);
						 longitude = product.getString(TAG_LONGITUDE);
						 time = product.getString(TAG_TIME);
						
						//txtName.setText(product.getString(TAG_NAME));
						
						/*
						// product with this pid found
						// Edit Text
						txtName = (EditText) findViewById(R.id.inputName);
						txtPrice = (EditText) findViewById(R.id.inputPrice);
						txtDesc = (EditText) findViewById(R.id.inputDesc);

						// display product data in EditText
						txtName.setText(product.getString(TAG_NAME));
						txtPrice.setText(product.getString(TAG_PRICE));
						txtDesc.setText(product.getString(TAG_DESCRIPTION));*/

					}else{
						// product with pid not found
					}
				
				} catch (JSONException e) {
					//e.printStackTrace();
					//txtView.setText("success :"+0);
				}
			
		return null;
	}
	/**
	 * After completing background task Dismiss the progress dialog
	 * When Using any GUI work then do that in onPostExecuteMethod
	 * **/
	protected void onPostExecute(String file_url) {
		// dismiss the dialog once product updated
		pDialog.dismiss();
		latText.setText(lattitude);
		longText.setText(longitude);
		timeText.setText(time);
	}
}

		/*
		 * class For deleting all previous data....
		 */
/*
 * Deleting all products in background through asynctask
 */
	class DeleteAllProducts extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		pDialog = new ProgressDialog(FindLocation.this);
		pDialog.setMessage("Deleting all locations please wait........");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
		}
		 /**
		 * getting deleting response from url
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));
				
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(url_delete_all_products, "GET", params);
				// check your log for json response
				Log.e("Delete All Products", json.toString());
				
				// json success tag
				
				success = json.getInt(TAG_SUCCESS);
				if(success==1){
					deleteresult = "Delete successfull";
				}
				else{
					deleteresult = "No rows found...";
					
				}
			}catch (JSONException e) {
				deleteresult = "exception Occured...";
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// dismiss the dialog once product deleted
		     pDialog.dismiss();
		     deleterowsValue.setText(""+deleteresult);
		     //Toast.makeText(FindLocation.this, deleteresult, Toast.LENGTH_SHORT).show();
		}
	}


}
