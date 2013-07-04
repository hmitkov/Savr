package com.proxiad.savr.common;

import java.io.File;

import com.proxiad.savr.model.Save;

import android.content.Context;

public class FileUtil {
	public static File getAppDir(Context context) {
		File appRootDir = context.getExternalFilesDir(null);
		if (appRootDir!=null && !appRootDir.exists()) {
			appRootDir.mkdirs();
		}
		return appRootDir;
	}

	public static File getSaveImageFile(Context context, Save save) {
		return new File(getAppDir(context), getSaveImageFileName(save));
	}

	public static String getSaveImageFileName(Save save) {
		return save.getCode() + ".jpg";
	}
}
