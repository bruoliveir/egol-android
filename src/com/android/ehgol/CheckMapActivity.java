package com.android.ehgol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CheckMapActivity extends Activity {
	GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkmap);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();

		buildMapFromIntent();
		
		Button directions_map = (Button) findViewById(R.id.directions_button);
		directions_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CheckMapActivity.this, DirectionsMapActivity.class);
				CheckMapActivity.this.startActivity(i);
			}
		});
	}

	private void buildMapFromIntent() {
		Intent i = getIntent();
		Toast.makeText(this, String.valueOf(i.getDoubleExtra("LOCATION_LAT", 0)), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, String.valueOf(i.getDoubleExtra("LOCATION_LNG", 0)), Toast.LENGTH_SHORT).show();
		LatLng GAME_LATLNG = new LatLng(i.getDoubleExtra("LOCATION_LAT", 0), i.getDoubleExtra("LOCATION_LNG", 0));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 9));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 10), 2000, null);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_checkmap, menu);
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