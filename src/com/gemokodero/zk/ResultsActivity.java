package com.gemokodero.zk;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gemokodero.zk.core.Result;
import com.gemokodero.zk.core.Search;
import com.gemokodero.zk.utils.AccountData;
import com.gemokodero.zk.utils.AddContact;
import com.gemokodero.zk.utils.ResultsAdapter;
import com.gemokodero.zk.utils.Utils;

public class ResultsActivity extends ListActivity implements OnItemClickListener {

	private static final int ACCOUNT_LIST = 0;
	private ArrayList<Result> resultItems;
	private Result mClickedResult;
	
	private ArrayList<String> mAccounts;
	private int mCurrentPage = 0;
	
	private ResultsAdapter mAdapter;
	private Search mSearch;
	private int mResCount;
	private Button mNextPageButton, mPrevPageButton;
	private TextView mPageCountView;
	private Thread polly = null;

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 666) {
				findViewById(R.id.search_progress).setVisibility(View.GONE);
			}
		}};

	public ResultsActivity() {
		mAccounts = new ArrayList<String>();
		resultItems = new ArrayList<Result>();
	}
	
	@Override
	public void onCreate(Bundle sInstanceState) {
		super.onCreate(sInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.results_list_view);
				
		mNextPageButton = (Button) findViewById(R.id.nextPage);
		mPrevPageButton = (Button) findViewById(R.id.prevPage);
		mPageCountView = (TextView) findViewById(R.id.pageCount);
		
		mPrevPageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				polly.interrupt();
				if(mSearch != null) {
					mAdapter.clear();
					mCurrentPage--;
					
					String tmpCurrPage = Integer.toString(mCurrentPage + 1);	
					mPageCountView.setText(tmpCurrPage + "/" + mResCount);

					if(mCurrentPage == 0) {
						mPrevPageButton.setVisibility(View.GONE);
					}
					
					doSearch();
				}
			}			
		});
		
		mNextPageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSearch != null) {
					polly.interrupt();
					mAdapter.clear();
//					mAdapter.notifyDataSetChanged();
					
					mCurrentPage++;
					String tmpCurrPage = Integer.toString(mCurrentPage + 1);	
					mPageCountView.setText(tmpCurrPage + "/" + mResCount);
					
					mPrevPageButton.setVisibility(View.VISIBLE);
					if(mResCount - 1 == mCurrentPage) {
						mNextPageButton.setVisibility(View.GONE);
					}
					
					doSearch();
				}
			}
		});
		
		initList();
		initSearch();
				
		registerForContextMenu(getListView());
	}
			
	private void initSearch() {
		mSearch = new Search(mHandler, resultItems, mAdapter);
		String location = getIntent().getExtras().getString("location");
		String term = getIntent().getExtras().getString("searchTerm");
		mResCount = getIntent().getExtras().getInt("resCount");
		
		mSearch.setSearchLocation(location);
		mSearch.setTerm(term);
//		mResCount = mSearch.getResultsCount();
		
		if(mResCount <= 1) {
//			finish();
		} else	if(mResCount > 1) {
			String tmpCurrPage = Integer.toString(mCurrentPage + 1);
			mPageCountView.setText(tmpCurrPage + "/" + mResCount);
			mNextPageButton.setVisibility(View.VISIBLE);
		}

		doSearch();
	}
	
	private void doSearch() {		
		polly = new Thread(new Runnable() {
			@Override
			public void run() {
				mSearch.exec(mCurrentPage);
			}			
		});
		
		polly.start();		
		findViewById(R.id.search_progress).setVisibility(View.VISIBLE);
	}

	private void initList() {
		mAdapter = new ResultsAdapter(this, R.layout.results_single_row, resultItems);
		setListAdapter(mAdapter);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		mClickedResult = resultItems.get(position);
		Utils.showOnMap(this, v.getContext(), mClickedResult);
    }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

		switch (item.getItemId()) {
		case 0:
			Utils.openWebsite(this, mClickedResult.getWebsite());
			break;
		case 1:
			Utils.callNumber(this, mClickedResult.getPhoneNumber());
			break;
		case 2:
			showDialog(ACCOUNT_LIST);
			break;
		case 3:
			Utils.showOnMap(this, this, mClickedResult);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		mClickedResult = resultItems.get(info.position);
		menu.setHeaderTitle(getString(R.string.choose));
				
		if(mClickedResult.getWebsite() != null) {
			menu.add(0, 0, 0, getString(R.string.openwww));
		}
		
		if(mClickedResult.getPhoneNumber() != null) {
			menu.add(0, 1, 0, getString(R.string.dial));			
		}
		
		menu.add(0, 2, 0, getString(R.string.savecontact));
		menu.add(0, 3, 0, getString(R.string.showOnMap));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	
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
		            	 
		            	 AddContact cAdder = new AddContact(mClickedResult, accountData, ResultsActivity.this);
		            	 String resultString = String.format(getString(R.string.contactSaved), mClickedResult.getName());
		            	 
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
	
	public void onCatsClick(View v) {
	    	Utils.goCategoriesList(this);
	 }
	 
	 public void onHomeClick(View v) {
	    	Utils.goHome(this);
	 }

}
