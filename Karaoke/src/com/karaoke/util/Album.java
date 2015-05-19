package com.karaoke.util;

import java.io.Serializable;

public class Album implements Serializable{

	String AlbumUrl,ThumbnailUrl,ArtistName,AlbumName,AlbumsdPath;
	byte[] albumImage;
	boolean isPurchased;
	
	
	
	public boolean isPurchased() {
		return isPurchased;
	}
	public void setPurchased(boolean isPurchased) {
		this.isPurchased = isPurchased;
	}
	public byte[] getAlbumImage() {
		return albumImage;
	}
	public void setAlbumImage(byte[] albumImage) {
		this.albumImage = albumImage;
	}
	private static final long serialVersionUID = 1L;
	int albumId,Songs,SongNo;
	
	public int getSongNo() {
		return SongNo;
	}
	public void setSongNo(int songNo) {
		SongNo = songNo;
	}
	
	
	public int getAlbumId() {
		return albumId;
	}
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	public int getSongs() {
		return Songs;
	}
	public void setSongs(int songs) {
		Songs = songs;
	}
	public String getAlbumUrl() {
		return AlbumUrl;
	}
	public void setAlbumUrl(String albumUrl) {
		AlbumUrl = albumUrl;
	}
	public String getThumbnailUrl() {
		return ThumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		ThumbnailUrl = thumbnailUrl;
	}
	public String getArtistName() {
		return ArtistName;
	}
	public void setArtistName(String artistName) {
		ArtistName = artistName;
	}
	public String getAlbumName() {
		return AlbumName;
	}
	public void setAlbumName(String albumName) {
		AlbumName = albumName;
	}
	public float getAlbumPrice() {
		return AlbumPrice;
	}
	public void setAlbumPrice(float albumPrice) {
		AlbumPrice = albumPrice;
	}
	
	public String getAlbumsdPath() {
		return AlbumsdPath;
	}
	public void setAlbumsdPath(String albumsdPath) {
		AlbumsdPath = albumsdPath;
	}
	float AlbumPrice;
	
}
