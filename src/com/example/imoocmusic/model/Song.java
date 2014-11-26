package com.example.imoocmusic.model;

public class Song {
	// 歌曲名称
	private String mSongName;

	// 歌曲的文件名字
	private String mSongFileName;

	// 歌曲名字的长度
	private int mNameLength;
	
	//将歌曲名称转换为字符数组
	public char[] getNameCharacters() {
			return mSongName.toCharArray();
	}
	//获得歌曲名
	public String getSongName() {
		return mSongName;
	}
	
	//设置歌曲名以及歌曲名称长度
	public void setSongName(String SongName) {
		this.mSongName = SongName;
		this.mNameLength = SongName.length();
	}

	//获得歌曲文件名字
	public String getSongFileName() {
		return mSongFileName;
	}

	//设置歌曲文件名字
	public void setSongFileName(String SongFileName) {
		this.mSongFileName = SongFileName;
	}

	//获得歌曲名称长度
	public int getNameLength() {
		return mNameLength;
	}

}
