package com.proxiad.savr.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.view.FBSave;

public class TwitterSaveAdapter extends SocialSaveAdapter {
	Bitmap myProfileImage;

	public TwitterSaveAdapter(Context context, List<FBSave> fbSaveList, Bitmap image) {
		super(context, fbSaveList);
		myProfileImage = image;
	}

	// In facebook and twitter save list, there is a profile picture for each
	// save item.
	// This adapter is used in two cases:
	// 1. In profile activity, where we see all saves, shared by me. In this
	// case the profile picture is the same for all saves.
	// 2. In social activity, where we see all saves, shared by my friend. In
	// this case the profile picture for each save is different.
	// In case one we create the adapter with a picture, so we don't need to
	// load it for each item.
	//
	// In facebook and twitter save list, there is a profile picture for each
	// save item. We use the same layout for both twitter and facebook save
	// list, so we need to dynamically include the profile picture
	@Override
	protected View initProfilePictureForSave(View newView, FBSave fbSave) {
		LinearLayout llProfilePictureForSave = (LinearLayout) newView.findViewById(R.id.llProfilePictureForSave);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView profilePic = (ImageView) layoutInflater.inflate(R.layout.profile_picture_twitter, llProfilePictureForSave, false);
		if (myProfileImage != null) {
			profilePic.setImageBitmap(ActivityHelper.getRoundedBitmap(myProfileImage));
		} else {
			profilePic.setImageBitmap(ActivityHelper.getRoundedBitmap(fbSave.getSocialNetworkProfileImage()));
		}
		profilePic.setLayoutParams(new LinearLayout.LayoutParams(ActivityHelper.pxFromDp(75, (Activity)newView.getContext()),ActivityHelper.pxFromDp(75, (Activity)newView.getContext())));
		llProfilePictureForSave.removeAllViews();
		llProfilePictureForSave.addView(profilePic);
		return profilePic;

	}
}
