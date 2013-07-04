package com.proxiad.savr.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.proxiad.savr.R;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;

public class ActivityHelper {
	public static void openMessageDialog(Context context, String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.msg_error);
		dialog.setMessage(message);

		dialog.setPositiveButton(R.string.btn_ok, null);
		dialog.show();
	}

	public static SpannableStringBuilder getErrorMessage(Context context, int resource) {
		int ecolor = R.color.msg_red;
		String estring = context.getString(resource);
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
		ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
		return ssbuilder;
	}

	public static void initRating(Context context, String stars, ImageView[] views) {
		double starsRate = Double.parseDouble(stars.trim());
		Resources res = context.getResources();
		int i = 0;
		for (i = 1; i <= starsRate; i++) {
			// check if the rating (which comes from the database) is greater
			// than 5, because we have only 5 stars
			if (i > 5) {
				break;
			}
			views[i - 1].setImageDrawable(res.getDrawable(R.drawable.redstar));

		}
		if (starsRate - i + 1 > 0) {
			views[i - 1].setImageDrawable(res.getDrawable(R.drawable.midstar));
		}
	}

	public static void initRatingLittleStars(Context context, String stars, ImageView[] views) {
		System.err.println("initRatingLittleStars-->"+stars);
		double starsRate = Double.parseDouble(stars.trim());
		Resources res = context.getResources();
		views[0].setImageDrawable(res.getDrawable(R.drawable.l_grey_star));
		views[1].setImageDrawable(res.getDrawable(R.drawable.l_grey_star));
		views[2].setImageDrawable(res.getDrawable(R.drawable.l_grey_star));
		views[3].setImageDrawable(res.getDrawable(R.drawable.l_grey_star));
		views[4].setImageDrawable(res.getDrawable(R.drawable.l_grey_star));
		
		int i = 0;
		for (i = 1; i <= starsRate; i++) {
			// check if the rating (which comes from the database) is greater
			// than 5, because we have only 5 stars
			if (i > 5) {
				break;
			}
			views[i - 1].setImageDrawable(res.getDrawable(R.drawable.l_red_star));

		}
		if (starsRate - i + 1 > 0) {
			views[i - 1].setImageDrawable(res.getDrawable(R.drawable.l_mid_star));
		}
	}

	public static int getCategoryResource(SaveCategory category) {
		int result;
		switch (category) {
		case restaurant:
			result = R.drawable.list_view_pin_restaurants;
			break;
		case cinema:
			result = R.drawable.list_view_pin_cinema;
			break;
		case livre:
			result = R.drawable.list_view_pin_books;
			break;
		case exposition:
			result = R.drawable.list_view_pin_expos;
			break;
		case bar:
			result = R.drawable.list_view_pin_clubs;
			break;
		case theatre:
			result = R.drawable.list_view_pin_theatre;
			break;
		case shopping:
			result = R.drawable.list_view_pin_shopping;
			break;
		case musique:
			result = R.drawable.list_view_pin_music;
			break;
		case hotel:
			result = R.drawable.list_view_pin_hotels;
			break;
		case evenement:
			result = R.drawable.list_view_pin_events;
			break;
		case autre:
			result = R.drawable.list_view_pin_others;
			break;
		case concert:
			result = R.drawable.list_view_pin_clubs;
			break;
		default:
			result = 0;
			break;
		}
		return result;
	}

	public static int dpFromPx(int px, Activity activity) {
		return (int) (px / activity.getResources().getDisplayMetrics().density);
	}

	public static int pxFromDp(int dp, Activity activity) {
		return (int) (dp * activity.getResources().getDisplayMetrics().density);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap loadAndStoreBMP(Context context, Save save) {
		// Try to read from disk. If it is not there, then download, round and
		// save
		Bitmap bmp = readImageFromDisk(context, save);
		if (bmp == null) {
			if (save.getPhoto() != null) {
				bmp = ServerHelper.downloadImage(Constants.SERVER_HOSTNAME + save.getPhoto());
				if (bmp != null) {
					bmp = ActivityHelper.getRoundedCornerBitmap(bmp, Constants.ROUND_IMAGE_CORNERS);
					try {
						storeImageToDisk(context, bmp, save);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bmp;
	}

	public static Bitmap readImageFromDisk(Context context, Save save) {
		return BitmapFactory.decodeFile(FileUtil.getSaveImageFile(context, save).getPath());
	}

	public static void storeImageToDisk(Context context, Bitmap bitmap, Save save) throws Exception {
		File imageFile = FileUtil.getSaveImageFile(context, save);
		imageFile.createNewFile();
		FileOutputStream out = new FileOutputStream(imageFile);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	}

	public static Date parseDate(String dateStr) throws Exception {
		DateTime dt = DateTime.parse(dateStr);
		Date date = dt.toDate();
		return date;
	}

	public static Date setTimeToMidnight(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static String getDateDifferenceInString(Date newerDate, Date olderDate) {
		long diff = newerDate.getTime() - olderDate.getTime();

		String differenceInString = new String();
		long realDiff = diff / (1000); // sec
		differenceInString = new String(Constants.STRING_ILYA + realDiff + Constants.STRING_SEC);
		if (realDiff > 1) {
			differenceInString = differenceInString + Constants.STRING_ES;
		}
		if (realDiff > 60) {
			realDiff = realDiff / 60; // min
			differenceInString = new String(Constants.STRING_ILYA + realDiff + Constants.STRING_MIN);
			if (realDiff > 1) {
				differenceInString = differenceInString + Constants.STRING_S;
			}
		}
		if (realDiff > 60) {
			realDiff = realDiff /  60; // hours
			differenceInString = new String(Constants.STRING_ILYA + realDiff + Constants.STRING_HOUR);
			if (realDiff > 1) {
				differenceInString = differenceInString + Constants.STRING_S;
			}
		}
		if (realDiff > 60) {
			realDiff = realDiff / 24; // days
			differenceInString = new String(Constants.STRING_ILYA + realDiff + Constants.STRING_DAYS);
			if (realDiff > 1) {
				differenceInString = differenceInString + Constants.STRING_S;
			}
		}
		if (realDiff > 31) {
			realDiff = realDiff / 31; // months
			differenceInString = new String(Constants.STRING_ILYA + realDiff + Constants.STRING_MONTH);
		}
		
		return differenceInString;
	}
	
	
	
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {


        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
	
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	 
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 100;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	  //  paint.setStrokeWidth(10);
	  //  paint.setStyle(Paint.Style.STROKE);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	    
	  }
	
	public static Bitmap getRoundedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	 
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 10;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	  //  paint.setStrokeWidth(10);
	  //  paint.setStyle(Paint.Style.STROKE);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	    
	  }
}
