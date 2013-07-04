package com.proxiad.savr.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.CustomTextViewMuseo;
import com.proxiad.savr.view.FBSave;

public class SaveAdapter extends BaseAdapter {
	Context context;
	List<FBSave> fbSaveList;

	@Override
	public int getCount() {
		return fbSaveList.size();
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public Object getItem(int i) {
		return fbSaveList.get(i);
	}

	public SaveAdapter(Context context, List<FBSave> fbSaveList) {
		this.context = context;
		this.fbSaveList = fbSaveList;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		final int position = pos;
		FBSave fbSave = fbSaveList.get(position);
		View newView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView != null) {
			newView = convertView;
		} else {
			newView = inflater.inflate(getResource(), parent, false);
		}
		ImageView ivSaveImage = (ImageView) newView
				.findViewById(R.id.ivSaveImage);
		ivSaveImage.setImageBitmap(fbSave.getBmp());
		CustomTextViewMuseo tvNewspaper = (CustomTextViewMuseo) newView
				.findViewById(R.id.tvNewspaper);
		tvNewspaper.setText(fbSave.getSave().getAuteur());
		CustomTextViewMuseo tvAddress = (CustomTextViewMuseo) newView
				.findViewById(R.id.tvAddress);
		tvAddress.setText(fbSave.getSave().getAdresse());

		Date publishDate = fbSave.getDate();
		CustomTextViewMuseo friendName = (CustomTextViewMuseo) newView
				.findViewById(R.id.friendName);
		String publisher = fbSave.getPublisher() == null ? "" : fbSave
				.getPublisher();
		if (publishDate != null) {
			if (publisher != null && !publisher.equals("")) {
				friendName.setText(publisher
						+ Constants.STRING_COMMA
						+ ActivityHelper.getDateDifferenceInString(new Date(),
								publishDate));
			} else {
				friendName.setText(ActivityHelper.getDateDifferenceInString(
						new Date(), publishDate));
			}
		}else{
			friendName.setVisibility(View.GONE);
		}
		//
		tvAddress.setText(fbSave.getSave().getAdresse());

		View profilePic = initProfilePictureForSave(newView, fbSave);
		initRightPointer(newView);
		initNom(newView, fbSave);
		initStars(newView, fbSave);
		initDate(newView, fbSave, position, profilePic);
		return newView;

	}

	protected int getResource() {
		return R.layout.list_item_save;
	}

	protected View initProfilePictureForSave(View newView, FBSave fbSave) {
		System.err.println("initProfilePictureForSave-->null");
		return null;
	}
	protected View initRightPointer(View newView) {
		newView.findViewById(R.id.pointer_right).setVisibility(View.VISIBLE);
		return null;
	}

	protected void initDate(View newView, FBSave fbSave, int position,
			View profilePic) {
		return;
	}

	protected void initStars(View newView, FBSave fbSave) {
		ImageView[] ivStars = new ImageView[5];
		ivStars[0] = (ImageView) newView.findViewById(R.id.star1);
		ivStars[1] = (ImageView) newView.findViewById(R.id.star2);
		ivStars[2] = (ImageView) newView.findViewById(R.id.star3);
		ivStars[3] = (ImageView) newView.findViewById(R.id.star4);
		ivStars[4] = (ImageView) newView.findViewById(R.id.star5);
		ActivityHelper.initRatingLittleStars(context, fbSave.getSave()
				.getNote(), ivStars);
	}

	protected void initNom(View view, FBSave fbSave) {
		TextView tvNom = (TextView) view.findViewById(R.id.tvSaveNom);
		tvNom.setText(fbSave.getSave().getNom());
		int categoryResource = ActivityHelper.getCategoryResource(fbSave
				.getSave().getCategorie());
		Drawable drawableLeft = null;
		if (categoryResource != 0) {
			drawableLeft = context.getResources().getDrawable(categoryResource);
		}
		tvNom.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null,
				null);
		setCompoundDrawablePadding(tvNom);
	}

	protected void setCompoundDrawablePadding(TextView tv) {
		tv.setCompoundDrawablePadding(15);
	}

}
