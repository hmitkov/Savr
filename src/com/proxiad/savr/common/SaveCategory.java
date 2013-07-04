package com.proxiad.savr.common;

import android.content.Context;

import com.proxiad.savr.R;

public enum SaveCategory {
	recent(), restaurant(), cinema(), livre(), exposition(), bar(), shopping(), theatre(), musique(), hotel(), autre(), concert(), evenement(), diver(), tous();

	public static SaveCategory getByText(Context context, String text) {
		SaveCategory result;
		if (text.equals(context.getString(SaveCategory.restaurant.getStringResource()))) {
			result = SaveCategory.restaurant;
		} else if (text.equals(context.getString(SaveCategory.cinema.getStringResource()))) {
			result = SaveCategory.cinema;
		} else if (text.equals(context.getString(SaveCategory.livre.getStringResource()))) {
			result = SaveCategory.livre;
		} else if (text.equals(context.getString(SaveCategory.exposition.getStringResource()))) {
			result = SaveCategory.exposition;
		} else if (text.equals(context.getString(SaveCategory.bar.getStringResource()))) {
			result = SaveCategory.bar;
		} else if (text.equals(context.getString(SaveCategory.shopping.getStringResource()))) {
			result = SaveCategory.shopping;
		} else if (text.equals(context.getString(SaveCategory.theatre.getStringResource()))) {
			result = SaveCategory.theatre;
		} else if (text.equals(context.getString(SaveCategory.musique.getStringResource()))) {
			result = SaveCategory.musique;
		} else if (text.equals(context.getString(SaveCategory.hotel.getStringResource()))) {
			result = SaveCategory.hotel;
		} else if (text.equals(context.getString(SaveCategory.autre.getStringResource()))) {
			result = SaveCategory.autre;
		} else if (text.equals(context.getString(SaveCategory.concert.getStringResource()))) {
			result = SaveCategory.concert;
		} else if (text.equals(context.getString(SaveCategory.evenement.getStringResource()))) {
			result = SaveCategory.evenement;
		} else if (text.equals(context.getString(SaveCategory.diver.getStringResource()))) {
			result = SaveCategory.diver;
		} else if (text.equals(context.getString(SaveCategory.tous.getStringResource()))) {
			result = SaveCategory.tous;
		} else if (text.equals(context.getString(SaveCategory.recent.getStringResource()))) {
			result = SaveCategory.recent;
		} else {
			throw new RuntimeException("Invalid category " + text);
		}
		return result;
	}

	public int getStringResource() {
		int result;
		switch (this) {
		case recent:
			result = R.string.category_recent;
			break;
		case restaurant:
			result = R.string.category_restaurant;
			break;
		case cinema:
			result = R.string.category_cinema;
			break;
		case livre:
			result = R.string.category_livre;
			break;
		case exposition:
			result = R.string.category_exposition;
			break;
		case bar:
			result = R.string.category_bar;
			break;
		case hotel:
			result = R.string.category_hotel;
			break;
		case shopping:
			result = R.string.category_shopping;
			break;
		case theatre:
			result = R.string.category_theatre;
			break;
		case musique:
			result = R.string.category_musique;
			break;
		case evenement:
			result = R.string.category_evenement;
			break;
		case diver:
			result = R.string.category_divers;
			break;
		case tous:
			result = R.string.category_tous;
			break;
		case concert:
			result = R.string.category_concert;
			break;
		case autre:
			result = R.string.category_autre;
			break;
		default:
			// don't return 0, or there will be an exception
			result = R.string.category_restaurant;
			break;
		}
		return result;
	}
}
