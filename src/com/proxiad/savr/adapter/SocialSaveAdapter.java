package com.proxiad.savr.adapter;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.CustomTextViewMuseo;
import com.proxiad.savr.view.FBSave;

public class SocialSaveAdapter extends SaveAdapter {
	public SocialSaveAdapter(Context context, List<FBSave> fbSaveList) {
		super(context, fbSaveList);
	}

	@Override
	protected int getResource() {
		return R.layout.list_item_social_save;
	}

	protected void initDatex(View view, FBSave fbSave) {
		Date fbDate = fbSave.getDate();
		String monthStr = null;
		int day = 0;
		TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
		if (fbDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(fbDate);
			monthStr = DateFormatSymbols.getInstance().getMonths()[c.get(Calendar.MONTH)];
			day = c.get(Calendar.DAY_OF_MONTH);
		}
		if (monthStr != null && day > 0) {
			tvDate = (TextView) view.findViewById(R.id.tvDate);
			tvDate.setText(day + "\n" + monthStr);
		} else {
			tvDate.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void initDate(View newView, FBSave fbSave, int position, View profilePic) {
		System.err.println("TEST DATE!");
		FBSave previousFbSave = null;
		if (position != 0) {
			previousFbSave = fbSaveList.get(position - 1);
		}
		LinearLayout fbcontainer = (LinearLayout) newView.findViewById(R.id.llProfilePictureForSave);
		
		
		
		
		ImageView pointer = (ImageView) newView.findViewById(R.id.pointer);
		LinearLayout fbdata = (LinearLayout) newView.findViewById(R.id.include);
		Date fbDate = fbSave.getDate();
		Date previousfbDate = null;
		TextView tvDate = (TextView) newView.findViewById(R.id.tvDate);
	//	ImageView tvDateImage = (ImageView) newView.findViewById(R.id.tvDateImage);
		if (previousFbSave != null) {
			previousfbDate = previousFbSave.getDate();
		}
//		if (previousfbDate != null) {
//			System.err.println("previousfbDate->" + ActivityHelper.setTimeToMidnight(previousfbDate));
//		} else {
//			System.err.println("previousfbDate->null");
//		}
//		System.err.println("fbDate->" + ActivityHelper.setTimeToMidnight(fbDate));
		System.err.println("fbDate-->"+fbDate+" previousfbDate-->"+previousfbDate);
		if (previousfbDate != null && ActivityHelper.setTimeToMidnight(fbDate).compareTo(ActivityHelper.setTimeToMidnight(previousfbDate)) == 0) {
			tvDate.setVisibility(View.GONE);
			//tvDateImage.setVisibility(View.GONE);
			fbcontainer.setPadding(ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));

			CustomTextViewMuseo saveName = (CustomTextViewMuseo) fbdata.findViewById(R.id.tvSaveNom);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) saveName.getLayoutParams();
			layoutParams.setMargins(ActivityHelper.pxFromDp(10, (Activity) context), ActivityHelper.pxFromDp(20, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));
			saveName.setLayoutParams(layoutParams);

			profilePic.setPadding(ActivityHelper.pxFromDp(13, (Activity) context), ActivityHelper.pxFromDp(10, (Activity) context), ActivityHelper.pxFromDp(13, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));
			// pointer.set

			MarginLayoutParams marginParams = new MarginLayoutParams(pointer.getLayoutParams());
			marginParams.setMargins(ActivityHelper.pxFromDp(72, (Activity) context), ActivityHelper.pxFromDp(36, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));
			FrameLayout.LayoutParams layoutParamsPointer = new FrameLayout.LayoutParams(marginParams);
			pointer.setLayoutParams(layoutParamsPointer);

			
			System.err.println("SET DATE missing ball-->w-->"+profilePic.getWidth()+" h-->"+profilePic.getHeight());
			// MarginLayoutParams marginParams = new
			// MarginLayoutParams(pointer.getLayoutParams());
			// marginParams.setMargins(ActivityHelper.pxFromDp(72,
			// (Activity)context), ActivityHelper.pxFromDp(88,
			// (Activity)context), ActivityHelper.pxFromDp(0,
			// (Activity)context), ActivityHelper.pxFromDp(0,
			// (Activity)context));
			// pointer.setPadding(ActivityHelper.pxFromDp(72,
			// (Activity)context), ActivityHelper.pxFromDp(88,
			// (Activity)context), ActivityHelper.pxFromDp(0,
			// (Activity)context), ActivityHelper.pxFromDp(0,
			// (Activity)context));
			// RelativeLayout.LayoutParams layoutParamsPointer = new
			// RelativeLayout.LayoutParams(marginParams);
			// pointer.setLayoutParams(marginParams);

//			System.err.println("SET DATE NULL");
		} else {

			MarginLayoutParams marginParams = new MarginLayoutParams(pointer.getLayoutParams());
			marginParams.setMargins(ActivityHelper.pxFromDp(72, (Activity) context), ActivityHelper.pxFromDp(108, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));
			FrameLayout.LayoutParams layoutParamsPointer = new FrameLayout.LayoutParams(marginParams);
			pointer.setLayoutParams(layoutParamsPointer);

			fbcontainer.setPadding(ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(60, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));

			CustomTextViewMuseo saveName = (CustomTextViewMuseo) fbdata.findViewById(R.id.tvSaveNom);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) saveName.getLayoutParams();
			layoutParams.setMargins(ActivityHelper.pxFromDp(10, (Activity) context), ActivityHelper.pxFromDp(92, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));
			saveName.setLayoutParams(layoutParams);

			profilePic.setPadding(ActivityHelper.pxFromDp(13, (Activity) context), ActivityHelper.pxFromDp(30, (Activity) context), ActivityHelper.pxFromDp(13, (Activity) context), ActivityHelper.pxFromDp(0, (Activity) context));

			System.err.println("SET DATE-->w-->"+profilePic.getWidth()+" h-->"+profilePic.getHeight());
			String monthStr = null;
			int day = 0;

			if (fbDate != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(fbDate);
				monthStr = DateFormatSymbols.getInstance().getMonths()[c.get(Calendar.MONTH)];
				day = c.get(Calendar.DAY_OF_MONTH);
			}
			if (monthStr != null && day > 0) {
				tvDate.setVisibility(View.VISIBLE);
				//tvDateImage.setVisibility(View.VISIBLE);
				// tvDate = (TextView) view.findViewById(R.id.tvDate);
				tvDate.setText(day + "\n" + monthStr);
			} else {
				tvDate.setVisibility(View.INVISIBLE);
			}
		}
		
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 40;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	@Override
	protected View initRightPointer(View newView) {
		return null;
	}
}
