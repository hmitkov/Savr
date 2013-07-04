package com.proxiad.savr.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.adapter.SaveAdapter;
import com.proxiad.savr.common.NetworkUtil;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.social.TwitterManager;
import com.proxiad.savr.view.FBSave;

public abstract class TwitterFragment extends Fragment {
	public static Bitmap profilePic = null;
	protected ListView lvFBSaves;
	protected SaveAdapter adapter;
	protected List<FBSave> fbSaveList = new ArrayList<FBSave>();
	protected ProgressDialog progressDialog;

	// public static boolean socialLoaded = false;
	// public static boolean profileLoaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(getViewResourceId(), container, false);
		final HomeActivity homeActivity = (HomeActivity) getActivity().getParent();
		lvFBSaves = (ListView) view.findViewById(R.id.lvSocialSaves);
		lvFBSaves.setAdapter(adapter);
		lvFBSaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Save save = fbSaveList.get((int) id).getSave();
				HomeActivity.setSave(save);
				homeActivity.refresh();
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (TwitterManager.getInstance().isLoggedInAlready()) {
			if (HomeActivity.shouldUpdate || fbSaveList == null || fbSaveList.size() == 0 && NetworkUtil.isNetworkConnectionAvailable(getActivity())) {
				startTask();
			}
		}
	}

	protected abstract void setAdapter();

	public abstract void startTask();

	protected abstract int getViewResourceId();

}
