package com.proxiad.savr.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.proxiad.savr.common.SaveCategory;
import com.proxiad.savr.model.Save;

public class PersistenceManager {
	public static void insertSave(Context context, Save save) {
		// Create a new row of values to insert.
		ContentValues newRow = new ContentValues();

		// Assign values for each row.
		newRow.put(SaveDBOpenHelper.COLUMN_ID_ARTICLE, save.getId_article());
		newRow.put(SaveDBOpenHelper.COLUMN_NOM, save.getNom());

		newRow.put(SaveDBOpenHelper.COLUMN_ACTEURS, save.getActeurs());
		newRow.put(SaveDBOpenHelper.COLUMN_PRIX, save.getPrix());
		System.err.println("INSERT COLUMN_PRIX-->" + save.getPrix());
		newRow.put(SaveDBOpenHelper.COLUMN_DATE_DEBUT, save.getDate_debut());
		newRow.put(SaveDBOpenHelper.COLUMN_DATE_FIN, save.getDate_fin());
		newRow.put(SaveDBOpenHelper.COLUMN_ARTICLE, save.getArticle());
		newRow.put(SaveDBOpenHelper.COLUMN_NOTE, save.getNote());
		newRow.put(SaveDBOpenHelper.COLUMN_ADRESSE, save.getAdresse());
		newRow.put(SaveDBOpenHelper.COLUMN_GEOCODE, save.getGeocode());
		System.err.println("INSERT GEOCODE-->" + save.getGeocode());
		newRow.put(SaveDBOpenHelper.COLUMN_NUMERO, save.getNumero());
		newRow.put(SaveDBOpenHelper.COLUMN_SIGNATURE, save.getSignature());
		newRow.put(SaveDBOpenHelper.COLUMN_LIENINTERESSANT0, save.getLieninteressant0());
		newRow.put(SaveDBOpenHelper.COLUMN_LIENINTERESSANT1, save.getLieninteressant1());
		newRow.put(SaveDBOpenHelper.COLUMN_LIENINTERESSANT2, save.getLieninteressant2());
		newRow.put(SaveDBOpenHelper.COLUMN_LIENINTERESSANT3, save.getLieninteressant3());
		newRow.put(SaveDBOpenHelper.COLUMN_LIENINTERESSANT4, save.getLieninteressant4());
		newRow.put(SaveDBOpenHelper.COLUMN_PHOTO, save.getPhoto());
		newRow.put(SaveDBOpenHelper.COLUMN_CODE, save.getCode());
		newRow.put(SaveDBOpenHelper.COLUMN_ID_JOURNALISTE, save.getId_journaliste());
		newRow.put(SaveDBOpenHelper.COLUMN_NUMBER_OF_REQUESTS, save.getNombre_de_requests());
		newRow.put(SaveDBOpenHelper.COLUMN_DATE_CREATION, save.getDate_creation());
		newRow.put(SaveDBOpenHelper.COLUMN_CATEGORIE_APPLI, save.getCategorie_appli());
		newRow.put(SaveDBOpenHelper.COLUMN_LIKES, save.getLikes());
		newRow.put(SaveDBOpenHelper.COLUMN_CATEGORIE, save.getCategorie().toString());
		newRow.put(SaveDBOpenHelper.COLUMN_IS_LIKED, save.getIsLiked() ? 1 : 0);

		// next 6 are specially for particular category
		newRow.put(SaveDBOpenHelper.COLUMN_REALISATEUR, save.getRealisateur());
		newRow.put(SaveDBOpenHelper.COLUMN_ARTISTES, save.getArtistes());

		newRow.put(SaveDBOpenHelper.COLUMN_HORAIRES, save.getHoraires());
		newRow.put(SaveDBOpenHelper.COLUMN_MARQUE, save.getMarque());
		newRow.put(SaveDBOpenHelper.COLUMN_LABEL, save.getLabel());
		newRow.put(SaveDBOpenHelper.COLUMN_GROUPE, save.getGroupe());
		newRow.put(SaveDBOpenHelper.COLUMN_GENRE, save.getGenre());
		newRow.put(SaveDBOpenHelper.COLUMN_TYPE_DE_CUISINE, save.getType_de_cuisine());
		newRow.put(SaveDBOpenHelper.COLUMN_AUTEUR, save.getAuteur());
		newRow.put(SaveDBOpenHelper.COLUMN_EDITION, save.getEdition());
		newRow.put(SaveDBOpenHelper.COLUMN_CLES, save.getClesToStr());
		// Insert the row into your table
		SaveDBOpenHelper openHelper = new SaveDBOpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.insert(SaveDBOpenHelper.DATABASE_TABLE, null, newRow);
		db.close();
	}

	public static List<Save> getAllSave(Context context) {
		String where = "";
		String whereArgs[] = null;
		return getSaveListByCriteria(context, where, whereArgs);
	}

	public static List<Save> getSaveByCode(Context context, String code) {
		String where = SaveDBOpenHelper.COLUMN_CODE + "='" + code.replace(" ", "").trim() + "'";
		String whereArgs[] = null;
		return getSaveListByCriteria(context, where, whereArgs);
	}

	public static List<Save> getSaveListByCategory(Context context, SaveCategory category) {
		String where = null;
		String whereArgs[] = null;
		System.err.println("getSaveListByCategory category-->" + category);
		if (category != null && category.equals(SaveCategory.recent)) {
			String order = SaveDBOpenHelper.COLUMN_ID_AUTO + " DESC LIMIT 10";

			return getSaveListByCriteriaOrder(context, order);
		} else {
			if (category != null) {
				where = SaveDBOpenHelper.COLUMN_CATEGORIE + "='" + category + "'";
			}
			return getSaveListByCriteria(context, where, whereArgs);
		}

	}

	public static List<Save> getSaveListByCategory(Context context, String searchWord) {
		String where = null;
		String whereArgs[] = null;
		if (searchWord != null) {
			where = SaveDBOpenHelper.COLUMN_NOM + " LIKE '%" + searchWord + "%'";

		}
		return getSaveListByCriteria(context, where, whereArgs);

	}

	public static List<Save> getSaveListByCriteria(Context context, String where, String[] whereArgs) {
		List<Save> allSaves = new ArrayList<Save>();
		String groupBy = null;
		String having = null;
		String order = null;
		SaveDBOpenHelper openHelper = new SaveDBOpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query(SaveDBOpenHelper.DATABASE_TABLE, SaveDBOpenHelper.all_columns, where, whereArgs, groupBy, having, order);
		while (cursor.moveToNext()) {
			Save save = saveFromCursor(cursor);
			allSaves.add(save);
		}
		db.close();
		return allSaves;
	}

	public static List<Save> getSaveListByCriteriaOrder(Context context, String order) {
		List<Save> allSaves = new ArrayList<Save>();
		SaveDBOpenHelper openHelper = new SaveDBOpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query(SaveDBOpenHelper.DATABASE_TABLE, SaveDBOpenHelper.all_columns, null, null, null, null, order);
		while (cursor.moveToNext()) {
			Save save = saveFromCursor(cursor);
			allSaves.add(save);
		}
		db.close();
		return allSaves;
	}

	private static Save saveFromCursor(Cursor cursor) {
		int column_index_id_article = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ID_ARTICLE);
		int column_index_nom = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_NOM);
		int column_index_acteurs = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ACTEURS);
		int column_index_prix = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_PRIX);
		int column_index_date_debut = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_DATE_DEBUT);
		int column_index_date_fin = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_DATE_FIN);
		int column_index_article = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ARTICLE);
		int column_index_note = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_NOTE);
		int column_index_addresse = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ADRESSE);
		int column_index_geocode = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_GEOCODE);
		int column_index_numero = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_NUMERO);
		int column_index_signature = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_SIGNATURE);
		int column_index_lieninteressant0 = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIENINTERESSANT0);
		int column_index_lieninteressant1 = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIENINTERESSANT1);
		int column_index_lieninteressant2 = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIENINTERESSANT2);
		int column_index_lieninteressant3 = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIENINTERESSANT3);
		int column_index_lieninteressant4 = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIENINTERESSANT4);
		int column_index_photo = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_PHOTO);
		int column_index_code = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_CODE);
		int column_index_id_journaliste = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ID_JOURNALISTE);
		int column_index_number_of_request = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_NUMBER_OF_REQUESTS);
		int column_index_date_creation = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_CODE);
		int column_index_categorie_appli = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_CATEGORIE_APPLI);
		int column_index_likes = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LIKES);
		int column_index_categorie = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_CATEGORIE);
		int column_index_is_liked = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_IS_LIKED);

		int column_index_artiste = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_ARTISTES);
		int column_index_realisateur = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_REALISATEUR);
		int column_index_horaires = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_HORAIRES);
		int column_index_is_marque = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_MARQUE);
		int column_index_label = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_LABEL);
		int column_index_groupe = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_GROUPE);
		int column_index_genre = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_GENRE);
		int column_index_cuisine = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_TYPE_DE_CUISINE);
		int column_index_auteur = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_AUTEUR);
		int column_index_edition = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_EDITION);
		int column_index_cles = cursor.getColumnIndexOrThrow(SaveDBOpenHelper.COLUMN_CLES);

		int id_article = cursor.getInt(column_index_id_article);
		String nom = cursor.getString(column_index_nom);
		String acteurs = cursor.getString(column_index_acteurs);
		String prix = cursor.getString(column_index_prix);
		String dateDebut = cursor.getString(column_index_date_debut);
		String dateFin = cursor.getString(column_index_date_fin);
		String article = cursor.getString(column_index_article);
		String note = cursor.getString(column_index_note);
		String addresse = cursor.getString(column_index_addresse);
		String numero = cursor.getString(column_index_numero);
		String geocode = cursor.getString(column_index_geocode);
		String signature = cursor.getString(column_index_signature);
		String lieninteressant0 = cursor.getString(column_index_lieninteressant0);
		String lieninteressant1 = cursor.getString(column_index_lieninteressant1);
		String lieninteressant2 = cursor.getString(column_index_lieninteressant2);
		String lieninteressant3 = cursor.getString(column_index_lieninteressant3);
		String lieninteressant4 = cursor.getString(column_index_lieninteressant4);
		String photo = cursor.getString(column_index_photo);
		String code = cursor.getString(column_index_code);
		String idJournaliste = cursor.getString(column_index_id_journaliste);
		String numberOfRequest = cursor.getString(column_index_number_of_request);
		String dateCreation = cursor.getString(column_index_date_creation);
		String categorie_appli = cursor.getString(column_index_categorie_appli);
		String likes = cursor.getString(column_index_likes);
		String categorie = cursor.getString(column_index_categorie);
		int isLiked = cursor.getInt(column_index_is_liked);

		String artiste = cursor.getString(column_index_artiste);
		String realisateur = cursor.getString(column_index_realisateur);
		String horaires = cursor.getString(column_index_horaires);
		String isMarque = cursor.getString(column_index_is_marque);
		String label = cursor.getString(column_index_label);
		String groupe = cursor.getString(column_index_groupe);
		String genre = cursor.getString(column_index_genre);
		String cuisine = cursor.getString(column_index_cuisine);
		String auteur = cursor.getString(column_index_auteur);
		String edition = cursor.getString(column_index_edition);
		String clesStr = cursor.getString(column_index_cles);

		Save save = new Save();
		save.setId_article(id_article);
		save.setNom(nom);
		save.setActeurs(acteurs);
		save.setPrix(prix);
		save.setDate_debut(dateDebut);
		save.setDate_fin(dateFin);
		save.setArticle(article);
		save.setNote(note);
		save.setAdresse(addresse);
		save.setNumero(numero);
		save.setGeocode(geocode);
		save.setSignature(signature);
		save.setLieninteressant0(lieninteressant0);
		save.setLieninteressant1(lieninteressant1);
		save.setLieninteressant2(lieninteressant2);
		save.setLieninteressant3(lieninteressant3);
		save.setLieninteressant4(lieninteressant4);
		save.setPhoto(photo);
		save.setCode(code);
		save.setId_journaliste(idJournaliste);
		save.setNombre_de_requests(numberOfRequest);
		save.setDate_creation(dateCreation);
		save.setCategorie_appli(categorie_appli);
		save.setLikes(Integer.valueOf(likes));
		save.setCategorie(SaveCategory.valueOf(categorie));
		save.setIsLiked(isLiked > 0 ? true : false);
		save.setArtistes(artiste);
		save.setRealisateur(realisateur);
		save.setHoraires(horaires);
		save.setMarque(isMarque);
		save.setLabel(label);
		save.setGroupe(groupe);
		save.setGenre(genre);
		save.setType_de_cuisine(cuisine);
		save.setAuteur(auteur);
		save.setEdition(edition);
		save.setClesFromStr(clesStr);

		return save;
	}

	public static Save updateLikes(Context context, Save save) {
		save.setIsLiked(true);
		int likes = save.getLikes();
		likes = likes + 1;
		save.setLikes(likes);

		ContentValues updates = new ContentValues();
		updates.put(SaveDBOpenHelper.COLUMN_LIKES, save.getLikes());
		updates.put(SaveDBOpenHelper.COLUMN_IS_LIKED, save.getIsLiked());

		SaveDBOpenHelper openHelper = new SaveDBOpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int rowsUpdated = db.update(SaveDBOpenHelper.DATABASE_TABLE, updates, SaveDBOpenHelper.COLUMN_CODE + "=" + save.getCode().trim(), null);
		db.close();

		return save;
	}

	public static int deleteSave(Context context, Save save) {
		SaveDBOpenHelper openHelper = new SaveDBOpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int rowsDeleted = db.delete(SaveDBOpenHelper.DATABASE_TABLE, SaveDBOpenHelper.COLUMN_CODE + "=" + save.getCode().trim(), null);
		return rowsDeleted;
	}
}
