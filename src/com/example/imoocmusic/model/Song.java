package com.example.imoocmusic.model;

public class Song {
	// ��������
	private String mSongName;

	// �������ļ�����
	private String mSongFileName;

	// �������ֵĳ���
	private int mNameLength;
	
	//����������ת��Ϊ�ַ�����
	public char[] getNameCharacters() {
			return mSongName.toCharArray();
	}
	//��ø�����
	public String getSongName() {
		return mSongName;
	}
	
	//���ø������Լ��������Ƴ���
	public void setSongName(String SongName) {
		this.mSongName = SongName;
		this.mNameLength = SongName.length();
	}

	//��ø����ļ�����
	public String getSongFileName() {
		return mSongFileName;
	}

	//���ø����ļ�����
	public void setSongFileName(String SongFileName) {
		this.mSongFileName = SongFileName;
	}

	//��ø������Ƴ���
	public int getNameLength() {
		return mNameLength;
	}

}
