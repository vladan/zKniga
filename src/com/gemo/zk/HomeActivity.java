package com.gemo.zk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gemo.zk.utils.Utils;

public class HomeActivity extends Activity {

	private EditText searchEditText;
	private Spinner locationSpinner;
	private String mLocation;
	private String mSearchTerm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		searchEditText = (EditText) findViewById(R.id.searchText);
		locationSpinner = (Spinner) findViewById(R.id.citiesSpinner);

		locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView adapterView, View arg1, int arg2, long arg3) {
				int selectedPosition = adapterView.getSelectedItemPosition();
				mLocation = locationSpinner.getAdapter().getItem(selectedPosition).toString();				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});

		searchEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					onSearchClick(v);
				}        		
				return false;
			}
		});
		
		if(!Utils.isOnline(this)) {
			Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_LONG).show();
		}
	}


	public Dialog onCreateDialog(int dialogId) {
		switch(dialogId) {
		case 0:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.pleaseWait));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}

		return null;    	
	}

	public void onSearchClick(View v) {
		if(Utils.isOnline(this)) {
			mSearchTerm = searchEditText.getText().toString();
			if(mSearchTerm.length() > 0) {
				Intent resultsIntent = new Intent(v.getContext(), ResultsActivity.class);
				Bundle resultsBundle = new Bundle();
				resultsBundle.putString("location", mLocation);
				resultsBundle.putString("searchTerm", mSearchTerm);
				resultsIntent.putExtras(resultsBundle);    			
				startActivity(resultsIntent);
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.emptySearchString), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_LONG).show();
		}
	}

	public void onCatsClick(View v) {
		Utils.goCategoriesList(this);
	}
	
	public void onFirstCatClick(View v) {
		onCatClick(0);		
	}
	
	public void onSecondCatClick(View v) {
		onCatClick(1);
	}
	
	public void onThirdCatClick(View v) {
		onCatClick(2);
	}
	
	public void onFourthCatClick(View v) {
		onCatClick(3);
	}
	
	private void onCatClick(int count) {
		switch(count) {
		case 0:
			mSearchTerm = getString(R.string.firstCat);
			break;
		case 1:
			mSearchTerm = getString(R.string.secondCat);
			break;
		case 2:
			mSearchTerm = getString(R.string.thirdCat);
			break;
		case 3:
			mSearchTerm = getString(R.string.fourthCat);
			break;
		}
		
		Intent resultsIntent = new Intent(this, ResultsActivity.class);
		Bundle resultsBundle = new Bundle();
		resultsBundle.putString("location", mLocation);
		resultsBundle.putString("searchTerm", mSearchTerm);
		resultsIntent.putExtras(resultsBundle);    			
		
		if(!Utils.isOnline(this)) {
			Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_LONG).show();
		} else {
			startActivity(resultsIntent);			
		}
	}
	
	public AlertDialog aboutDialog() {
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String versionInfo = pInfo.versionName;

		String aboutTitle = String.format(getString(R.string.app_name));
		String versionString = String.format(getString(R.string.version), versionInfo);
		String aboutText = getString(R.string.aboutText);

		final TextView message = new TextView(this);
		message.setGravity(Gravity.CENTER);
		message.setLinkTextColor(getResources().getColor(R.color.genwhite));
		final SpannableString s = new SpannableString(aboutText);

		message.setPadding(5, 5, 5, 5);
		message.setText(s + "\n\n" + versionString);
		Linkify.addLinks(message, Linkify.ALL);

		return new AlertDialog.Builder(this).setTitle(aboutTitle).setCancelable(true).setIcon(R.drawable.icon).setPositiveButton(
				getString(android.R.string.ok), null).setView(message).create();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1 ,0, "За Апликацијата").setIcon(R.drawable.icon);		
		return true;	
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			aboutDialog().show();
		}
		return true;
	}
}
