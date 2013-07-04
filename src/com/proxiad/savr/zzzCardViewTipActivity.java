package com.proxiad.savr;

import java.io.InputStream;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proxiad.savr.async.AsyncOperation;
import com.proxiad.savr.async.AsyncOperation.AsyncOperationCallback;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.FileUtil;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;

public class zzzCardViewTipActivity extends FragmentActivity {

	LinearLayout llProgress;
	FrameLayout flSendCode;
	TextView tvMessages;
	Button btnSendOrSave;
	static AsyncOperation<?, ?, ?> mOperation = null;

	// prix, adresse, artistes, numero, date_debut, date_fin, horaires, marque,
	// genre, acteurs, label, groupe
	public void setValue(TextView view, String labelValue, Save newSave, Typeface museoSlab) {
		if (labelValue.equals("prix")) {
			view.setText(newSave.getPrix());
		} else if (labelValue.equals("adresse")) {
			view.setText(newSave.getAdresse());
		} else if (labelValue.equals("artistes")) {
			view.setText(newSave.getArtistes());
		} else if (labelValue.equals("numero")) {
			view.setText(newSave.getNumero());
		} else if (labelValue.equals("date_debut")) {
			view.setText(newSave.getDate_debut());
		} else if (labelValue.equals("date_fin")) {
			view.setText(newSave.getDate_fin());
		} else if (labelValue.equals("horaires")) {
			view.setText(newSave.getHoraires());
		} else if (labelValue.equals("marque")) {
			view.setText(newSave.getMarque());
		} else if (labelValue.equals("acteurs")) {
			view.setText(newSave.getActeurs());
		} else if (labelValue.equals("label")) {
			view.setText(newSave.getLabel());
		} else if (labelValue.equals("groupe")) {
			view.setText(newSave.getGroupe());
		} else if (labelValue.equals("genre")) {
			view.setText(newSave.getGenre());
		} else if (labelValue.equals("realisateur")) {
			view.setText(newSave.getRealisateur());
		} else if (labelValue.equals("type_de_cuisine")) {
			view.setText(newSave.getType_de_cuisine());
		} else if (labelValue.equals("auteur")) {
			view.setText(newSave.getAuteur());
		} else if (labelValue.equals("edition")) {
			view.setText(newSave.getEdition());
		}

		view.setTypeface(museoSlab);
		String text = view.getText().toString();
		if (text.trim().toUpperCase().startsWith("WWW")) {
			text = "http://" + text;
		}
		if (text.trim().toUpperCase().startsWith("HTTP")) {
			view.setText(text);
			view.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Uri uri = Uri.parse(((TextView) view).getText().toString());
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}
			});
		}

	}

	private RelativeLayout.LayoutParams getRightValueParams() {

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.setMargins(ActivityHelper.pxFromDp(100, this), ActivityHelper.pxFromDp(0, this), ActivityHelper.pxFromDp(0, this), ActivityHelper.pxFromDp(1, this));
		return params;
	}

	private RelativeLayout.LayoutParams getLeftValueParams() {

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.setMargins(ActivityHelper.pxFromDp(24, this), ActivityHelper.pxFromDp(0, this), ActivityHelper.pxFromDp(100, this), ActivityHelper.pxFromDp(1, this));
		return params;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_view_tips);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setLayout();
	};

	protected void setLayout() {

		Typeface myriadPro = Typeface.createFromAsset(getAssets(), "MyriadPro-Semibold.ttf");
		Typeface museoSlab = Typeface.createFromAsset(getAssets(), "MuseoSlab-700.ttf");

		HomeActivity homeActivity = (HomeActivity) getParent();
		Save newSave = homeActivity.getSave();
		ImageView heart = (ImageView) findViewById(R.id.heart);
		((TextView) findViewById(R.id.likes)).setText(newSave.getLikes() + "");
		if (newSave.getIsLiked()) {
			heart.setImageResource(R.drawable.heartpressed);
		} else {

			heart.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (!((HomeActivity) getParent()).getSave().getIsLiked()) {
						Save updated = PersistenceManager.updateLikes((HomeActivity) getParent(), ((HomeActivity) getParent()).getSave());
						((HomeActivity) getParent()).setSave(updated);
						setLayout();
					}
				}
			});
		}
		String imageUrl = newSave.getPhoto();

		ImageView ivItemImage = (ImageView) findViewById(R.id.itemImage);
		Display display = getWindowManager().getDefaultDisplay();
		Bitmap bmp = BitmapFactory.decodeFile(FileUtil.getSaveImageFile(this, newSave).getPath());
		// If the image is not saved in file, then try to download it. This
		// happens in the case when a save from facebook is displayed.
		if (bmp == null) {
			new DownloadImageTask(ivItemImage).execute(Constants.SERVER_HOSTNAME + imageUrl);
		} else {
			ivItemImage.setImageBitmap(bmp);
			ivItemImage.setMinimumWidth(display.getWidth());
			ivItemImage.setMaxWidth(display.getWidth());
			ivItemImage.setMinimumHeight(ActivityHelper.dpFromPx(60, getParent()));
		}

		for (int i = 0; i < newSave.getCles().length; i++) {
			if (i == 0) {
				TextView fistLabel = (TextView) findViewById(R.id.fistLabel);
				fistLabel.setText(newSave.getCles()[i]);
				fistLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.firstValue), newSave.getCles()[i], newSave, museoSlab);
				RelativeLayout.LayoutParams params = getLeftValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.fistLabel);

				View firstValue = ((View) findViewById(R.id.firstValue));
				firstValue.setLayoutParams(params);
			} else if (i == 1) {
				TextView secondLabel = (TextView) findViewById(R.id.secondLabel);
				secondLabel.setText(newSave.getCles()[i]);
				secondLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.secondValue), newSave.getCles()[i], newSave, museoSlab);

				RelativeLayout.LayoutParams params = getRightValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.secondLabel);
				params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.secondLabel);

				View secondValue = ((View) findViewById(R.id.secondValue));
				secondValue.setLayoutParams(params);

			} else if (i == 2) {
				TextView thirdLabel = (TextView) findViewById(R.id.thirdLabel);
				thirdLabel.setText(newSave.getCles()[i]);
				thirdLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.thirdValue), newSave.getCles()[i], newSave, museoSlab);
				RelativeLayout.LayoutParams params = getLeftValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.thirdLabel);

				View thirdValue = ((View) findViewById(R.id.thirdValue));
				thirdValue.setLayoutParams(params);
			} else if (i == 3) {
				TextView fourthLabel = (TextView) findViewById(R.id.fourthLabel);
				fourthLabel.setText(newSave.getCles()[i]);
				fourthLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.fourthValue), newSave.getCles()[i], newSave, museoSlab);

				RelativeLayout.LayoutParams params = getRightValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.fourthLabel);
				params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.fourthLabel);

				View fourthValue = ((View) findViewById(R.id.fourthValue));
				fourthValue.setLayoutParams(params);
			} else if (i == 4) {
				TextView fifthLabel = (TextView) findViewById(R.id.fifthLabel);
				fifthLabel.setText(newSave.getCles()[i]);
				fifthLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.fifthValue), newSave.getCles()[i], newSave, museoSlab);

				RelativeLayout.LayoutParams params = getLeftValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.fifthLabel);

				View fifthValue = ((View) findViewById(R.id.fifthValue));
				fifthValue.setLayoutParams(params);

			} else if (i == 5) {
				TextView sixthLabel = (TextView) findViewById(R.id.sixthLabel);
				sixthLabel.setText(newSave.getCles()[i]);
				sixthLabel.setTypeface(museoSlab);
				setValue((TextView) findViewById(R.id.sixthValue), newSave.getCles()[i], newSave, museoSlab);
				RelativeLayout.LayoutParams params = getRightValueParams();
				params.addRule(RelativeLayout.BELOW, R.id.sixthLabel);
				params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.sixthLabel);

				View sixthValue = ((View) findViewById(R.id.sixthValue));
				sixthValue.setLayoutParams(params);
			}
		}

		TextView labelArticle = (TextView) findViewById(R.id.labelArticle);
		labelArticle.setTypeface(museoSlab);

		TextView article = (TextView) findViewById(R.id.article);

		RelativeLayout.LayoutParams paramsArticle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		paramsArticle.setMargins(ActivityHelper.dpFromPx(11, this), ActivityHelper.dpFromPx(9, this), ActivityHelper.pxFromDp(11, this), ActivityHelper.pxFromDp(9, this));

		paramsArticle.addRule(RelativeLayout.BELOW, R.id.labelArticle);
		paramsArticle.addRule(RelativeLayout.ALIGN_LEFT, R.id.labelArticle);

		article.setText(newSave.getArticle());

		article.setTypeface(museoSlab);
		article.setLayoutParams(paramsArticle);

		TextView itemName = (TextView) findViewById(R.id.itemName);
		itemName.setText(newSave.getNom());
		itemName.setTypeface(museoSlab);

		TextView itemCode = (TextView) findViewById(R.id.itemCode);
		itemCode.setText(newSave.getCode());
		itemCode.setTypeface(myriadPro);

		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		if (map != null && newSave.getGeocode() != null && !newSave.getGeocode().equals("")) {
			String rareGeoCode = newSave.getGeocode().trim().substring(1, newSave.getGeocode().length() - 1);
			String geoCodeFirstPart = rareGeoCode.substring(0, rareGeoCode.indexOf(",")).trim();
			String geoCodeSecondPart = rareGeoCode.substring(rareGeoCode.indexOf(",") + 1).trim();

			map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(geoCodeFirstPart), Double.parseDouble(geoCodeSecondPart))).title(newSave.getNom()).snippet(getString(newSave.getCategorie().getStringResource())).draggable(false));
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(geoCodeFirstPart), Double.parseDouble(geoCodeSecondPart)));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

			map.moveCamera(center);
			map.animateCamera(zoom);
		} else {
			((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getView().setVisibility(View.GONE);

		}
		String stars = newSave.getNote();
		double starsRate = Double.parseDouble(stars.trim());
		Resources res = getResources();
		if (starsRate < 1 && starsRate > 0) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.midstar));
		} else if (starsRate == 1) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));

		} else if (starsRate < 2 && starsRate > 1) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.midstar));
		} else if (starsRate == 2) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
		} else if (starsRate < 3 && starsRate > 2) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.midstar));
		} else if (starsRate == 3) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.redstar));
		} else if (starsRate < 4 && starsRate > 3) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star4)).setImageDrawable(res.getDrawable(R.drawable.midstar));
		} else if (starsRate == 4) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star4)).setImageDrawable(res.getDrawable(R.drawable.redstar));
		} else if (starsRate < 5 && starsRate > 4) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star4)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star5)).setImageDrawable(res.getDrawable(R.drawable.midstar));
		} else if (starsRate == 5) {
			((ImageView) findViewById(R.id.star1)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star2)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star3)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star4)).setImageDrawable(res.getDrawable(R.drawable.redstar));
			((ImageView) findViewById(R.id.star5)).setImageDrawable(res.getDrawable(R.drawable.redstar));
		}
		int textSizeFirstValue = ((TextView) findViewById(R.id.firstValue)).getText().length();
		int textSizeSecondValue = ((TextView) findViewById(R.id.firstValue)).getText().length();
		int textSizeThirdValue = ((TextView) findViewById(R.id.thirdValue)).getText().length();
		int textSizeFourthValue = ((TextView) findViewById(R.id.fourthValue)).getText().length();
		if (newSave.getCles().length < 5) {

			TextView fifthLabel = (TextView) findViewById(R.id.fifthLabel);
			fifthLabel.setText("");
			fifthLabel.setHeight(0);
			TextView fifthValue = (TextView) findViewById(R.id.fifthValue);
			fifthValue.setText("");
			fifthValue.setHeight(0);
			TextView sixthLabel = (TextView) findViewById(R.id.sixthLabel);
			sixthLabel.setText("");
			sixthLabel.setHeight(0);
			TextView sixthValue = (TextView) findViewById(R.id.sixthValue);
			sixthValue.setText("");
			sixthValue.setHeight(0);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ActivityHelper.pxFromDp(100, this));

			params.setMargins(ActivityHelper.dpFromPx(11, this), ActivityHelper.dpFromPx(9, this), ActivityHelper.dpFromPx(11, this), 0);

			if (textSizeFirstValue + textSizeThirdValue > textSizeSecondValue + textSizeFourthValue) {
				params.addRule(RelativeLayout.BELOW, R.id.thirdValue);
			} else {
				params.addRule(RelativeLayout.BELOW, R.id.fourthValue);
			}

			View mapView = ((View) findViewById(R.id.map));
			mapView.setLayoutParams(params);
		} else {
			if (newSave.getCles().length == 5) {
				TextView sixthLabel = (TextView) findViewById(R.id.sixthLabel);
				sixthLabel.setText("");
				sixthLabel.setHeight(0);
				TextView sixthValue = (TextView) findViewById(R.id.sixthValue);
				sixthValue.setText("");
				sixthValue.setHeight(0);
			}
			int textSizeFifthValue = ((TextView) findViewById(R.id.fifthValue)).getText().length();
			int textSizeSixthValue = ((TextView) findViewById(R.id.sixthValue)).getText().length();

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ActivityHelper.pxFromDp(100, this));

			params.setMargins(ActivityHelper.pxFromDp(24, this), ActivityHelper.pxFromDp(20, this), ActivityHelper.pxFromDp(24, this), 0);
			if (textSizeFirstValue + textSizeThirdValue + textSizeFifthValue > textSizeSecondValue + textSizeFourthValue + textSizeSixthValue) {
				params.addRule(RelativeLayout.BELOW, R.id.fifthValue);
			} else {
				params.addRule(RelativeLayout.BELOW, R.id.sixthValue);
			}

			View mapView = ((View) findViewById(R.id.map));
			mapView.setLayoutParams(params);

		}

		ImageView mapView = ((ImageView) findViewById(R.id.itemImage));
		mapView.setMinimumHeight(ActivityHelper.dpFromPx(64, getParent()));
		mapView.setMaxHeight(ActivityHelper.dpFromPx(64, getParent()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_initialization, menu);
		return true;
	}

	class SendCodeCallback implements AsyncOperationCallback {

		public SendCodeCallback() {
			super();

		}

		@Override
		public void onPreExecute() {
			tvMessages.setVisibility(View.GONE);
			flSendCode.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.GONE);
			llProgress.setVisibility(View.VISIBLE);

		}

		@Override
		public void onPostExecute() {
			mOperation = null;
			// TODO change tab
			// Intent intent = new Intent();
			// intent.setClass(InitializationActivity.this, HomeActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
		}

		@Override
		public void onProgress(int progress) {
		}

		@Override
		public void onCancel() {
			mOperation = null;
		}

		@Override
		public void onError(String msg) {
			tvMessages.setVisibility(View.VISIBLE);
			tvMessages.setText(msg);
			flSendCode.setVisibility(View.VISIBLE);
			llProgress.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.VISIBLE);
			mOperation = null;
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_initialization, menu);
	// return true;
	// }
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			Display display = getWindowManager().getDefaultDisplay();
			bmImage.setImageBitmap(result);
			bmImage.setMinimumWidth(display.getWidth());
			bmImage.setMaxWidth(display.getWidth());
			bmImage.setMinimumHeight(ActivityHelper.dpFromPx(60, getParent()));

		}
	}

}
