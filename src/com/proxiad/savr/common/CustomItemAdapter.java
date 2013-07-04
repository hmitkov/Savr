package com.proxiad.savr.common;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;


public class CustomItemAdapter extends SimpleAdapter {


	public CustomItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		
		super(context, data, resource, from, to);
	}

/*	@Override
	public void setViewImage(ImageView v, String value) {
		
		
	}*/

}
