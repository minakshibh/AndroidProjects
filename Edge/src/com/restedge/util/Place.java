package com.restedge.util;

import java.io.Serializable;



public class Place implements Serializable {
	int id,visibility_flag;
	double rating;
	private String  name, address, imageUrl, description,category,subcategory;
	public double getRating()
	{
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	double latitude, longitude;
	byte[] placeImage;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public byte[] getPlaceImage() {
		return placeImage;
	}
	public void setPlaceImage(byte[] placeImage) {
		this.placeImage = placeImage;
	}
	public int getVisibility_flag() {
		return visibility_flag;
	}
	public void setVisibility_flag(int visibility_flag) {
		this.visibility_flag = visibility_flag;
	}
}
