package com.example.androidtablayout;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class AndroidTabLayoutActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TabHost tabHost = getTabHost();
		// Tab for Photos
		TabSpec photospec = tabHost.newTabSpec("Eyemate");
		photospec.setIndicator("Eyemate",getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent photosIntent = new Intent(this, PhotosActivity.class);
        photospec.setContent(photosIntent);
        
        
        TabSpec songspec = tabHost.newTabSpec("Info");
        songspec.setIndicator("Info",getResources().getDrawable(R.drawable.icon_songs_tab));
		Intent songIntent = new Intent(this, SongsActivity.class);
		songspec.setContent(songIntent);
        
        TabSpec videospec = tabHost.newTabSpec("Videos");
        videospec.setIndicator("Videos",getResources().getDrawable(R.drawable.icon_videos_tab));
		Intent videoIntent = new Intent(this, VideosActivity.class);
        videospec.setContent(videoIntent);
        
     // Adding all TabSpec to TabHost
        tabHost.addTab(photospec); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab
        tabHost.addTab(videospec); // Adding videos tab
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
