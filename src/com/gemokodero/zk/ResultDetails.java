package com.gemokodero.zk;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.gemokodero.zk.core.Result;
import com.gemokodero.zk.utils.AccountData;
import com.gemokodero.zk.utils.AddContact;
import com.gemokodero.zk.utils.ItemizedOverlay;
import com.gemokodero.zk.utils.Utils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ResultDetails extends MapActivity {

	private MyLocationOverlay myLocationOverlay;
	private MapView resultMapView;
	private static final int ACCOUNT_LIST = 0;

	private String mName;
	private String mPhone;
	private String mAddress;
	private String mWebsite;
	private String mLocation;
	
	private PointF mLatLon;
	
	ItemizedOverlay itemizedOverlay;
	List<Overlay> mapOverlays;
	Drawable drawable;
	
	private Button mWebsiteButton;
	private ArrayList<String> mAccounts;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.resultdetails);
				
		resultMapView = (MapView) findViewById(R.id.locationMap);
		mWebsiteButton = (Button) findViewById(R.id.websiteAction);
		
		Bundle passedBundle = getIntent().getExtras();
		mName = passedBundle.getString("name");
		mPhone = passedBundle.getString("phonenumber");
		mAddress = passedBundle.getString("address");
		mLocation = passedBundle.getString("location");
		mWebsite = passedBundle.getString("website");
			
		if(mAddress == null) {
			mAddress = getString(R.string.noresult);
		}
		
		if(mWebsite == null) {
			mWebsite = getString(R.string.noresult);
			mWebsiteButton.setVisibility(View.GONE);
		}
		
		mapOverlays = resultMapView.getOverlays();
		drawable = getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new ItemizedOverlay(drawable, resultMapView);
		
		mLatLon = getLocationFromAddress(mAddress);
		
		mAccounts = new ArrayList<String>();
		
		String locationText = String.format(getString(R.string.foundLocation), mAddress, mLocation);
		
		if(mLatLon.x == 0.0 || mLatLon.y == 0.0 || mAddress == getString(R.string.noresult)) {
			locationText = getString(R.string.notFoundLocation);
		}
		
		Toast.makeText(this, locationText, Toast.LENGTH_LONG).show();
		initMap();
	}
	
	private void initMap() {
		myLocationOverlay = new MyLocationOverlay(this, resultMapView);
        resultMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	GeoPoint gPoint = new GeoPoint((int) (mLatLon.x*1E6), (int) (mLatLon.y*1E6));
        		OverlayItem overlayItem = new OverlayItem(gPoint, mName,
        				String.format(getString(R.string.address), mAddress) + "\n"
        				+ String.format(getString(R.string.phone), mPhone) + "\n"
        				+ String.format(getString(R.string.location), mLocation) + "\n"
        				+ String.format(getString(R.string.website), mWebsite));
        		        		
        		itemizedOverlay.addOverlay(overlayItem);
        		mapOverlays.add(itemizedOverlay);

            	resultMapView.getController().setZoom(17);
            	resultMapView.getController().animateTo(gPoint);
            }
        });
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	protected PointF getLocationFromAddress(String address) {

		Geocoder gc = new Geocoder(this);
		double latitude = 0.0;
		double longitude = 0.0;
		
		address = address + " " + mLocation;
		try {
			List<Address> foundAdresses = gc.getFromLocationName(address, 5);
			latitude = foundAdresses.get(0).getLatitude();
			longitude = foundAdresses.get(0).getLongitude();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return (new PointF((float)latitude, (float)longitude));
	}
	
	public void onHomeClick(View v) {
		Utils.goHome(this);
	}
	
	public void onCatsClick(View v) {
		Utils.goCategoriesList(this);
	}
	
	public void onSaveAction(View v) {
		showDialog(ACCOUNT_LIST);
	}
	
	public void onWebAction(View v) {
		Utils.openWebsite(this, mWebsite);
	}
	
	public void onCallAction(View v) {
		Utils.callNumber(this, mPhone);
	}
	
	@Override
	protected Dialog onCreateDialog(int dialog) {
		switch(dialog) {
		case ACCOUNT_LIST:
			final Account[] list = AccountManager.get(this).getAccounts();

			for(int i = 0; i < list.length; i++) {
				mAccounts.add(list[i].name);
			}

			return new AlertDialog.Builder(this)
			.setTitle(getString(R.string.chooseAccount))
			.setItems(mAccounts.toArray(new CharSequence[mAccounts.size()]), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					AccountData accountData = new AccountData(list[which].name, list[which].type);
					Result currentResult = new Result();
					currentResult.setName(mName);
					currentResult.setPhoneNumber(mPhone);
					currentResult.setWebsite(mWebsite);
					
					AddContact cAdder = new AddContact(currentResult, accountData, ResultDetails.this);
					String resultString = null;
					resultString = getString(R.string.contactSaved);
					
					if(!cAdder.createContactEntry()) {
						resultString = getString(R.string.contactNotSaved);
					}

					Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_SHORT).show();
				}
			})
			.create();
		}

		return null;
	}
}
