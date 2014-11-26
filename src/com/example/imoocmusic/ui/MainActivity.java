package com.example.imoocmusic.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.example.imoocmusic.data.Const;
import com.example.imoocmusic.model.IWordButtonClickListener;
import com.example.imoocmusic.model.Song;
import com.example.imoocmusic.model.WorkButton;
import com.example.imoocmusic.myui.MyGridView;
import com.example.imoocmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {

	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;// 线性的动画

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;// 线性的动画

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;// 线性的动画

	private ImageView mViewPan;
	private ImageView mViewPanBar;

	// Play 按键事件
	private ImageButton mBtnPlayStart;
	private boolean isRunning = false;// 播放状态

	// 文字框容器
	private ArrayList<WorkButton> mAllWords;
	private ArrayList<WorkButton> mBtnSelectWords;
	private MyGridView mMyGridView;

	// 已选择文字框的UI容器
	private LinearLayout mViewWordsContainer;

	// 当前的歌曲
	private Song mCurrentSong;

	// 当前关的索引
	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		// 注册监听事件
		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		// 初始化动画

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setInterpolator(mPanLin);
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPan.startAnimation(mPanAnim);
			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setInterpolator(mPanLin);
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				isRunning = false;
				mBtnPlayStart.setVisibility(View.INVISIBLE);
			}
		});

		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPanBar.setAnimation(mBarOutAnim);
			}
		});

		// 初始化控件
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handlePlayButton();
			}
		});

		// 初始化游戏数据
		initCurrentStageData();
	}

	/**
	 * 控制动画
	 */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!isRunning) {
				isRunning = true;
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mViewPan.clearAnimation();
		super.onPause();
	}

	/**
	 * 读取当前关的歌曲的信息
	 * @param stageIndex 
	 * @return
	 */
	private Song loadStageSongInfo(int stageIndex){
		Song song = new Song();
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
	}
	
	/**
	 * 初始化当前数据
	 */
	private void initCurrentStageData() {
		//读取当前歌曲信息
		mCurrentSong=loadStageSongInfo(++mCurrentStageIndex);
		
		// 初始化已选择文字
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(90, 90);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}

		// 获得数据
		mAllWords = initAllWord();
		// 更新数据 - MyGridView
		mMyGridView.updateData(mAllWords);
	}

	/**
	 * 初始化待选文字框
	 * 
	 * @return
	 */
	private ArrayList<WorkButton> initAllWord() {
		ArrayList<WorkButton> data = new ArrayList<WorkButton>();
		// 获得所有待选文字
		// TODO
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WorkButton button = new WorkButton();
			button.mWordString = "好";
			data.add(button);
		}
		return data;
	}

	/**
	 * 初始化已选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WorkButton> initWordSelect() {
		ArrayList<WorkButton> data = new ArrayList<WorkButton>();
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);
			WorkButton holder = new WorkButton();
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);

			data.add(holder);
		}
		return data;

	}

	@Override
	public void onWordButtonClick(WorkButton wordButton) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "哈哈", Toast.LENGTH_LONG).show();
	}

}
