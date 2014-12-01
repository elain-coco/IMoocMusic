package com.example.imoocmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.util.Log;
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
import com.example.imoocmusic.util.MyLog;
import com.example.imoocmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {

	public final static String TAG = "MainActivity"; // Tag

	/** �𰸵�״̬---��ȷ */
	public final static int STATUS_ANSWER_RIGHT = 1;

	/** �𰸵�״̬---���� */
	public final static int STATUS_ANSWER_WRONG = 2;

	/** �𰸵�״̬---������ */
	public final static int STATUS_ANSWER_LACK = 3;

	// ��˸�Ĵ���
	public final static int SPASH_TIMES = 6;
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

	// ���ؽ���
	private View passView;
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
		String[] words = generateWords();
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
			final WorkButton holder = new WorkButton();
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			// ������ѡ���ĵ���¼�
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					clearTheAnswer(holder);
				}
			});
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
		Random random = new Random();
		String[] words = new String[MyGridView.COUNTS_WORDS];

		// �������
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// ��ȡ������ֲ���������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// ��������˳�����ȴ�����Ԫ�������ѡȡһ��Ԫ�����һ��Ԫ�ؽ�����Ȼ�����ڵڶ���֮��
		// ���ѡ��һ��Ԫ����ڶ���Ԫ�ؽ�����ֱ�����һ��Ԫ�أ�����ÿ��Ԫ�ر������ĸ��ʶ�Ϊ1/n

		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
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

	/**
	 * �����ѡ���Ĵ�
	 * 
	 * @param button
	 */
	private void clearTheAnswer(WorkButton button) {
		button.mViewButton.setText("");
		button.mWordString = "";
		button.mIsVisable = false;

		// ���ô�ѡ��Ŀɼ���
		setButtonVisiable(mAllWords.get(button.mIndex), View.VISIBLE);
	}

	/**
	 * ���ô𰸵������Լ��ɼ���
	 * 
	 * @param workButton
	 */
	private void setSelectWord(WorkButton workButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {// ���α�����ѡ������
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ô����ֿ�������Լ��ɼ���
				mBtnSelectWords.get(i).mViewButton
						.setText(workButton.mWordString);
				mBtnSelectWords.get(i).mIsVisable = true;
				mBtnSelectWords.get(i).mWordString = workButton.mWordString;
				// ��¼����
				mBtnSelectWords.get(i).mIndex = workButton.mIndex;
				// Log
				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");
				// ���ô�ѡ��Ŀɼ���
				setButtonVisiable(workButton, View.INVISIBLE);
				break;// ��һ��ǳ���Ҫ����ȻΪ�յĴ�ѡ�򶼻ᱻѡ��
			}
		}
	}

	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WorkButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisable = (visibility == View.VISIBLE) ? true : false;
		// Log ��ӡ��ť�Ŀɼ���
		MyLog.d(TAG, button.mIsVisable + "");
	}

	@Override
	public void onWordButtonClick(WorkButton wordButton) {
		// TODO Auto-generated method stub
		setSelectWord(wordButton);
		// ��ô𰸵�״̬
		int checkResult = checkTheAnswer();
		// ����
		switch (checkResult) {
		case STATUS_ANSWER_RIGHT:
			// ����ȷ����ý������ҹ���
//			Toast.makeText(MainActivity.this, "����ȷ�˰�������ô��������",
//					Toast.LENGTH_LONG).show();
			handlePassEvent();
			break;
		case STATUS_ANSWER_WRONG:
			// �𰸴�����˸���ֲ�����ʾ����
			sparkTheWords();
			break;
		default:
			// ��ȱʧ,������ɫ����Ϊ��ɫ
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			break;
		}
	}

	/**
	 * �������й��ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		passView = (LinearLayout) this.findViewById(R.id.pass_view);// ��ʼ��
		passView.setVisibility(View.VISIBLE);// ����Ϊ�ɼ�
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// �ȼ�鳤��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// ������пյ�˵��˵���𰸲�����
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		// �������������ȷ��
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sBuffer.append(mBtnSelectWords.get(i).mWordString);
		}
		return (sBuffer.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * �𰸴�����˸����
	 */
	private void sparkTheWords() {
		// ��ʱ�����
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;// ��˸����

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {// ��UI�߳�����ʵ��

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (++mSpardTimes > SPASH_TIMES) {// ��˸SPASH_TIMES�ξ��˳�,SPASH_TIMESΪһ����������ʼ��Ϊ6
							return;
						}
						// ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ����
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;// �ı���˸״̬
					}
				});

			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);// ������ʱ��
	}

}
