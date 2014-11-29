package com.example.imoocmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
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

	// ��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;// ���ԵĶ���

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;// ���ԵĶ���

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;// ���ԵĶ���

	private ImageView mViewPan;
	private ImageView mViewPanBar;

	// Play �����¼�
	private ImageButton mBtnPlayStart;
	private boolean isRunning = false;// ����״̬

	// ���ֿ�����
	private ArrayList<WorkButton> mAllWords;
	private ArrayList<WorkButton> mBtnSelectWords;
	private MyGridView mMyGridView;

	// ��ѡ�����ֿ��UI����
	private LinearLayout mViewWordsContainer;

	// ��ǰ�ĸ���
	private Song mCurrentSong;

	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		// ע������¼�
		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		// ��ʼ������

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

		// ��ʼ���ؼ�
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handlePlayButton();
			}
		});

		// ��ʼ����Ϸ����
		initCurrentStageData();
	}

	/**
	 * ���ƶ���
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
	 * ��ȡ��ǰ�صĸ�������Ϣ
	 * 
	 * @param stageIndex
	 * @return
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
	}

	/**
	 * ��ʼ����ǰ����
	 */
	private void initCurrentStageData() {
		// ��ȡ��ǰ������Ϣ
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		// ��ʼ����ѡ������
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(90, 90);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}

		// �������
		mAllWords = initAllWord();
		// �������� - MyGridView
		mMyGridView.updateData(mAllWords);
	}

	/**
	 * ��ʼ����ѡ���ֿ�
	 * 
	 * @return
	 */
	private ArrayList<WorkButton> initAllWord() {
		ArrayList<WorkButton> data = new ArrayList<WorkButton>();
		
		// ������д�ѡ����
		String [] words =generateWords();
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WorkButton button = new WorkButton();
			button.mWordString = words[i];
			data.add(button);
		}
		return data;
	}

	/**
	 * ��ʼ����ѡ�����ֿ�
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

	/**
	 * �������еĴ�ѡ����
	 * 
	 * @return
	 */
	private String[] generateWords() {
		String[] words = new String[MyGridView.COUNTS_WORDS];

		// �������
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// ��ȡ������ֲ���������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		return words;
	}

	/**
	 * ��������ַ�
	 * 
	 * @return ������ַ�
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos, lowPos;// ��λ ��λ
		Random random = new Random();
		hightPos = (176 + Math.abs(random.nextInt(39)));
		// �����39�ǿ��Ե��ģ�01 - 94 ���ɣ�������39�Ժ��Ϊ��Ƨ����
		lowPos = (161 + Math.abs(random.nextInt(93)));

		// ����b���byte������
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");

		} catch (UnsupportedEncodingException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return str.charAt(0);
	}

	@Override
	public void onWordButtonClick(WorkButton wordButton) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "����", Toast.LENGTH_LONG).show();
	}

}
