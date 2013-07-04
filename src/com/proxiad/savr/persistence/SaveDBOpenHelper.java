package com.proxiad.savr.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SaveDBOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "SaveDatabase.db";
	public static final String DATABASE_TABLE = "Save";
	public static final int DATABASE_VERSION = 12;
	public static final String COLUMN_ID_AUTO = "id";
	public static final String COLUMN_ID_ARTICLE = "id_article";
	public static final String COLUMN_NOM = "nom";
	public static final String COLUMN_ACTEURS = "acteurs";
	public static final String COLUMN_PRIX = "prix";
	public static final String COLUMN_DATE_DEBUT = "date_debut";
	public static final String COLUMN_DATE_FIN = "date_fin";
	public static final String COLUMN_ARTICLE = "article";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_ADRESSE = "adresse";
	public static final String COLUMN_GEOCODE = "geocode";
	public static final String COLUMN_NUMERO = "numero";
	public static final String COLUMN_SIGNATURE = "signature";
	public static final String COLUMN_LIENINTERESSANT0 = "lieninteressant0";
	public static final String COLUMN_LIENINTERESSANT1 = "lieninteressant1";
	public static final String COLUMN_LIENINTERESSANT2 = "lieninteressant2";
	public static final String COLUMN_LIENINTERESSANT3 = "lieninteressant3";
	public static final String COLUMN_LIENINTERESSANT4 = "lieninteressant4";
	public static final String COLUMN_PHOTO = "photo";
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_ID_JOURNALISTE = "id_journaliste";
	public static final String COLUMN_NUMBER_OF_REQUESTS = "nombre_de_requests";
	public static final String COLUMN_DATE_CREATION = "date_creation";
	public static final String COLUMN_CATEGORIE_APPLI = "categorie_appli";
	public static final String COLUMN_LIKES = "likes";
	public static final String COLUMN_CATEGORIE = "categorie";
	public static final String COLUMN_IS_LIKED = "is_liked";

	public static final String COLUMN_REALISATEUR = "realisateur";
	public static final String COLUMN_ARTISTES = "artistes";
	public static final String COLUMN_HORAIRES = "horaires";
	public static final String COLUMN_MARQUE = "marque";
	public static final String COLUMN_LABEL = "label";
	public static final String COLUMN_GROUPE = "groupe";
	public static final String COLUMN_GENRE = "genre";
	public static final String COLUMN_TYPE_DE_CUISINE = "type_de_cuisine";
	public static final String COLUMN_AUTEUR = "auteur";
	public static final String COLUMN_EDITION = "edition";

	public static final String COLUMN_CLES = "cles";

	public static final String[] all_columns = new String[] { SaveDBOpenHelper.COLUMN_ID_ARTICLE, //
			SaveDBOpenHelper.COLUMN_NOM,//
			SaveDBOpenHelper.COLUMN_ACTEURS,//
			SaveDBOpenHelper.COLUMN_PRIX,//
			SaveDBOpenHelper.COLUMN_DATE_DEBUT,//
			SaveDBOpenHelper.COLUMN_DATE_FIN,//
			SaveDBOpenHelper.COLUMN_ARTICLE,//
			SaveDBOpenHelper.COLUMN_NOTE,//
			SaveDBOpenHelper.COLUMN_ADRESSE,//
			SaveDBOpenHelper.COLUMN_GEOCODE,//
			SaveDBOpenHelper.COLUMN_NUMERO,//
			SaveDBOpenHelper.COLUMN_SIGNATURE,//
			SaveDBOpenHelper.COLUMN_LIENINTERESSANT0,//
			SaveDBOpenHelper.COLUMN_LIENINTERESSANT1,//
			SaveDBOpenHelper.COLUMN_LIENINTERESSANT2,//
			SaveDBOpenHelper.COLUMN_LIENINTERESSANT3,//
			SaveDBOpenHelper.COLUMN_LIENINTERESSANT4,//
			SaveDBOpenHelper.COLUMN_PHOTO,//
			SaveDBOpenHelper.COLUMN_CODE,//
			SaveDBOpenHelper.COLUMN_ID_JOURNALISTE,//
			SaveDBOpenHelper.COLUMN_NUMBER_OF_REQUESTS,//
			SaveDBOpenHelper.COLUMN_DATE_CREATION,//
			SaveDBOpenHelper.COLUMN_CATEGORIE_APPLI,//
			SaveDBOpenHelper.COLUMN_LIKES,//
			SaveDBOpenHelper.COLUMN_CATEGORIE,//
			SaveDBOpenHelper.COLUMN_IS_LIKED,//
			SaveDBOpenHelper.COLUMN_REALISATEUR,//
			SaveDBOpenHelper.COLUMN_ARTISTES,//
			SaveDBOpenHelper.COLUMN_HORAIRES,//
			SaveDBOpenHelper.COLUMN_MARQUE,//
			SaveDBOpenHelper.COLUMN_LABEL,//
			SaveDBOpenHelper.COLUMN_GROUPE,//
			SaveDBOpenHelper.COLUMN_GENRE,//
			SaveDBOpenHelper.COLUMN_TYPE_DE_CUISINE,//
			SaveDBOpenHelper.COLUMN_AUTEUR,//
			SaveDBOpenHelper.COLUMN_EDITION, SaveDBOpenHelper.COLUMN_CLES };

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + //
			COLUMN_ID_AUTO + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
			COLUMN_ID_ARTICLE + " integer, " + //
			COLUMN_NOM + " text, " + //
			COLUMN_ACTEURS + " text, " + //
			COLUMN_PRIX + " text, " + //
			COLUMN_DATE_DEBUT + " text, " + //
			COLUMN_DATE_FIN + " text, " + //
			COLUMN_ARTICLE + " text, " + //
			COLUMN_NOTE + " text, " + //
			COLUMN_ADRESSE + " text, " + //
			COLUMN_GEOCODE + " text, " + //
			COLUMN_NUMERO + " text, " + //
			COLUMN_SIGNATURE + " text, " + //
			COLUMN_LIENINTERESSANT0 + " text, " + //
			COLUMN_LIENINTERESSANT1 + " text, " + //
			COLUMN_LIENINTERESSANT2 + " text, " + //
			COLUMN_LIENINTERESSANT3 + " text, " + //
			COLUMN_LIENINTERESSANT4 + " text, " + //
			COLUMN_PHOTO + " text, " + //
			COLUMN_CODE + " text, " + //
			COLUMN_ID_JOURNALISTE + " text, " + //
			COLUMN_NUMBER_OF_REQUESTS + " text, " + //
			COLUMN_DATE_CREATION + " text, " + //
			COLUMN_CATEGORIE_APPLI + " text, " + //
			COLUMN_LIKES + " text, " + //
			COLUMN_CATEGORIE + " text, " + //
			COLUMN_IS_LIKED + " integer, " + //
			// "//next 6 are specially present only for specific category
			COLUMN_ARTISTES + " text, " + //
			COLUMN_REALISATEUR + " text, " + //
			COLUMN_HORAIRES + " text, " + //
			COLUMN_MARQUE + " text, " + //
			COLUMN_LABEL + " text, " + //
			COLUMN_GROUPE + " text, " + //
			COLUMN_GENRE + " text, " + //
			COLUMN_TYPE_DE_CUISINE + " text, " + //
			COLUMN_AUTEUR + " text, " + //
			COLUMN_EDITION + " text," + COLUMN_CLES + " text); ";

	public SaveDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public SaveDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Called when no database exists in disk and the helper class needs
	// to create a new one.
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	// Called when there is a database version mismatch meaning that
	// the version of the database on disk needs to be upgraded to
	// the current version.

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Upgrade the existing database to conform to the new
		// version. Multiple previous versions can be handled by
		// comparing oldVersion and newVersion values.
		// The simplest case is to drop the old table and create a new one.
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		// Create a new one.
		onCreate(db);
	}
}