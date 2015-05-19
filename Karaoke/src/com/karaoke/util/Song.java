package com.karaoke.util;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable{

	
	private static final long serialVersionUID = 1L;
	int songId=-1,albumId=-1,songs;
	float songPrice;
	byte[] songImage;
	String songName,songUrl,songTumbnailUrl,artistName,albumName,SDcardPath,SampleVideoUrl,albumcode,duration,uploadDate;
	boolean isPurchased;
	private String itemStatus;
	
	
	public boolean isPurchased() {
		return isPurchased;
	}
	public void setPurchased(boolean isPurchased) {
		this.isPurchased = isPurchased;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getAlbumcode() {
		return albumcode;
	}
	public void setAlbumcode(String albumcode) {
		this.albumcode = albumcode;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	ArrayList<VideosInAlbum> subCategories = new ArrayList<Song.VideosInAlbum>();
	
	 public ArrayList<VideosInAlbum> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(ArrayList<VideosInAlbum> subCategories) {
		this.subCategories = subCategories;
	}

	boolean selected = false;
	 
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getSampleVideoUrl() {
		return SampleVideoUrl;
	}
	public void setSampleVideoUrl(String sampleVideoUrl) {
		SampleVideoUrl = sampleVideoUrl;
	}
	
	public int getSongs() {
		return songs;
	}
	public void setSongs(int songs) {
		this.songs = songs;
	}
	
	public byte[] getSongImage() {
		return songImage;
	}
	public void setSongImage(byte[] songImage) {
		this.songImage = songImage;
	}
	
	public String getSDcardPath() {
		return SDcardPath;
	}
	public void setSDcardPath(String sDcardPath) {
		SDcardPath = sDcardPath;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName)
	{
		this.artistName = artistName;
	}
	public String getAlbumName() 
	{
		return albumName;
	}
	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}
	public int getAlbumId() 
	{
		return albumId;
	}
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	public int getSongId()
	{
		return songId;
	}
	public void setSongId(int songId) 
	{
		this.songId = songId;
	}
	public float getSongPrice() 
	{
		return songPrice;
	}
	public void setSongPrice(float songPrice)
	{
		this.songPrice = songPrice;
	}
	public String getSongName() 
	{
		return songName;
	}
	public void setSongName(String songName)
	{
		this.songName = songName;
	}
	public String getSongUrl()
	{
		return songUrl;
	}
	public void setSongUrl(String songUrl)
	{
		this.songUrl = songUrl;
	}
	public String getSongTumbnailUrl()
	{
		return songTumbnailUrl;
	}
	public void setSongTumbnailUrl(String songTumbnailUrl)
	{
		this.songTumbnailUrl = songTumbnailUrl;
	}
	
	public String getItemStatus() {
		return itemStatus;
	}
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}

	public class VideosInAlbum implements Serializable
	{
		int videoId,albumId;
		boolean isVideoPurchase;
		boolean isPurchased;
		String VideoTrackCode,VideoUrl,SampleVideoUrl,ThumbnailUrl,VideoName,ArtistName,duration;
		
	public boolean isVideoPurchase() {
			return isVideoPurchase;
		}
		public void setVideoPurchase(boolean isVideoPurchase) {
			this.isVideoPurchase = isVideoPurchase;
		}
			public boolean isPurchased() {
			return isPurchased;
		}
		public void setPurchased(boolean isPurchased) {
			this.isPurchased = isPurchased;
		}
		public int getVideoId() {
			return videoId;
		}
		public void setVideoId(int videoId) {
			this.videoId = videoId;
		}
		public int getAlbumId() {
			return albumId;
		}
		public void setAlbumId(int albumId) {
			this.albumId = albumId;
		}
		public String getVideoTrackCode() {
			return VideoTrackCode;
		}
		public void setVideoTrackCode(String videoTrackCode) {
			VideoTrackCode = videoTrackCode;
		}
		public String getVideoUrl() {
			return VideoUrl;
		}
		public void setVideoUrl(String videoUrl) {
			VideoUrl = videoUrl;
		}
		public String getSampleVideoUrl() {
			return SampleVideoUrl;
		}
		public void setSampleVideoUrl(String sampleVideoUrl) {
			SampleVideoUrl = sampleVideoUrl;
		}
		public String getThumbnailUrl() {
			return ThumbnailUrl;
		}
		public void setThumbnailUrl(String thumbnailUrl) {
			ThumbnailUrl = thumbnailUrl;
		}
		public String getVideoName() {
			return VideoName;
		}
		public void setVideoName(String videoName) {
			VideoName = videoName;
		}
		public String getArtistName() {
			return ArtistName;
		}
		public void setArtistName(String artistName) {
			ArtistName = artistName;
		}
		public String getDuration() {
			return duration;
		}
		public void setDuration(String duration) {
			this.duration = duration;
		}
	
		
		
		
	}
}
