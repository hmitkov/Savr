package com.proxiad.savr;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proxiad.savr.async.AsyncOperation;
import com.proxiad.savr.async.AsyncOperation.AsyncOperationCallback;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.CustomTextViewMuseo;
import com.proxiad.savr.common.SaveCategory;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;

public class MapActivity extends FragmentActivity {

	LinearLayout llProgress;
	FrameLayout flSendCode;
	TextView tvMessages;
	Button btnSendOrSave;
	static AsyncOperation<?, ?, ?> mOperation = null;
	public static SaveCategory selectedCategory;
	MapActivity thisInstance;
	int counterRefreshIndicator = 0;

	// prix, adresse, artistes, numero, date_debut, date_fin, horaires, marque,
	// genre, acteurs, label, groupe

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		thisInstance = this;
		setContentView(R.layout.activity_map);
		setLayout();
	}
	
	@Override
	protected void onResume() {
		selectedCategory = SaveCategory.tous;
		String stringMapPointer = null;
		HomeActivity tabActivity =  (HomeActivity)getParent();
		stringMapPointer = tabActivity.getFlagMap();
		if (stringMapPointer == null) {
			putSavesOnMap(this, selectedCategory);
			initMapWithLastGPSNETLocations(thisInstance);
			getNewLocation(this);
		}else{
			putSaveOnMap(this);
		}
		super.onResume();
		System.err.println("MapActivity - > OnResume");
	}

	protected void setLayout() {

		CustomTextViewMuseo tous = (CustomTextViewMuseo) findViewById(R.id.tous);
		tous.setOnClickListener(new MapMenuOnClickListener(SaveCategory.tous));
		tous.setText(SaveCategory.tous.getStringResource());
		CustomTextViewMuseo restaurant = (CustomTextViewMuseo) findViewById(R.id.restaurant);
		restaurant.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.restaurant));
		restaurant.setText(SaveCategory.restaurant.getStringResource());
		CustomTextViewMuseo cinema = (CustomTextViewMuseo) findViewById(R.id.cinema);
		cinema.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.cinema));
		cinema.setText(SaveCategory.cinema.getStringResource());
		CustomTextViewMuseo livre = (CustomTextViewMuseo) findViewById(R.id.livre);
		livre.setOnClickListener(new MapMenuOnClickListener(SaveCategory.livre));
		livre.setText(SaveCategory.livre.getStringResource());
		CustomTextViewMuseo exposition = (CustomTextViewMuseo) findViewById(R.id.exposition);
		exposition.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.exposition));
		exposition.setText(SaveCategory.exposition.getStringResource());
		CustomTextViewMuseo bar = (CustomTextViewMuseo) findViewById(R.id.bar);
		bar.setOnClickListener(new MapMenuOnClickListener(SaveCategory.bar));
		bar.setText(SaveCategory.bar.getStringResource());
		CustomTextViewMuseo shopping = (CustomTextViewMuseo) findViewById(R.id.shopping);
		shopping.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.shopping));
		shopping.setText(SaveCategory.shopping.getStringResource());
		CustomTextViewMuseo theatre = (CustomTextViewMuseo) findViewById(R.id.theatre);
		theatre.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.theatre));
		theatre.setText(SaveCategory.theatre.getStringResource());
		CustomTextViewMuseo musique = (CustomTextViewMuseo) findViewById(R.id.musique);
		musique.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.musique));
		musique.setText(SaveCategory.musique.getStringResource());
		CustomTextViewMuseo hotel = (CustomTextViewMuseo) findViewById(R.id.hotel);
		hotel.setOnClickListener(new MapMenuOnClickListener(SaveCategory.hotel));
		hotel.setText(SaveCategory.hotel.getStringResource());
		CustomTextViewMuseo autre = (CustomTextViewMuseo) findViewById(R.id.autre);
		autre.setOnClickListener(new MapMenuOnClickListener(SaveCategory.autre));
		autre.setText(SaveCategory.autre.getStringResource());
		CustomTextViewMuseo concert = (CustomTextViewMuseo) findViewById(R.id.concert);
		concert.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.concert));
		concert.setText(SaveCategory.concert.getStringResource());
		CustomTextViewMuseo evenement = (CustomTextViewMuseo) findViewById(R.id.evenement);
		evenement.setOnClickListener(new MapMenuOnClickListener(
				SaveCategory.evenement));
		evenement.setText(SaveCategory.evenement.getStringResource());
		selectedCategory = SaveCategory.tous;
		String stringMapPointer = null;
		HomeActivity tabActivity =  (HomeActivity)getParent();
		stringMapPointer = tabActivity.getFlagMap();
		if (stringMapPointer == null) {
			putSavesOnMap(this, selectedCategory);
			initMapWithLastGPSNETLocations(thisInstance);
			getNewLocation(this);
		}else{
			putSaveOnMap(this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_initialization, menu);
		return true;
	}

	class SendCodeCallback implements AsyncOperationCallback {

		public SendCodeCallback() {
			super();

		}

		@Override
		public void onPreExecute() {
			tvMessages.setVisibility(View.GONE);
			flSendCode.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.GONE);
			llProgress.setVisibility(View.VISIBLE);

		}

		@Override
		public void onPostExecute() {
			mOperation = null;
			// TODO change tab
			// Intent intent = new Intent();
			// intent.setClass(InitializationActivity.this, HomeActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
		}

		@Override
		public void onProgress(int progress) {
		}

		@Override
		public void onCancel() {
			mOperation = null;
		}

		@Override
		public void onError(String msg) {
			tvMessages.setVisibility(View.VISIBLE);
			tvMessages.setText(msg);
			flSendCode.setVisibility(View.VISIBLE);
			llProgress.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.VISIBLE);
			mOperation = null;
		}
	}

	class MapMenuOnClickListener implements OnClickListener {
		String menuItem;

		public MapMenuOnClickListener(SaveCategory category) {
			menuItem = MapActivity.this.getString(category.getStringResource());
		}

		@Override
		public void onClick(View menuItemCategory) {
			HomeActivity tabActivity =  (HomeActivity)getParent();
			tabActivity.setFlagMap(null);
			CustomTextViewMuseo tous = (CustomTextViewMuseo) findViewById(R.id.tous);
			tous.setDefaultColor();
			tous.setBackgroundResource(R.drawable.bg_four);

			CustomTextViewMuseo restaurant = (CustomTextViewMuseo) findViewById(R.id.restaurant);
			restaurant.setDefaultColor();
			restaurant.setBackgroundResource(R.drawable.bg_ten);

			CustomTextViewMuseo cinema = (CustomTextViewMuseo) findViewById(R.id.cinema);
			cinema.setDefaultColor();
			cinema.setBackgroundResource(R.drawable.bg_six);
			CustomTextViewMuseo livre = (CustomTextViewMuseo) findViewById(R.id.livre);
			livre.setDefaultColor();
			livre.setBackgroundResource(R.drawable.bg_five);
			CustomTextViewMuseo exposition = (CustomTextViewMuseo) findViewById(R.id.exposition);
			exposition.setDefaultColor();
			exposition.setBackgroundResource(R.drawable.bg_ten);
			CustomTextViewMuseo bar = (CustomTextViewMuseo) findViewById(R.id.bar);
			bar.setDefaultColor();
			bar.setBackgroundResource(R.drawable.bg_three);
			CustomTextViewMuseo shopping = (CustomTextViewMuseo) findViewById(R.id.shopping);
			shopping.setDefaultColor();
			shopping.setBackgroundResource(R.drawable.bg_eight);
			CustomTextViewMuseo theatre = (CustomTextViewMuseo) findViewById(R.id.theatre);
			theatre.setDefaultColor();
			theatre.setBackgroundResource(R.drawable.bg_seven);
			CustomTextViewMuseo musique = (CustomTextViewMuseo) findViewById(R.id.musique);
			musique.setDefaultColor();
			musique.setBackgroundResource(R.drawable.bg_seven);
			CustomTextViewMuseo hotel = (CustomTextViewMuseo) findViewById(R.id.hotel);
			hotel.setDefaultColor();
			hotel.setBackgroundResource(R.drawable.bg_five);
			CustomTextViewMuseo autre = (CustomTextViewMuseo) findViewById(R.id.autre);
			autre.setDefaultColor();
			autre.setBackgroundResource(R.drawable.bg_five);
			CustomTextViewMuseo concert = (CustomTextViewMuseo) findViewById(R.id.concert);
			concert.setDefaultColor();
			concert.setBackgroundResource(R.drawable.bg_seven);
			CustomTextViewMuseo evenement = (CustomTextViewMuseo) findViewById(R.id.evenement);
			evenement.setDefaultColor();
			evenement.setBackgroundResource(R.drawable.bg_ten);

			CustomTextViewMuseo itemMenuView = (CustomTextViewMuseo) menuItemCategory;

			itemMenuView.setTextColor(Color.RED);
			if (menuItem != null && menuItem.length() == 3) {
				itemMenuView
						.setBackgroundResource(R.drawable.bg_three_selected);
			} else if (menuItem != null && menuItem.length() == 4) {
				itemMenuView.setBackgroundResource(R.drawable.bg_four_selected);
			} else if (menuItem != null && menuItem.length() == 5) {
				itemMenuView.setBackgroundResource(R.drawable.bg_five_selected);
			} else if (menuItem != null && menuItem.length() == 6) {
				itemMenuView.setBackgroundResource(R.drawable.bg_six_selected);
			} else if (menuItem != null && menuItem.length() == 7) {
				itemMenuView
						.setBackgroundResource(R.drawable.bg_seven_selected);
			} else if (menuItem != null && menuItem.length() == 8) {
				itemMenuView
						.setBackgroundResource(R.drawable.bg_eight_selected);
			} else if (menuItem != null && menuItem.length() == 9) {
				itemMenuView.setBackgroundResource(R.drawable.bg_ten_selected);
			} else if (menuItem != null && menuItem.length() == 10) {
				itemMenuView.setBackgroundResource(R.drawable.bg_ten_selected);
			}

			String selectedItem = itemMenuView.getString();
			selectedCategory = SaveCategory.getByText(MapActivity.this,
					selectedItem);
			
			String stringMapPointer = null;
			stringMapPointer = tabActivity.getFlagMap();
			if (stringMapPointer == null) {
				putSavesOnMap(menuItemCategory.getContext(), selectedCategory);
				initMapWithLastGPSNETLocations(thisInstance);
				getNewLocation(menuItemCategory.getContext());
			}else{
				putSaveOnMap(menuItemCategory.getContext());
			}
		}

	}

	public void getNewLocation(Context context) {

		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapLocations)).getMap();

		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Acquire a reference to the system Location Manager

		// Define a listener that responds to location updates
		LocationManagerHelper locationListener = new LocationManagerHelper();
		locationListener.setContext(context);
		locationListener.setGoogleMap(map);
		// Register the listener with the Location Manager to
		// receive location updates
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 0, 0, locationListener);
		/*
		 * locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
		 * 0L, 0F, ((LocationListener) locationListener));
		 */

		try {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		} catch (Exception e) {// Provider does not exist

		}
		/*
		 * try { locationManager.requestLocationUpdates(
		 * LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); } catch
		 * (Exception e) {// Provider does not exist
		 * 
		 * }
		 */

	}

	public void putSavesOnMap(Context context, SaveCategory selectedCat) {
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapLocations)).getMap();
		System.err.println("selectedCat-->" + selectedCat);
		System.err.println("MAP GEOCODE-->");
		if (map != null) {
			System.err.println("MAP GEOCODE2-->");
			map.clear();
			SaveCategory categorySearch = null;
			if (!selectedCat.equals(SaveCategory.tous)) {
				categorySearch = selectedCat;
			}
			System.err.println("MAP GEOCODE3.0-->" + categorySearch);
			List<Save> saves = PersistenceManager.getSaveListByCategory(
					context, categorySearch);
			Iterator<Save> savesIter = saves.iterator();
			System.err.println("MAP GEOCODE3-->" + saves.size());
			while (savesIter.hasNext()) {
				Save save = (Save) savesIter.next();
				if (save.getGeocode() != null
						&& !save.getGeocode().trim().equals("")) {

					System.err.println("GEOCODE-->" + save.getGeocode());

					String rareGeoCode = save.getGeocode().trim()
							.substring(1, save.getGeocode().length() - 1);
					String geoCodeFirstPart = rareGeoCode.substring(0,
							rareGeoCode.indexOf(",")).trim();
					String geoCodeSecondPart = rareGeoCode.substring(
							rareGeoCode.indexOf(",") + 1).trim();

					map.addMarker(new MarkerOptions()
							.position(
									new LatLng(
											Double.parseDouble(geoCodeFirstPart),
											Double.parseDouble(geoCodeSecondPart)))
							.title(save.getNom()).snippet(save.getAdresse())
							.draggable(false));

				}
			}
		}
	}
	
	
	
	
	
	public void putSaveOnMap(Context context) {
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapLocations)).getMap();
		if (map != null) {
			System.err.println("MAP GEOCODE2-->");
			map.clear();

				Save save = ((HomeActivity)getParent()).getSave();
				if (save.getGeocode() != null
						&& !save.getGeocode().trim().equals("")) {

					System.err.println("GEOCODE-->" + save.getGeocode());

					String rareGeoCode = save.getGeocode().trim()
							.substring(1, save.getGeocode().length() - 1);
					String geoCodeFirstPart = rareGeoCode.substring(0,
							rareGeoCode.indexOf(",")).trim();
					String geoCodeSecondPart = rareGeoCode.substring(
							rareGeoCode.indexOf(",") + 1).trim();

					map.addMarker(new MarkerOptions()
							.position(
									new LatLng(
											Double.parseDouble(geoCodeFirstPart),
											Double.parseDouble(geoCodeSecondPart)))
							.title(save.getNom()).snippet(save.getAdresse())
							.draggable(false));
					CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
							Double.parseDouble(geoCodeFirstPart), Double.parseDouble(geoCodeSecondPart)));
					CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

					map.moveCamera(center);
					map.animateCamera(zoom);


			}
		}
	}
	
	
	
	

	public void initMapWithLastGPSNETLocations(Context context) {
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapLocations)).getMap();
		if (map != null) {
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			Location locationGPS = null;
			Location locationNetwork = null;
			// MyLocation myLocation = null;
			locationGPS = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			locationNetwork = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (locationNetwork != null) {
				map.addMarker(new MarkerOptions()
						.position(
								new LatLng(locationNetwork.getLatitude(),
										locationNetwork.getLongitude()))
						.title(Constants.MOI).snippet("").draggable(false));

				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
						locationNetwork.getLatitude(), locationNetwork
								.getLongitude()));
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

				map.moveCamera(center);
				map.animateCamera(zoom);
			} else if (locationGPS != null) {
				map.addMarker(new MarkerOptions()
						.position(
								new LatLng(locationGPS.getLatitude(),
										locationGPS.getLongitude()))
						.title(Constants.MOI).snippet("").draggable(false));

				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
						locationGPS.getLatitude(), locationGPS.getLongitude()));
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

				map.moveCamera(center);
				map.animateCamera(zoom);

			}
		}
	}

	public void initMapWithLocation(Location location, GoogleMap map) {
		map.addMarker(new MarkerOptions()
				.position(
						new LatLng(location.getLatitude(), location
								.getLongitude())).title(Constants.MOI)
				.snippet("").draggable(false));

		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location
				.getLatitude(), location.getLongitude()));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		map.moveCamera(center);
		map.animateCamera(zoom);

	}

	public void updateMapLocation(Location location) {

	}

	private class LocationManagerHelper implements LocationListener {
		public Context context;
		public GoogleMap map;

		public void setContext(Context con) {
			context = con;
		}

		public void setGoogleMap(GoogleMap gmap) {
			map = gmap;
		}

		public void onLocationChanged(Location location) {
			if (counterRefreshIndicator == 0) {
				
				
				counterRefreshIndicator = counterRefreshIndicator + 1;
				
				
				String stringMapPointer = null;
				HomeActivity tabActivity =  (HomeActivity)getParent();
				stringMapPointer = tabActivity.getFlagMap();
				if (stringMapPointer == null) {
					putSavesOnMap(thisInstance, selectedCategory);
					initMapWithLocation(location, map);
				}else{
					putSaveOnMap(thisInstance);
				}
				
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	}
}
