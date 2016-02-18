package com.example.androidtablayout;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationHelper {

	public static final int MESSAGE_CODE_LOCATION_FOUND = 1;
	   public static final int MESSAGE_CODE_LOCATION_NULL  = 2;
	   public static final int MESSAGE_CODE_PROVIDER_NOT_PRESENT  = 3;   
	   private static final int  FIX_RECENT_BUFFER_TIME = 30000;
	   private LocationManager locationMgr;
	   private LocationListener locationListener;
	   private Handler handler;
	   private Runnable handlerCallback;
	   private String providerName;
	   private String logTag;
	   
	   public Double LattiTude,LongiTude;
	   
	   public LocationHelper(LocationManager locationMgr, 
			      Handler handler, String logTag) {                     
			      this.locationMgr = locationMgr;
			      this.locationListener = new LocationListenerImpl();
			      this.handler = handler;      
			      this.handlerCallback = new Thread() {                
			         public void run() {
			            endListenForLocation(null);                         
			         }
			      };
			      Criteria criteria = new Criteria();             
			      criteria.setAccuracy(Criteria. ACCURACY_FINE );              
			      this.providerName = locationMgr.getBestProvider(criteria, true);
			      this.logTag = logTag;
			   }
	   public void getCurrentLocation(int durationSeconds) {   
		      if (this.providerName == null) {  
		         sendLocationToHandler( MESSAGE_CODE_PROVIDER_NOT_PRESENT , 0, 0); 
		         return;
		      }
		      Location lastKnown = locationMgr.getLastKnownLocation(providerName);
		      if (lastKnown != null && 
		            lastKnown.getTime() >= 
		            (System.currentTimeMillis() - FIX_RECENT_BUFFER_TIME)) {
		    	    
		         sendLocationToHandler( MESSAGE_CODE_LOCATION_FOUND, 
		            (int) (lastKnown.getLatitude() * 1e6),
		            (int) (lastKnown.getLongitude() * 1e6));
		         
		      } else {
		         listenForLocation(providerName, durationSeconds);     
		      }
		   }
	   private void sendLocationToHandler(int msgId, int lat, int lon) {
		      Message msg = Message. obtain (handler, msgId, lat, lon);
		      handler.sendMessage(msg);
		   }
	   private void listenForLocation(String providerName, 
		         int durationSeconds) {                             
		      locationMgr.requestLocationUpdates(providerName, 0, 0, 
		            locationListener);                                
		      handler.postDelayed(handlerCallback, durationSeconds * 1000);   
		   }
	   private void endListenForLocation(Location loc) {         
		      locationMgr.removeUpdates(locationListener); 
		      handler.removeCallbacks(handlerCallback); 
		      if (loc != null) {
		         sendLocationToHandler( MESSAGE_CODE_LOCATION_FOUND, 
		            (int) (loc.getLatitude() * 1e6), 
		            (int) (loc.getLongitude() * 1e6));               
		      } else {
		         sendLocationToHandler( MESSAGE_CODE_LOCATION_NULL , 0, 0); 
		      }
		   }
	   
	   public double retLattitude(){
		   
		   return LattiTude;
	   }
      public double retLongiitude(){
		   
		   return LongiTude;
	   }
	   private class LocationListenerImpl 
	      implements LocationListener {

		   @Override
		   public void onLocationChanged(Location loc) {      
		      if (loc == null) {
		         return;
		      }
		      Log.d(logTag, "Location changed to:" + loc.toString());
		      endListenForLocation(loc);
		   }
		   @Override
		   public void onProviderDisabled(String provider) {  
		      endListenForLocation(null);
		   }
		   @Override
		   public void onProviderEnabled(String provider) {
		   }

		@Override
		   public void onStatusChanged(String provider, int status, 
		         Bundle extras) {                                    
		      Log.d (logTag, "Location status changed to:" + status);
		      switch (status) {
		         case LocationProvider. AVAILABLE:
		            break;
		         case LocationProvider. TEMPORARILY_UNAVAILABLE :
		            break;
		         case LocationProvider. OUT_OF_SERVICE:
		            endListenForLocation(null);
		      }
		   }
	   
	   }

}
