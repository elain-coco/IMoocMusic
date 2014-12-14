package com.example.imoocmusic.ui;

import com.example.imoocmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


/**
 * 通关界面
 * @author noprom
 *
 */
public class AllPassView extends Activity{
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.all_pass_view);
		
		// 隐藏右上角的金币按钮
		FrameLayout view = (FrameLayout)findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
		
	}

}
