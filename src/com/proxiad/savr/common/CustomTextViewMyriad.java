package com.proxiad.savr.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewMyriad extends TextView {

	public CustomTextViewMyriad(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewMyriad(Context context) {
		super(context);
		init();
	}

	public CustomTextViewMyriad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		Typeface myriadPro = Typeface.createFromAsset(getContext().getAssets(),
				"MyriadPro-Semibold.ttf");
		setTypeface(myriadPro);
		
	}
	public void setDefaultColor(){
		//light_blackD101101101
		setTextColor(Color.parseColor("#656565"));
		getText();
	}
	public String getString(){
		return getText().toString();
	}
}