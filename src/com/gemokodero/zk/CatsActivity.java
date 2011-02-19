package com.gemokodero.zk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gemokodero.zk.utils.Utils;

public class CatsActivity extends ListActivity implements ListView.OnScrollListener {

	private final class rmWin implements Runnable {
		public void run() {
			removeWindow();
		}
	}

	ArrayList<String> mCategoriesList;
	private rmWin mRemoveWindow = new rmWin();
	Handler mHandler = new Handler();
	private WindowManager mWindowManager;
	private TextView mLetterText;
	private boolean mVisible;
	private boolean mOk;
	private char mPrevLetter = Character.MIN_VALUE;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.catslayout);

		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		getListView().setOnScrollListener(this);
		LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mLetterText = (TextView) inflate.inflate(R.layout.letter_layout, null);
		mLetterText.setVisibility(View.INVISIBLE);

		mHandler.post(new Runnable() {
			public void run() {
				mOk = true;

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);

				mWindowManager.addView(mLetterText, lp);
			}});

		AssetManager assetManager = getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("zkcats.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mCategoriesList = generateCategories(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<HashMap<String, String>> categories = new ArrayList<HashMap<String, String>>();
		for(String cat : mCategoriesList) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("catname", cat);
			categories.add(map);
		}

		SimpleAdapter catsAdapter = new SimpleAdapter(this, categories,
				R.layout.cats_single_row, 
				new String[] {"catname"},
				new int[] { R.id.catname} );

		getListView().setAdapter(catsAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOk = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeWindow();
		mOk = false;
	}

	public void onHomeClick(View v) {
		Utils.goHome(this);
	}

	private ArrayList<String> generateCategories(InputStream inputStream) throws IOException {
		ArrayList<String> catList = new ArrayList<String>();
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		String x = "";
		while ((x = r.readLine()) != null) {
			catList.add(x);
		}

		return catList;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mOk) {
			char firstLetter = mCategoriesList.get(firstVisibleItem).charAt(0);

			if (!mVisible && firstLetter != mPrevLetter) {
				mVisible = true;
				mLetterText.setVisibility(View.VISIBLE);
			}
			mLetterText.setText(((Character)firstLetter).toString());
			mHandler.removeCallbacks(mRemoveWindow);
			mHandler.postDelayed(mRemoveWindow, 3000);
			mPrevLetter = firstLetter;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(Utils.isOnline(this)) {
			final Intent intent = new Intent(this, ResultsActivity.class);
			Bundle resultsBundle = new Bundle();
			resultsBundle.putString("location", getString(R.string.country));
			resultsBundle.putString("searchTerm", mCategoriesList.get(position));
			
			intent.putExtras(resultsBundle);    			
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
		} else {
			Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_LONG).show();
		}
	}

	private void removeWindow() {
		if (mVisible) {
			mVisible = false;
			mLetterText.setVisibility(View.INVISIBLE);
		}
	}
}
