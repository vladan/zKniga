package com.gemo.zk.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.os.Handler;
import android.os.Message;

import com.gemo.zk.utils.ResultsAdapter;
import com.gemo.zk.utils.Utils;

public class Search {

	private ArrayList<Result> mAllResults;
	private ResultsAdapter mAdapter;
	private String mTerm;
	private String mLocation;
	private Handler mHandler;
	private int resultsCountHelper = 1;
	private int resCount;
		
	private static String mRequestUrl = "http://www.zk.com.mk/full/";

	public Search(Handler handler, ArrayList<Result> allResults, ResultsAdapter resAdapter) {
		mAdapter = resAdapter;
		mAllResults = allResults;
		mHandler = handler;
	}
	
	public void setTerm(String term) {
		mTerm = term;
	}
	
	public void setSearchLocation(String location) {
		mLocation = location;
	}
	
	public int getResultsCount() {
		String requestUrl = mRequestUrl;
		try {
			requestUrl += URLEncoder.encode(mTerm, "UTF-8") +"/" + URLEncoder.encode(mLocation, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		TagNode rootNode = getRootNode(requestUrl);
		
		int resultsCount = 1;
		Object[] len = evaluateXPath(Utils.XPATH_RESULT_COUNT, rootNode);
	    
		if(len.length > 0) {
			String str = ((StringBuffer) len[0]).toString();
		    String[] tempContent = str.split(" ");
			resultsCount = Integer.parseInt(tempContent[3]);
		}

		resCount = resultsCount;		
		return resultsCount;		
	}
		
	private Runnable resultAdded = new Runnable() {
	    public void run() {
	    	resultsCountHelper++;
	    	if(resultsCountHelper >= 1) {
	    		final Message m = mHandler.obtainMessage();
	    		m.what = 666;
	    		mHandler.sendMessage(m);
	    		resultsCountHelper = 0;
	    	}
	    	
			mAdapter.notifyDataSetChanged();
	    }
	};
	
	public void exec(int page) {
		String requestUrl = mRequestUrl;
		try {
			requestUrl += URLEncoder.encode(mTerm, "UTF-8") +"/" + URLEncoder.encode(mLocation, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		exec(requestUrl + "/" + Integer.toString(page));		
	}
	
	public void exec(String requestUrl) {
		
    	TagNode rootNode = getRootNode(requestUrl);		
    	int resLenght = evaluateXPath(Utils.XPATH_RESULTS_NUM, rootNode).length;
		if (resLenght > 0) {
			for(int res = 1; res <= resLenght; res++) {

				if(Thread.currentThread().isInterrupted()) {
					return;
				}
				
				Result result = new Result();			
				Object[] nameNodes = evaluateXPath(String.format(Utils.XPATH_RESULTS_NAME, Integer.toString(res)), rootNode);
				Object[] infoNodes = evaluateXPath(String.format(Utils.XPATH_RESULTS_ALL, Integer.toString(res)), rootNode);
				Object[] metaNodes = evaluateXPath(String.format(Utils.XPATH_RESULTS_METAINFO, Integer.toString(res)), rootNode);
				
				String websiteNode = null;
				if(metaNodes.length > 0) {
					websiteNode = (String) metaNodes[0];
				}
				
				String name = ((TagNode) nameNodes[0]).getText().toString();
				String address = ((TagNode) infoNodes[0]).getText().toString();
				String location = ((TagNode) infoNodes[1]).getText().toString();
				String phone = ((TagNode) infoNodes[2]).getText().toString();
				
				address = address.substring(8, address.length());
				location = location.substring(7, location.length());
				phone = phone.substring(9, phone.length()).replaceAll("\\D", "");

				result.setName(name);
				result.setAddress(address);
				result.setLocation(location);
				result.setPhoneNumber(phone);
				result.setWebsite(websiteNode);
				
				mAllResults.add(result);
				mHandler.post(resultAdded);
			}
		} else {
//			Utils.goHome(mContext);
		}
	}
	
	private Object[] evaluateXPath(String xpath, TagNode rootNode) {
        Object[] nodes = null;
		try {
			nodes = rootNode.evaluateXPath(xpath);
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		
		return nodes;	
	}
	
	private TagNode getRootNode(String requestUrl) {
		
		TagNode rootNode = null;
		
		HtmlCleaner mainCleaner = new HtmlCleaner();
    	CleanerProperties cProps = mainCleaner.getProperties();
    	cProps.setAllowHtmlInsideAttributes(false);
    	cProps.setAllowMultiWordAttributes(false);
    	cProps.setRecognizeUnicodeChars(true);
    	cProps.setOmitComments(true);
    	cProps.setOmitUnknownTags(true);
    	
    	URL url = null;
    	try {
			url = new URL(requestUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		URLConnection urlConn = null; 
		try {
			urlConn = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			rootNode = mainCleaner.clean(new InputStreamReader(urlConn.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rootNode;
	}
}
