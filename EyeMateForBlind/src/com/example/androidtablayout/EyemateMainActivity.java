package com.example.androidtablayout;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class EyemateMainActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TabHost tabHost = getTabHost();
		// Tab for Photos
		TabSpec eyemate = tabHost.newTabSpec("Eyemate");
		eyemate.setIndicator("Eyemate",getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent eyemateIntent = new Intent(this, EyemateActivity.class);
		eyemate.setContent(eyemateIntent);
        
        
        TabSpec info = tabHost.newTabSpec("Info");
        info.setIndicator("Info",getResources().getDrawable(R.drawable.icon_songs_tab));
		Intent infoIntent = new Intent(this, InfoActivity.class);
		info.setContent(infoIntent);
        
       
        
     // Adding all TabSpec to TabHost
        tabHost.addTab(eyemate); // Adding photos tab
        tabHost.addTab(info); // Adding songs tab
        
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
