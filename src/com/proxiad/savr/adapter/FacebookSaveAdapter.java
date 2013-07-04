package com.proxiad.savr.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.widget.ProfilePictureView;
import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.view.FBSave;

public class FacebookSaveAdapter extends SocialSaveAdapter {

	public FacebookSaveAdapter(Context context, List<FBSave> fbSaveList) {
		super(context, fbSaveList);
	}

	// In facebook and twitter save list, there is a profile picture for each
	// save item. We use the same layout for both twitter and facebook save
	// list, so we need to dynamically include the profile picture
	@Override
	protected View initProfilePictureForSave(View newView, FBSave fbSave) {

		System.err.println("TEST FacebookSaveAdapter");
		LinearLayout llProfilePictureForSave = (LinearLayout) newView
				.findViewById(R.id.llProfilePictureForSave);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ProfilePictureView profilePic = (ProfilePictureView) layoutInflater
				.inflate(R.layout.profile_picture_facebook,
						llProfilePictureForSave, false);
		llProfilePictureForSave.removeAllViews();
		profilePic.setLayoutParams(new LinearLayout.LayoutParams(ActivityHelper
				.pxFromDp(75, (Activity) newView.getContext()), ActivityHelper
				.pxFromDp(75, (Activity) newView.getContext())));
		llProfilePictureForSave.addView(profilePic);

			System.err.println("REFRESH with remote data");
			profilePic.setProfileId(fbSave.getSocialNetworkId());
			System.err.println("profilePic w-->"+profilePic.getWidth()+" h-->"+profilePic.getHeight());

		return profilePic;
	}
}
