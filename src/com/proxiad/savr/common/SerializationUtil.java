package com.proxiad.savr.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class SerializationUtil {
	public static Object deserialize(Context context, String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = context.openFileInput(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		return ois.readObject();
	}

	public static void serialize(Context context, String fileName, Object object) throws IOException {
		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
	}
	public static void empty(Context context, String fileName) throws IOException {
		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		fos.flush();
		fos.close();
	}
}
