package com.example.imoocmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 * 
 * @author noprom
 *
 */
public class WorkButton {
	// 定义4个属性
	public int mIndex;
	public boolean mIsVisable;
	public String mWordString;
	public Button mViewButton;

	// 定义构造方法
	public WorkButton() {
		mIsVisable = false;
		mWordString = "";
	}
}
