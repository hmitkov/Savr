package com.proxiad.savr.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.proxiad.savr.common.SaveCategory;


public class Save {
	private int id_article;
	private String nom;
	private String acteurs;
	private String realisateur;
	private String prix;
	private String date_debut;
	private String date_fin;
	private String article;
	private String note;
	private String adresse;
	private String geocode;
	private String numero;
	private String signature;
	private String lieninteressant0;
	private String lieninteressant1;
	private String lieninteressant2;
	private String lieninteressant3;
	private String lieninteressant4;
	private String photo;
	private String code;
	private String id_journaliste;
	private String nombre_de_requests;
	private String date_creation;
	private String categorie_appli;
	private int likes;
	private SaveCategory categorie;
	private boolean isLiked = false;
	String[] cles;

	private String artistes;
	private String horaires;
	private String marque;
	private String label;
	private String groupe;
	private String genre;

	// new
	private String type_de_cuisine;
	private String auteur;
	private String edition;

	public int getId_article() {
		return id_article;
	}

	public void setId_article(int id_article) {
		this.id_article = id_article;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getActeurs() {
		return acteurs;
	}

	public void setActeurs(String acteurs) {
		this.acteurs = acteurs;
	}

	public String getPrix() {
		return prix;
	}

	public void setPrix(String prix) {
		this.prix = prix;
	}

	public String getDate_debut() {
		return date_debut;
	}

	public void setDate_debut(String date_debut) {
		this.date_debut = date_debut;
	}

	public String getDate_fin() {
		return date_fin;
	}

	public void setDate_fin(String date_fin) {
		this.date_fin = date_fin;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getGeocode() {
		return geocode;
	}

	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getLieninteressant0() {
		return lieninteressant0;
	}

	public void setLieninteressant0(String lieninteressant0) {
		this.lieninteressant0 = lieninteressant0;
	}

	public String getLieninteressant1() {
		return lieninteressant1;
	}

	public void setLieninteressant1(String lieninteressant1) {
		this.lieninteressant1 = lieninteressant1;
	}

	public String getLieninteressant2() {
		return lieninteressant2;
	}

	public void setLieninteressant2(String lieninteressant2) {
		this.lieninteressant2 = lieninteressant2;
	}

	public String getLieninteressant3() {
		return lieninteressant3;
	}

	public void setLieninteressant3(String lieninteressant3) {
		this.lieninteressant3 = lieninteressant3;
	}

	public String getLieninteressant4() {
		return lieninteressant4;
	}

	public void setLieninteressant4(String lieninteressant4) {
		this.lieninteressant4 = lieninteressant4;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId_journaliste() {
		return id_journaliste;
	}

	public void setId_journaliste(String id_journaliste) {
		this.id_journaliste = id_journaliste;
	}

	public String getNombre_de_requests() {
		return nombre_de_requests;
	}

	public void setNombre_de_requests(String nombre_de_requests) {
		this.nombre_de_requests = nombre_de_requests;
	}

	public String getDate_creation() {
		return date_creation;
	}

	public void setDate_creation(String date_creation) {
		this.date_creation = date_creation;
	}

	public String getCategorie_appli() {
		return categorie_appli;
	}

	public void setCategorie_appli(String categorie_appli) {
		this.categorie_appli = categorie_appli;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public SaveCategory getCategorie() {
		return categorie;
	}

	public void setCategorie(SaveCategory categorie) {
		this.categorie = categorie;
	}

	public boolean getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}

	public String getArtistes() {
		return artistes;
	}

	public void setArtistes(String artistes) {
		this.artistes = artistes;
	}

	public String getHoraires() {
		return horaires;
	}

	public void setHoraires(String horaires) {
		this.horaires = horaires;
	}

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getGroupe() {
		return groupe;
	}

	public void setGroupe(String groupe) {
		this.groupe = groupe;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getRealisateur() {
		return realisateur;
	}

	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}

	public String getType_de_cuisine() {
		return type_de_cuisine;
	}

	public void setType_de_cuisine(String type_de_cuisine) {
		this.type_de_cuisine = type_de_cuisine;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String[] getCles() {
		return cles;
	}

	public void setCles(String[] cles) {
		this.cles = cles;
	}

	public void setLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}

	public String getClesToStr() {
		String result = "";
		for (String c : cles) {
			result += c + ",";
		}
		if (result.length() > 1) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public void setClesFromStr(String clesStr) {
		List<String> clesStrList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(clesStr, ",");
		while (st.hasMoreTokens()) {
			clesStrList.add(st.nextToken());
		}
		cles = new String[clesStrList.size()];
		for (int i = 0; i < clesStrList.size(); i++) {
			cles[i] = clesStrList.get(i);
		}
	}

	public String[] getClesz() {
		String[] cles = new String[] {};
		if (categorie == null) {
			cles = new String[] { "prix", "adresse", "type_de_cuisine", "numero" };
		} else if (categorie.equals("restaurant")) {
			cles = new String[] { "prix", "adresse", "type_de_cuisine", "numero" };
		} else if (categorie.equals("cinema")) {
			cles = new String[] { "acteurs", "realisateur", "genre", "date_debut" };
		} else if (categorie.equals("livre")) {
			cles = new String[] { "prix", "auteur", "genre", "edition", "date_debut" };
		} else if (categorie.equals("exposition")) {
			cles = new String[] { "prix", "adresse", "artistes", "numero", "date_debut", "date_fin" };
		} else if (categorie.equals("bar")) {
			cles = new String[] { "prix", "adresse", "horaires", "numero" };
		} else if (categorie.equals("shopping")) {
			cles = new String[] { "prix", "adresse", "marque", "genre" };
		} else if (categorie.equals("theatre")) {
			cles = new String[] { "prix", "adresse", "acteurs", "numero", "date_debut", "date_fin" };
		} else if (categorie.equals("musique")) {
			cles = new String[] { "artistes", "genre", "label", "date_debut" };
		} else if (categorie.equals("hotel")) {
			cles = new String[] { "prix", "adresse", "groupe", "numero" };
		} else if (categorie.equals("autre")) {
			cles = new String[] { "prix", "adresse", "numero", "date_debut", "date_fin" };
		} else if (categorie.equals("concert")) {
			cles = new String[] { "prix", "adresse", "artistes", "numero", "date_debut", "date_fin" };
		} else if (categorie.equals("evenement")) {
			cles = new String[] { "prix", "adresse", "numero", "date_debut", "date_fin" };
		}
		return cles;
	}

	@Override
	public String toString() {
		return "Save [id=" + id_article + ", nom=" + nom + ", code=" + code + ", categorie=" + categorie + "]";
	}

}
