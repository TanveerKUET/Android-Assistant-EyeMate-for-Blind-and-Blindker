package com.example.blindtracker;

import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
	//static final LatLng LSH = new LatLng(22.8977529, 89.5016342);
	  //static final LatLng KIEL = new LatLng(22.8977521, 89.5016282);
	  private GoogleMap map;
	  
	  
	  LatLng NEW;
	  
	//location
			private LocationManager locationManager;
			String latt,longi;
			Double lattiTude,longiTude;
			Calendar c;
			
			String lat,lon,tim;
			
		    private static final String TAG_LATTITUDE = "lattitude";
			private static final String TAG_LONGITUDE = "longitude";
			private static final String TAG_TIME = "time";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Intent i = getIntent();
		
		// getting product id (pid) from intent
		lat = i.getStringExtra(TAG_LATTITUDE);
		lon = i.getStringExtra(TAG_LONGITUDE);
		tim = i.getStringExtra(TAG_TIME);
        Log.v("GET_VALUES", "lattitude: "+lat+"longitude: "+lon+"time: "+tim);
        double lati = new Double(lat);
        double longi = new Double(lon);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap(); 
         LatLng CUR = new LatLng(lati, longi);
		 Marker hamburg = map.addMarker(new MarkerOptions().position(CUR).title("person"));
		 
		 Marker kiel = map.addMarker(new MarkerOptions()
	        .position(CUR)
	        .title("person")
	        .snippet("He is here")
	        .icon(BitmapDescriptorFactory
	            .fromResource(R.drawable.me)));
		 
		 
		 // Move the camera instantly to hamburg with a zoom of 15.
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CUR, 15));

		    // Zoom in, animating the camera.
		    map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

		    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
