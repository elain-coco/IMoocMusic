package com.example.imoocmusic.model;

import android.widget.Button;

/**
 * ���ְ�ť
 * 
 * @author noprom
 *
 */
public class WorkButton {
	// ����4������
	public int mIndex;
	public boolean mIsVisable;
	public String mWordString;
	public Button mViewButton;

	// ���幹�췽��
	public WorkButton() {
		mIsVisable = false;
		mWordString = "";
	}
}
