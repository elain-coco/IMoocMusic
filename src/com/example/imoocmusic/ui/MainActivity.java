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
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.example.imoocmusic.R;
import com.example.imoocmusic.data.Const;
import com.example.imoocmusic.model.IAlertDialogButtonClickListener;
import com.example.imoocmusic.model.IWordButtonClickListener;
import com.example.imoocmusic.model.Song;
import com.example.imoocmusic.model.WorkButton;
import com.example.imoocmusic.myui.MyGridView;
import com.example.imoocmusic.util.MyLog;
import com.example.imoocmusic.util.MyMediaPlayer;
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

	// �Ի���ID
	public final static int ID_DIALOG_DELETE_WORD = 1;
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	public final static int ID_DIALOG_LACK_COINS = 3;
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
	private View mPassView;
	// ���ֿ�����
	private ArrayList<WorkButton> mAllWords;
	private ArrayList<WorkButton> mBtnSelectWords;
	private MyGridView mMyGridView;

	// ��ѡ�����ֿ��UI����
	private LinearLayout mViewWordsContainer;

	// ��ǰ�ص�����
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;
	// ��ǰ��������
	private TextView mCurrentSongNamePassView;

	// ��ǰ�ĸ���
	private Song mCurrentSong;

	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;

	// ��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;

	// ��ҵ�View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ȡ����
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

		// ��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");

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

		// ����ɾ�������¼�
		handleDeleteWord();

		// ������ʾ���¼�
		handleTipAnswer();

		// �����������¼�
		handleBackEvent();
	}

	/**
	 * ����Բ���м�Ĳ��Ű�ť�����ƶ�������ʼ��������
	 */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!isRunning) {
				isRunning = true;

				// ��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);

				// ��������
				MyMediaPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// ������Ϸ����
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		// ֹͣ����
		mViewPan.clearAnimation();
		// ��ͣ����
		MyMediaPlayer.stopTheSong(MainActivity.this);
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
	 * ���ص�ǰ�ص�����
	 */
	private void initCurrentStageData() {
		// ��һ�ص�ʱ�����غ��˰�ť
		ImageButton backButton = (ImageButton) findViewById(R.id.btn_bar_back);
		if(mCurrentStageIndex == -1){
			backButton.setVisibility(View.INVISIBLE);
		}else {
			backButton.setVisibility(View.VISIBLE);
		}
		

		// ��ȡ��ǰ������Ϣ
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		// ��ʼ����ѡ������
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(90, 90);
		// ���ԭ���Ĵ�
		mViewWordsContainer.removeAllViews();
		// �����µĴ𰸿�
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// ��ʾ��ǰ�ؿ�����
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		// �������
		mAllWords = initAllWord();
		// �������� - MyGridView
		mMyGridView.updateData(mAllWords);
		// һ��ʼ�Ͳ�������
		handlePlayButton();
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
			button.mIsVisable = true;
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
			// Toast.makeText(MainActivity.this, "����ȷ�˰�������ô��������",
			// Toast.LENGTH_LONG).show();
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
		// �����û��Ľ������
		handleCoins(getPassEventCoins());

		// ��ʾ���ؽ���
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);// ��ʼ��
		mPassView.setVisibility(View.VISIBLE);// ����Ϊ�ɼ�

		// ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();

		// ֹͣ���ڲ��ŵ�����
		MyMediaPlayer.stopTheSong(MainActivity.this);

		// ������Ч
		MyMediaPlayer.playTone(MainActivity.this,
				MyMediaPlayer.INDEX_STONE_COIN);

		// ��ǰ�ص�����
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}

		// ��ʾ��������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}

		// ��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (judgeAppPassed()) {
					// ���뵽ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// ������һ��
					mPassView.setVisibility(View.GONE);// ���ع��ؽ���
					// ���عؿ�����
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * ������˰�ť����¼�
	 */
	private void handleBackEvent() {
		ImageButton backButton = (ImageButton) findViewById(R.id.btn_bar_back);
		// ���õ���¼�
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyLog.d(TAG, "��ǰ�ؿ�����" + mCurrentStageIndex);
				if (mCurrentStageIndex > 0) {
					// ���ٵ�ǰ�ؿ���
					mCurrentStageIndex -=2;
					// ������Ϸ����
					Util.saveData(MainActivity.this, mCurrentStageIndex - 1,
							mCurrentCoins);

					// ���ٽ��
					handleCoins(-getPassEventCoins());
					// ���¼��عؿ�
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * �ж��û��Ƿ�ͨ��
	 * 
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
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

	/**
	 * ��ʾ���֣��Զ�ѡ��һ����
	 */
	private void tipAnswer() {
		if (!isAbleDelCoins(-getTipCoins())) {
			// ���������������ʾ�����ҷ���
			Toast.makeText(MainActivity.this, "���Ľ�Ҳ���ѽ�����ֵ~", Toast.LENGTH_LONG)
					.show();
		}
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); ++i) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				onWordButtonClick(findIsAnswerWord(i));
				// �ҵ�һ���𰸵����֣����Ҹ��ݵ�ǰ�𰸿����������
				tipWord = true;
				// ���ٽ������
				if (!handleCoins(-getTipCoins())) {
					// ���������������ʾ�Ի�����ʾ�û���Ҳ���
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				break;
			}
		}
		// û���ҵ��������Ĵ�
		if (!tipWord) {
			// ��˸��������ʾ�û�
			sparkTheWords();
		}
	}

	/**
	 * ɾ��һ������
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeleteWordCoins())) {
			// ���ܼ���ָ���Ľ��,��ʾ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}

		// �����������Ӧ��WordButton����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * �ҵ�һ�����Ǵ𰸵����ֲ��ҵ�ǰ�����ǿɼ���
	 * 
	 * @return
	 */
	private WorkButton findNotAnswerWord() {
		Random random = new Random();
		WorkButton buf = null;

		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);

			buf = mAllWords.get(index);
			return buf;
			// if (buf.mIsVisable && !isTheAnswerWord(buf)) {
			// return buf;
			// }
		}
	}

	/**
	 * �ҵ�һ��������
	 * 
	 * @param index
	 *            ��ǰ��Ҫ����𰸿������
	 * @return �ҵ������ҵ��Ĵ𰸣����򷵻ؿ�
	 */
	private WorkButton findIsAnswerWord(int index) {
		WorkButton buf = null;
		for (int i = 0; i < MyGridView.COUNTS_WORDS; ++i) {
			buf = mAllWords.get(i);
			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		return null;
	}

	/**
	 * �ж�ĳ�������Ƿ��Ǵ�
	 * 
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WorkButton word) {
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {// ����������ַ��������αȽ�
			if (word.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i]))
				Log.d(TAG, "�ҵ�һ���Ǵ𰸵�����:::" + word.mWordString);
			return true;
		}
		return false;
	}

	/**
	 * �ж��û���ǰ�Ƿ���Լ��ٽ��
	 * 
	 * @return ���ԣ�����true�����򷵻�false
	 */
	private boolean isAbleDelCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		return (mCurrentCoins + data >= 0);
	}

	/**
	 * ���ӻ��߼���ָ�������Ľ��
	 * 
	 * @param data
	 * @return true ����/���ٳɹ���false ʧ��
	 */
	private boolean handleCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if (mCurrentCoins + data >= 0) {// ���ӻ��߼��ٳɹ�
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {// ��Ҳ���
			Log.d(TAG, "��Ҳ���");
			return false;
		}
	}

	/**
	 * �������ļ������ȡɾ�������õĽ��
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * �������ļ������ȡ��ʾ�����õĽ��
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * �������ļ������ȡ���ز����õĽ��
	 * 
	 * @return
	 */
	private int getPassEventCoins() {
		return this.getResources().getInteger(R.integer.get_passevent_word);
	}

	/**
	 * ����ɾ���������¼�
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});

	}

	/**
	 * ������ʾ�����¼�
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}

	// �Զ���AlertDialog���¼���Ӧ
	// ɾ�������
	private IAlertDialogButtonClickListener mBtnOkDeleteWordListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// ִ���¼�
			deleteOneWord();
		}
	};

	// ����ʾ
	private IAlertDialogButtonClickListener mBtnOkTipAnswerListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// ִ���¼�
			tipAnswer();
		}
	};

	// ��Ҳ���
	private IAlertDialogButtonClickListener mBtnOkLackCoinsListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// ִ���¼�
		}
	};

	/**
	 * ��ʾ�Ի���
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeleteWordCoins()
					+ "�����ȥ��һ���������", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipCoins()
					+ "����һ��һ��������ʾ", mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ��㣬ȥ�̵깺���",
					mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
}
