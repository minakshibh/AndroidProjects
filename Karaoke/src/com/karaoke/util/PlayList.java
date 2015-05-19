package com.karaoke.util;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayList implements Serializable{

	private static final long serialVersionUID = 3L;
	private int id;
	private String playListName;
	private ArrayList<Song> songs;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPlayListName() {
		return playListName;
	}
	
	public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}
	
	public ArrayList<Song> getSongs() {
		return songs;
	}

	public void setSongs(ArrayList<Song> songs) {
		this.songs = songs;
	}
}
