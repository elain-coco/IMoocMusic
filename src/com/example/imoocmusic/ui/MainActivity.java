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

	/** 答案的状态---正确 */
	public final static int STATUS_ANSWER_RIGHT = 1;

	/** 答案的状态---错误 */
	public final static int STATUS_ANSWER_WRONG = 2;

	/** 答案的状态---不完整 */
	public final static int STATUS_ANSWER_LACK = 3;

	// 闪烁的次数
	public final static int SPASH_TIMES = 6;
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

	// 过关界面
	private View passView;
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
	 * 初始化当前数据
	 */
	private void initCurrentStageData() {
		// 读取当前歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

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
		String[] words = generateWords();
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WorkButton button = new WorkButton();
			button.mWordString = words[i];
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
			final WorkButton holder = new WorkButton();
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			// 设置已选择框的点击事件
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
	 * 生成所有的待选文字
	 * 
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();
		String[] words = new String[MyGridView.COUNTS_WORDS];

		// 存入歌名
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// 获取随机文字并存入数组
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个元素与第一个元素交换，然后再在第二个之后
		// 随机选择一个元素与第二个元素交换，直到最后一个元素，这样每个元素被交换的概率都为1/n

		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		return words;
	}

	/**
	 * 随机生成字符
	 * 
	 * @return 随机的字符
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos, lowPos;// 高位 低位
		Random random = new Random();
		hightPos = (176 + Math.abs(random.nextInt(39)));
		// 这里的39是可以调的，01 - 94 即可，经测试39以后就为生僻字了
		lowPos = (161 + Math.abs(random.nextInt(93)));

		// 置于b这个byte数组中
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
	 * 清除已选择框的答案
	 * 
	 * @param button
	 */
	private void clearTheAnswer(WorkButton button) {
		button.mViewButton.setText("");
		button.mWordString = "";
		button.mIsVisable = false;

		// 设置待选框的可见性
		setButtonVisiable(mAllWords.get(button.mIndex), View.VISIBLE);
	}

	/**
	 * 设置答案的内容以及可见性
	 * 
	 * @param workButton
	 */
	private void setSelectWord(WorkButton workButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {// 依次遍历已选择文字
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框的内容以及可见性
				mBtnSelectWords.get(i).mViewButton
						.setText(workButton.mWordString);
				mBtnSelectWords.get(i).mIsVisable = true;
				mBtnSelectWords.get(i).mWordString = workButton.mWordString;
				// 记录索引
				mBtnSelectWords.get(i).mIndex = workButton.mIndex;
				// Log
				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");
				// 设置待选框的可见性
				setButtonVisiable(workButton, View.INVISIBLE);
				break;// 这一点非常重要，不然为空的待选框都会被选择
			}
		}
	}

	/**
	 * 设置待选文字框是否可见
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WorkButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisable = (visibility == View.VISIBLE) ? true : false;
		// Log 打印按钮的可见性
		MyLog.d(TAG, button.mIsVisable + "");
	}

	@Override
	public void onWordButtonClick(WorkButton wordButton) {
		// TODO Auto-generated method stub
		setSelectWord(wordButton);
		// 获得答案的状态
		int checkResult = checkTheAnswer();
		// 检查答案
		switch (checkResult) {
		case STATUS_ANSWER_RIGHT:
			// 答案正确，获得奖励并且过关
//			Toast.makeText(MainActivity.this, "答案正确了啊，你怎么还不出现",
//					Toast.LENGTH_LONG).show();
			handlePassEvent();
			break;
		case STATUS_ANSWER_WRONG:
			// 答案错误，闪烁文字并且提示错误
			sparkTheWords();
			break;
		default:
			// 答案缺失,文字颜色设置为白色
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			break;
		}
	}

	/**
	 * 处理所有过关界面及事件
	 */
	private void handlePassEvent() {
		passView = (LinearLayout) this.findViewById(R.id.pass_view);// 初始化
		passView.setVisibility(View.VISIBLE);// 设置为可见
	}

	/**
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 先检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 入如果有空的说明说明答案不完整
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		// 答案完整则检查答案正确性
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sBuffer.append(mBtnSelectWords.get(i).mWordString);
		}
		return (sBuffer.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * 答案错误闪烁文字
	 */
	private void sparkTheWords() {
		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;// 闪烁次数

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {// 在UI线程里面实现

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (++mSpardTimes > SPASH_TIMES) {// 闪烁SPASH_TIMES次就退出,SPASH_TIMES为一个常量，初始化为6
							return;
						}
						// 执行闪烁逻辑：交替显示红色和白色文字
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;// 改变闪烁状态
					}
				});

			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);// 启动定时器
	}

}
