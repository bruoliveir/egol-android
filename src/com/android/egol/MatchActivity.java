package com.android.egol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchActivity extends Activity {
	GoogleMap map;
	String team1, team2, date, city, stadium, stage;
	double latitude, longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match);

		checkGoogleMapsApp();

		buildMatchFromIntent();
		
		Button check_map = (Button) findViewById(R.id.map_button);
		check_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MatchActivity.this, CheckMapActivity.class);
				i.putExtra("MATCH_DETAILS_LAT", latitude);
				i.putExtra("MATCH_DETAILS_LNG", longitude);
				i.putExtra("MATCH_DETAILS_TEAM1", team1);
				i.putExtra("MATCH_DETAILS_TEAM2", team2);
				i.putExtra("MATCH_DETAILS_CITY", city);
				i.putExtra("MATCH_DETAILS_STADIUM", stadium);
				MatchActivity.this.startActivity(i);
			}
		});
		
		Button directions_map = (Button) findViewById(R.id.directions_button);
		directions_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?f=d&daddr=" + latitude + "," + longitude))
					.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
				startActivity(i);
			}
		});
		
		ImageButton facebook = (ImageButton) findViewById(R.id.facebook_button);
		facebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND)
					.setType("text/plain")
					.putExtra(Intent.EXTRA_TEXT, "Test sharing")
					.setPackage("com.facebook.katana");
		        startActivity(i);
		        // https://m.facebook.com/sharer.php?u=website_url&t=titleOfThePost
			}
		});
		
		ImageButton twitter = (ImageButton) findViewById(R.id.twitter_button);
		twitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND)
					.setType("text/plain")
		        	.putExtra(Intent.EXTRA_TEXT, "Hey, I'll be at " + stadium + " in " + city + " to watch the " + team1 + " × " + team2 + " match!")
		        	.setComponent(new ComponentName("com.twitter.android", "com.twitter.android.PostActivity"));
		        startActivity(i);
			}
		});
	
		ImageButton gplus = (ImageButton) findViewById(R.id.gplus_button);
		gplus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent i = new PlusShare.Builder(MatchActivity.this)
		         	.setType("text/plain")
		         	.setText("Hey, I'll be at " + stadium + " in " + city + " to watch the " + team1 + " × " + team2 + " match!")
		         	.getIntent();
				 startActivity(i);
			}
		});
	}
	
	
	
	private void buildMatchFromIntent() {
		Intent intent = getIntent();
		String extra = intent.getStringExtra("MATCH_DETAILS");
		
		JSONObject o;
		JSONArray a;
		try {
			a = new JSONArray(extra);
			o = a.getJSONObject(0);
			team1 = (o.getJSONObject("team1").getString("name") == "null") ? o.getJSONObject("team1").getString("code") : o.getJSONObject("team1").getString("name");
			team2 = (o.getJSONObject("team2").getString("name") == "null") ? o.getJSONObject("team2").getString("code") : o.getJSONObject("team2").getString("name");
            date = o.getString("date_and_time");
			city = o.getJSONObject("city").getString("name");
			stadium = o.getJSONObject("city").getString("stadium");
            stage = o.getJSONObject("stage").getString("name");
			latitude = Float.parseFloat(o.getJSONObject("city").getString("latitude"));
			longitude = Float.parseFloat(o.getJSONObject("city").getString("longitude"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		TextView mTeams = (TextView) findViewById(R.id.list_item_teams);
        TextView mDate = (TextView) findViewById(R.id.list_item_date);
		TextView mCity = (TextView) findViewById(R.id.list_item_city);
		TextView mStadium = (TextView) findViewById(R.id.list_item_stadium);
        TextView mStage = (TextView) findViewById(R.id.list_item_stage);
		
		mTeams.setText(team1 + " × " + team2);
        mDate.setText("Date: " + date);
		mCity.setText("City: " + city);
		mStadium.setText("Place: " + stadium);
        mStage.setText("Stage: " + stage);
		
		LatLng MATCH_LATLNG = new LatLng(latitude, longitude);
		
		if (map != null) {
			map.addMarker(new MarkerOptions()
				.position(MATCH_LATLNG)
				.title(team1 + " × " + team2)
				.snippet(stadium + " – " + city));
		}
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(MATCH_LATLNG, 13));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(MATCH_LATLNG, 14), 1000, null);
		
	}
	
	
	private void checkGoogleMapsApp() {
		boolean maps_installed;
		
	    try {
	        getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        maps_installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	    	maps_installed = false;
	    }
	    
	    // Do a null check to confirm that the map is not instantiated.
	    if (map == null) {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.match_map)).getMap();

	        if (!maps_installed) {
	            AlertDialog.Builder d = new AlertDialog.Builder(this);
	            d.setMessage("Install Google Maps to view location and get directions to stadiums!\n\nPlease, click below to download and install from Google Play.");
	            d.setCancelable(false);
	            d.setPositiveButton("Install", new DialogInterface.OnClickListener() {
	    	        @Override
	    	        public void onClick(DialogInterface dialog, int which) {
	    	            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
	    	            startActivity(i);
	    	            // Finish the activity to force re-check
	    	            finish();
	    	        }
	            });
	            d.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(MatchActivity.this, MainActivity.class);
						startActivity(i);
					}
				});
	            AlertDialog dialog = d.create();
	            dialog.show();
	        }
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_match, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
