package com.gemokodero.zk.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gemokodero.zk.R;
import com.gemokodero.zk.core.Result;

public class ResultsAdapter extends ArrayAdapter<Result> {

	private ArrayList<Result> items;
	private Context context;

	public ResultsAdapter(Context context, int textViewResourceId, ArrayList<Result> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.results_single_row, null);
		}
		Result result = items.get(position);
		if (result != null) {
			
			TextView name = (TextView) v.findViewById(R.id.name);
			TextView address = (TextView) v.findViewById(R.id.address);
			TextView location = (TextView) v.findViewById(R.id.location);
			TextView phonenumber = (TextView) v.findViewById(R.id.phonenumber);
			TextView website = (TextView) v.findViewById(R.id.website);
			
			String addressHelper = context.getString(R.string.noresult);
			String locationHelper = context.getString(R.string.noresult);
			String phonenumberHelper = context.getString(R.string.noresult);
			String websiteHelper = context.getString(R.string.noresult);
			
			if (name != null) {
				name.setText(result.getName());
			}
			
			if(address != null && result.getAddress() != null) {
				addressHelper = result.getAddress();
			}

			if(location != null && result.getLocation() != null) {
				locationHelper = result.getLocation();
			}

			if(phonenumber != null && result.getPhoneNumber() != null) {
				phonenumberHelper = result.getPhoneNumber();
			}

			if(website != null && result.getWebsite() != null) {
				websiteHelper = result.getWebsite();
			}
			
			address.setText(String.format(context.getString(R.string.address), addressHelper));
			location.setText(String.format(context.getString(R.string.location), locationHelper));
			phonenumber.setText(String.format(context.getString(R.string.phone), phonenumberHelper));
			website.setText(String.format(context.getString(R.string.website), websiteHelper));
		}
		
		return v;
	}

	@Override
	public int getCount() {
		return items.size();
	}


	public Result getItem(int position) {     
		return items.get(position);
	}

	public long getItemId(int position) {  
		return position;
	}
}