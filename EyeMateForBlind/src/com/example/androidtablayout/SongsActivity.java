package com.example.androidtablayout;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SongsActivity extends Activity{
			TextView lattText,longText,records;
			EditText name,phone;
			Button updatebtn,insert,showdb,deleteAll;
			
			Handler myhandler;
			String latt,longi;
			//GPSTracker class
			GPSTracker gps;	
			//location
			private LocationManager locationManager;
			DBAdapter db = new DBAdapter(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songlayout);
		
		lattText = (TextView)findViewById(R.id.lattitudetext);
		longText = (TextView) findViewById(R.id.longitudetext);
		records = (TextView) findViewById(R.id.records);
		
		name = (EditText) findViewById(R.id.editText1);
		phone = (EditText) findViewById(R.id.editText2);
		
		
		insert = (Button) findViewById(R.id.insbtn);
		showdb = (Button) findViewById(R.id.showbtn);
		deleteAll = (Button) findViewById(R.id.deleteAll);
		
		//declaring the location manager
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );		

				
		myhandler = new Handler() {                      
	          public void handleMessage(Message m) {
	             if (m.what == LocationHelper.MESSAGE_CODE_LOCATION_FOUND) {    
	                	                int l = m.arg1;
	                double latdouble  = l/(1e6);
	                latt = ""+latdouble;
	                int lo=m.arg2;
	                double longdouble = lo/(1e6);
	                longi = ""+lo;
	                lattText.setText(latt);
	                longText.setText(longi);
	                
	                Log.e("INSERT_TAG", "Best location from handler and it is now inserting");
	                //detail.setText("HANDLER RETURNED\nlat:" + latt + "\nlon:" + longi);

	                //new InsertLocation().execute();
	               // Log.e("HANDLER_RETURNED_TAG",""+detail.getText().toString());
	             } else if (m.what ==
	                   LocationHelper.MESSAGE_CODE_LOCATION_NULL ) {  
	                //detail.setText("HANDLER RETURNED\nunable to get location");
	                /*
	                 * here we check that the GPS tracker can give us value or not
	                 */
	                // check if GPS enabled
	                
	                if(gps.canGetLocation()){
	                     
	                    double latitude = gps.getLatitude();
	                    double longitude = gps.getLongitude();
	                    //detail.setText("GPS tracker returns the network value latt:  "+latitude+" long: "+longitude);
	                    // \n is for new line
	                    Toast.makeText(SongsActivity.this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
	                    Log.e("GPS_HANDLER_TAG",""+"Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
	                    latt = ""+latitude;
	                    longi = ""+longitude;
	                    
	                    lattText.setText(latt);
		                longText.setText(longi);
	                    Log.e("INSERT_TAG", "Best location from GPSTracker and it is now inserting");
	                    //new InsertLocation().execute();
	                }else{
	                    // can't get location
	                    // GPS or Network is not enabled
	                    // Ask user to enable GPS/network in settings
	                    gps.showSettingsAlert();
	                }
	             } else if (m.what ==
	                   LocationHelper.MESSAGE_CODE_PROVIDER_NOT_PRESENT ) {             
	                //detail.setText("HANDLER RETURNED\nprovider not present");
	             }
	          }
	       };
	       
	       
	       insert.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					db.open();
					if(name.getText().toString().trim().length()==0 || phone.getText().toString().trim().length()==0){
			          Toast.makeText(getApplicationContext(), "it cant be blank", Toast.LENGTH_SHORT).show();

					}
					else{
						String Name = name.getText().toString();
						String pnumber = phone.getText().toString();
						long id = db.insertContact(Name,pnumber);
						Toast.makeText(getApplicationContext(), id+"Inserted succesfully",Toast.LENGTH_SHORT).show();
					}
					db.close();
				}
			});
	       
	       showdb.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new getData().execute();
				}
			});
	       
	       deleteAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				db.open();
				db.deleteAll();
				db.close();
			}
		});
	}
	
	/*
	 * Retrieving data in background
	 * 
	 */
	
	class getData extends AsyncTask<String, String, String>{
     String  hisname,hisphone;
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			db.open();
			Cursor c = db.getAllContacts();
			if(c.getCount()==0){
				//records.setText("No records found.....");
			}
			else{
				StringBuffer buffer=new StringBuffer();
				while(c.moveToNext()){
					/*
					buffer.append("name: "+c.getString(0)+"\n");
	    			buffer.append("phone: "+c.getString(1)+"\n\n");
	    			*/
	    			hisname = c.getString(0);
					hisphone = c.getString(1);
				}
				
				/*
				//records.setText(""+buffer.toString());
				Builder builder=new Builder(SongsActivity.this);
		    	builder.setCancelable(true);
		    	builder.setTitle("Records");
		    	builder.setMessage(""+buffer.toString());
		    	builder.show();
				//records.setText("records found.....");
		    	*/
				db.close();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			name.setText(hisname);
			phone.setText(hisphone);
			
		}
		
	}

}
