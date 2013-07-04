package com.proxiad.savr;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.proxiad.savr.HomeActivity.CategoryChangeListener;
import com.proxiad.savr.adapter.SaveAdapter;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.SaveCategory;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;
import com.proxiad.savr.view.FBSave;

public class MySavesActivity extends Activity implements CategoryChangeListener {
	ListView lvMySaves;
	SaveAdapter adapter;
	List<FBSave> fbSaveList = null;
	TextView tvCategoryName;
	private GestureDetector gestureDetector = new GestureDetector(
			new MyGestureDetector());

	// private ProgressDialog progressDialog;
	@Override
	protected void onStart() {
		super.onStart();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_saves);
		tvCategoryName = (TextView) findViewById(R.id.tvCategoryName);
		final HomeActivity homeActivity = (HomeActivity) getParent();
		homeActivity.setmCategoryListener(this);
		initTvCategory();
		lvMySaves = (ListView) findViewById(R.id.lvSaves);

		lvMySaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Save save = fbSaveList.get((int) id).getSave();
				homeActivity.setSave(save);
				homeActivity.refresh();
			}
		});
		lvMySaves.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	void initTvCategory() {
		tvCategoryName.setText(getString(HomeActivity.category
				.getStringResource()));
	}

	void initFBSaves(List<Save> saveList) {
		fbSaveList = new ArrayList<FBSave>();
		for (Save save : saveList) {
			Bitmap bmp = ActivityHelper.readImageFromDisk(this, save);
			fbSaveList.add(new FBSave(save, null, null, bmp, null, null));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (fbSaveList == null || fbSaveList.size() == 0) {
			List<Save> saveList = com.proxiad.savr.persistence.PersistenceManager
					.getSaveListByCategory(this, HomeActivity.category);
			initFBSaves(saveList);
			adapter = new SaveAdapter(this, fbSaveList);
			lvMySaves.setAdapter(adapter);
		}
	}

	@Override
	public void onCategoryChange(SaveCategory category) {
		System.err.println("onCategoryChange--<>"+category);
		List<Save> saveList = PersistenceManager.getSaveListByCategory(this,
				category);
		initFBSaves(saveList);
		adapter = new SaveAdapter(this, fbSaveList);
		lvMySaves.setAdapter(adapter);
		initTvCategory();
	}
	
	@Override
	public void onSearch(String searchWord) {
		System.err.println("onSearch--<>"+searchWord);
		List<Save> saveList = PersistenceManager.getSaveListByCategory(this,
				searchWord);
		initFBSaves(saveList);
		adapter = new SaveAdapter(this, fbSaveList);
		lvMySaves.setAdapter(adapter);
		initTvCategory();
	}

	private void openDeleteDialog(final FBSave fbSave) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.msg_delete);
		dialog.setMessage(R.string.msg_delete_item);

		dialog.setPositiveButton(R.string.btn_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int i) {
						deleteSave(fbSave);
					}
				});
		dialog.setNegativeButton(R.string.btn_cancel, null);
		dialog.show();
	}

	private void deleteSave(FBSave fbSave) {
		PersistenceManager.deleteSave(this, fbSave.getSave());
		fbSaveList.remove(fbSave);
		adapter.notifyDataSetChanged();
		((HomeActivity) this.getParent()).initSlideMenuItems();
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				float sensitvity = 50;
				if ((e2.getX() - e1.getX()) > sensitvity) {
					// Swipe Left;
					int id = lvMySaves.pointToPosition((int) e1.getX(),
							(int) e1.getY());
					FBSave fbSave = fbSaveList.get(id);
					openDeleteDialog(fbSave);
					return true;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
}
