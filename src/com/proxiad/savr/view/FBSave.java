package com.proxiad.savr.view;

import java.util.Date;

import android.graphics.Bitmap;

import com.proxiad.savr.model.Save;

public class FBSave implements Comparable<FBSave> {
	private Save save;
	private String socialNetworkId;
	// image of the save
	private Bitmap bmp;
	private Date date;
	// social network image
	private Bitmap socialNetworkProfileImage;
	private String publisher;

	public FBSave(Save save, String socialNetworkId, String publisher, Bitmap bmp, Date date, Bitmap socialNetworkProfileImage) {
		super();
		this.save = save;
		this.socialNetworkId = socialNetworkId;
		this.bmp = bmp;
		this.date = date;
		this.socialNetworkProfileImage = socialNetworkProfileImage;
		this.publisher = publisher;
	}

	public Save getSave() {
		return save;
	}

	public void setSave(Save save) {
		this.save = save;
	}

	public String getSocialNetworkId() {
		return socialNetworkId;
	}

	public void setSocialNetworkId(String socialNetworkId) {
		this.socialNetworkId = socialNetworkId;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Bitmap getSocialNetworkProfileImage() {
		return socialNetworkProfileImage;
	}

	public void setSocialNetworkProfileImage(Bitmap socialNetworkProfileImage) {
		this.socialNetworkProfileImage = socialNetworkProfileImage;
	}

	@Override
	public String toString() {
		return "FBSave [save=" + save + "]";
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public int compareTo(FBSave o) {
		if ((this == o))
			return 0;
		if ((o == null))
			return -1;
		if (this.getDate() == o.getDate()) {
			return 0;
		}
		if (this.getDate() == null) {
			return -1;
		}
		if (o.getDate() == null) {
			return 1;
		}
		int result = this.getDate().before(o.getDate()) ? 1 : -1;
		return result;
	}
	

}
