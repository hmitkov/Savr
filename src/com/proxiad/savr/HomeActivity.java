package com.proxiad.savr;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.proxiad.savr.animation.CollapseAnimation;
import com.proxiad.savr.animation.ExpandAnimation;
import com.proxiad.savr.common.SaveCategory;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;

@SuppressWarnings(value = "deprecation")
public class HomeActivity extends TabActivity {
	public static SaveCategory category = SaveCategory.restaurant;
	private static Save save;
	private static String flagMap;
	public static boolean shouldUpdate = false;

	private TabHost tabHost;
	// Sliding components
	private LinearLayout slidingPanel;
	private boolean isExpanded;
	private DisplayMetrics metrics;
	private LinearLayout menuPanel;
	private int panelWidth;
	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	CategoryChangeListener mCategoryListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ActionBar actionBar = getActionBar();
		actionBar.hide();
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);
		// // displaying custom ActionBar
		// View mActionBarView =
		// getLayoutInflater().inflate(R.layout.action_bar_custom, null);
		// actionBar.setCustomView(mActionBarView);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		setContentView(R.layout.activity_home);
		initSlidingMenu();
		setTabs();
	}

	void initSlidingMenu() {
		// Initialize sliding
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		panelWidth = (int) ((metrics.widthPixels) * 0.80);
		menuPanel = (LinearLayout) findViewById(R.id.menuPanel);
		menuPanelParameters = (FrameLayout.LayoutParams) menuPanel
				.getLayoutParams();
		menuPanelParameters.width = panelWidth;
		menuPanel.setLayoutParams(menuPanelParameters);
		slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
		slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel
				.getLayoutParams();
		slidingPanelParameters.width = metrics.widthPixels;
		slidingPanel.setLayoutParams(slidingPanelParameters);
		EditText searchText = (EditText) findViewById(R.id.edittext);
		searchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				changeCategorySearch(v);
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

				handled = true;
				// }
				return handled;
			}
		});
		searchText.setOnTouchListener(new OnTouchListener() {


			@Override
			public boolean onTouch(View searchEdit, MotionEvent event) {
				// TODO Auto-generated method stub
				EditText searchField = (EditText)searchEdit;
				searchField.setText("");
				return false;
			}
			
		});
		
		
		Typeface myriadPro = Typeface.createFromAsset(this.getAssets(),
				"MuseoSlab-700.ttf");
		searchText.setTypeface(myriadPro);
		initSlideMenuItems();
	}

	public void initSlideMenuItems() {
		int[] buttonViewIds = new int[] { R.id.btnSlideMenuCategoryDerniereSaves, R.id.btnSlideMenuCategoryRestaurnats,
				R.id.btnSlideMenuCategoryCinema, R.id.btnSlideMenuCategoryLivre,
				R.id.btnSlideMenuCategoryExposition, R.id.btnSlideMenuCategoryBar, R.id.btnSlideMenuCategoryHotel,
				R.id.btnSlideMenuCategoryShopping, R.id.btnSlideMenuCategoryTheatre,
				R.id.btnSlideMenuCategoryMusique, R.id.btnSlideMenuCategoryEvenement,
				R.id.btnSlideMenuCategoryDivers };
		Typeface myriadPro = Typeface.createFromAsset(this.getAssets(),
				"MuseoSlab-700.ttf");
		for (int i = 0; i < buttonViewIds.length; i++) {
			Button button = (Button) findViewById(buttonViewIds[i]);
			button.setTypeface(myriadPro);
		}
		
		
		int[] textViewIds = new int[] { R.id.tvCatDerSaves, R.id.tvCatRestNum,
				R.id.tvCatCinemaNum, R.id.tvCatLivreNum,
				R.id.tvCatExpositionNum, R.id.tvCatBarNum, R.id.tvCatHotelNum,
				R.id.tvCatShoppingNum, R.id.tvCatTheatreNum,
				R.id.tvCatMusiqueNum, R.id.tvCatEvenementNum,
				R.id.tvCatDiversNum };
		for (int i = 0; i < textViewIds.length; i++) {
			TextView tv = (TextView) findViewById(textViewIds[i]);
			SaveCategory category = getCategoryByViewId(textViewIds[i]);
			System.err.println("SUPER CATEGORY-->" + category);
			int num = PersistenceManager.getSaveListByCategory(this, category)
					.size();
			initTextView(tv, num);
		}
	}

	void initTextView(TextView tv, int num) {
		if (num > 0) {
			tv.setText(String.valueOf(num));
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Slides button that expands options menu from the left side of the screen
	 * This method is configured in action_bar_custom.xml
	 */
	public void onClickSlideButton(View view) {
		if (!isExpanded) {
			isExpanded = true;
			// Expand
			new ExpandAnimation(slidingPanel, panelWidth,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
		} else {
			isExpanded = false;
			// Collapse
			new CollapseAnimation(slidingPanel, panelWidth,
					TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);
		}
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void setTabs() {
		tabHost = getTabHost();
		addMenuButton(1, R.drawable.selector_tab_menu,
				new Intent(this, MySavesActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		addTab(2, R.drawable.selector_tab_places, new Intent(this,
				MapActivity.class));

		if (save == null) {
			addTabSaveIt(3, R.drawable.tab_save_it, new Intent(this,
					InitializationActivity.class));
		} else {
			addTabSaveIt(3, R.drawable.tab_save_it,
					new Intent(this, CardViewTipActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		}
		addTab(4, R.drawable.selector_tab_social, new Intent(this,
				SocialActivity.class));
		addTab(5, R.drawable.selector_tab_profile,
				new Intent(this, ProfileActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		tabHost.setCurrentTab(2);

		getTabWidget().getChildAt(2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						HomeActivity.setSave(null);
						HomeActivity.setFlagMap(null);
						refresh();
					}
				});
	}

	private void addMenuButton(int labelId, int drawableId, Intent intent) {
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		icon.setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View view) {
				if (!isExpanded) {
					isExpanded = true;
					// Expand
					new ExpandAnimation(slidingPanel, panelWidth,
							Animation.RELATIVE_TO_SELF, 0.0f,
							Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
				} else {
					isExpanded = false;
					// Collapse
					new CollapseAnimation(slidingPanel, panelWidth,
							TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
							TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f,
							0, 0.0f);
				}
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			
		});
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	private void addTab(int labelId, int drawableId, Intent intent) {
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	private void addTabSaveIt(int labelId, int drawableId, Intent intent) {
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator_saveit, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	public static Save getSave() {
		return save;
	}

	public static void setSave(Save save) {
		HomeActivity.save = save;
	}

	public void refresh() {
		getTabHost().invalidate();
		getTabHost().clearAllTabs();
		setTabs();
		initSlideMenuItems();
	}

	// Called from the sliding menu elements
	public void changeCategory(View view) {
		if (!isExpanded) {
			return;
		}
		int id = view.getId();
		category = getCategoryByViewId(id);
		System.err.println("category-->" + category);
		if (mCategoryListener != null) {
			mCategoryListener.onCategoryChange(category);
		}
		if (getTabHost().getCurrentTab() != 0) {
			getTabHost().setCurrentTab(0);
		}
		isExpanded = false;
		new CollapseAnimation(slidingPanel, panelWidth,
				TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
				TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);
	}

	public void changeCategorySearch(View view) {
		if (!isExpanded) {
			return;
		}
		// int id = view.getId();
		// category = getCategoryByViewId(id);
		EditText searchText = (EditText) view;
		System.err.println("searchText-->" + searchText.getText());
		if (mCategoryListener != null) {
			mCategoryListener.onSearch(searchText.getText().toString());
		}
		if (getTabHost().getCurrentTab() != 0) {
			getTabHost().setCurrentTab(0);
		}
		isExpanded = false;
		new CollapseAnimation(slidingPanel, panelWidth,
				TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
				TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);
	}

	private SaveCategory getCategoryByViewId(int id) {
		SaveCategory result = null;
		switch (id) {
		case R.id.btnSlideMenuCategoryDerniereSaves:
			result = SaveCategory.recent;
			break;
		case R.id.tvCatDerSaves:
			result = SaveCategory.recent;
			break;
		case R.id.btnSlideMenuCategoryRestaurnats:
			result = SaveCategory.restaurant;
			break;
		case R.id.tvCatRestNum:
			result = SaveCategory.restaurant;
			break;
		case R.id.btnSlideMenuCategoryCinema:
			result = SaveCategory.cinema;
			break;
		case R.id.tvCatCinemaNum:
			result = SaveCategory.cinema;
			break;
		case R.id.btnSlideMenuCategoryTheatre:
			result = SaveCategory.theatre;
			break;
		case R.id.tvCatTheatreNum:
			result = SaveCategory.theatre;
			break;
		case R.id.btnSlideMenuCategoryLivre:
			result = SaveCategory.livre;
			break;
		case R.id.tvCatLivreNum:
			result = SaveCategory.livre;
			break;
		case R.id.btnSlideMenuCategoryExposition:
			result = SaveCategory.exposition;
			break;
		case R.id.tvCatExpositionNum:
			result = SaveCategory.exposition;
			break;
		case R.id.btnSlideMenuCategoryBar:
			result = SaveCategory.bar;
			break;
		case R.id.tvCatBarNum:
			result = SaveCategory.bar;
			break;
		case R.id.btnSlideMenuCategoryHotel:
			result = SaveCategory.hotel;
			break;
		case R.id.tvCatHotelNum:
			result = SaveCategory.hotel;
			break;
		case R.id.btnSlideMenuCategoryShopping:
			result = SaveCategory.shopping;
			break;
		case R.id.tvCatShoppingNum:
			result = SaveCategory.shopping;
			break;
		case R.id.btnSlideMenuCategoryMusique:
			result = SaveCategory.musique;
			break;
		case R.id.tvCatMusiqueNum:
			result = SaveCategory.musique;
			break;
		case R.id.btnSlideMenuCategoryEvenement:
			result = SaveCategory.evenement;
			break;
		case R.id.tvCatEvenementNum:
			result = SaveCategory.evenement;
			break;
		case R.id.btnSlideMenuCategoryDivers:
			result = SaveCategory.diver;
			break;
		case R.id.tvCatDiversNum:
			result = SaveCategory.diver;
			break;
		default:
			result = SaveCategory.restaurant;
		}
		return result;
	}

	public interface CategoryChangeListener {
		void onCategoryChange(SaveCategory category);
		void onSearch(String searchWord);
	}

	public CategoryChangeListener getmCategoryListener() {
		return mCategoryListener;
	}

	public void setmCategoryListener(CategoryChangeListener mCategoryListener) {
		this.mCategoryListener = mCategoryListener;
	}

	public static String getFlagMap() {
		return flagMap;
	}

	public static void setFlagMap(String flagMap) {
		HomeActivity.flagMap = flagMap;
	}
	
}
