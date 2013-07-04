package com.proxiad.savr.common;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proxiad.savr.R;
import com.proxiad.savr.view.FBSave;

public class CustomListView extends ListView implements AbsListView.OnScrollListener {

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setOnScrollListener(this);
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOnScrollListener(this);
	}

	public CustomListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setOnScrollListener(this);
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		if (totalItemCount > 0) {
			FBSave fbSave = (FBSave) view.getItemAtPosition(firstVisibleItem);

			if (view != null) {
				RelativeLayout linearLayoutParent = (RelativeLayout) view.getParent();
				TextView tvDateHeader = (TextView) linearLayoutParent.findViewById(R.id.tvDate2);

				Date fbDate = fbSave.getDate();
				String monthStr = null;
				int day = 0;

				if (tvDateHeader != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(fbDate);
					monthStr = DateFormatSymbols.getInstance().getMonths()[c.get(Calendar.MONTH)];
					day = c.get(Calendar.DAY_OF_MONTH);
				}
				if (monthStr != null && day > 0) {
					tvDateHeader.setText(day + "\n" + monthStr);
				} else {
					tvDateHeader.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

}
