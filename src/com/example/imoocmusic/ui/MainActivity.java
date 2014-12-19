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

	/** 答案的状态---正确 */
	public final static int STATUS_ANSWER_RIGHT = 1;

	/** 答案的状态---错误 */
	public final static int STATUS_ANSWER_WRONG = 2;

	/** 答案的状态---不完整 */
	public final static int STATUS_ANSWER_LACK = 3;

	// 闪烁的次数
	public final static int SPASH_TIMES = 6;

	// 对话框ID
	public final static int ID_DIALOG_DELETE_WORD = 1;
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	public final static int ID_DIALOG_LACK_COINS = 3;
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
	private View mPassView;
	// 文字框容器
	private ArrayList<WorkButton> mAllWords;
	private ArrayList<WorkButton> mBtnSelectWords;
	private MyGridView mMyGridView;

	// 已选择文字框的UI容器
	private LinearLayout mViewWordsContainer;

	// 当前关的索引
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;
	// 当前歌曲名称
	private TextView mCurrentSongNamePassView;

	// 当前的歌曲
	private Song mCurrentSong;

	// 当前关的索引
	private int mCurrentStageIndex = -1;

	// 当前金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;

	// 金币的View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 读取数据
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

		// 初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");

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

		// 处理删除按键事件
		handleDeleteWord();

		// 处理提示答案事件
		handleTipAnswer();

		// 处理案件后退事件
		handleBackEvent();
	}

	/**
	 * 处理圆盘中间的播放按钮，控制动画，开始播放音乐
	 */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!isRunning) {
				isRunning = true;

				// 开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);

				// 播放音乐
				MyMediaPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// 保存游戏数据
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		// 停止动画
		mViewPan.clearAnimation();
		// 暂停音乐
		MyMediaPlayer.stopTheSong(MainActivity.this);
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
	 * 加载当前关的数据
	 */
	private void initCurrentStageData() {
		// 第一关的时候隐藏后退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.btn_bar_back);
		if(mCurrentStageIndex == -1){
			backButton.setVisibility(View.INVISIBLE);
		}else {
			backButton.setVisibility(View.VISIBLE);
		}
		

		// 读取当前歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		// 初始化已选择文字
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(90, 90);
		// 清空原来的答案
		mViewWordsContainer.removeAllViews();
		// 增加新的答案框
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// 显示当前关卡索引
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		// 获得数据
		mAllWords = initAllWord();
		// 更新数据 - MyGridView
		mMyGridView.updateData(mAllWords);
		// 一开始就播放音乐
		handlePlayButton();
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
			button.mIsVisable = true;
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
			// Toast.makeText(MainActivity.this, "答案正确了啊，你怎么还不出现",
			// Toast.LENGTH_LONG).show();
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
		// 增加用户的金币数量
		handleCoins(getPassEventCoins());

		// 显示过关界面
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);// 初始化
		mPassView.setVisibility(View.VISIBLE);// 设置为可见

		// 停止未完成的动画
		mViewPan.clearAnimation();

		// 停止正在播放的音乐
		MyMediaPlayer.stopTheSong(MainActivity.this);

		// 播放音效
		MyMediaPlayer.playTone(MainActivity.this,
				MyMediaPlayer.INDEX_STONE_COIN);

		// 当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}

		// 显示歌曲名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}

		// 下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (judgeAppPassed()) {
					// 进入到通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// 进入下一关
					mPassView.setVisibility(View.GONE);// 隐藏过关界面
					// 加载关卡数据
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * 处理后退按钮点击事件
	 */
	private void handleBackEvent() {
		ImageButton backButton = (ImageButton) findViewById(R.id.btn_bar_back);
		// 设置点击事件
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyLog.d(TAG, "当前关卡数：" + mCurrentStageIndex);
				if (mCurrentStageIndex > 0) {
					// 减少当前关卡数
					mCurrentStageIndex -=2;
					// 保存游戏数据
					Util.saveData(MainActivity.this, mCurrentStageIndex - 1,
							mCurrentCoins);

					// 减少金币
					handleCoins(-getPassEventCoins());
					// 重新加载关卡
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * 判断用户是否通关
	 * 
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
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

	/**
	 * 提示文字，自动选择一个答案
	 */
	private void tipAnswer() {
		if (!isAbleDelCoins(-getTipCoins())) {
			// 金币数量不够，提示错误并且返回
			Toast.makeText(MainActivity.this, "您的金币不够呀，请充值~", Toast.LENGTH_LONG)
					.show();
		}
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); ++i) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				onWordButtonClick(findIsAnswerWord(i));
				// 找到一个答案的文字，并且根据当前答案框的文字填入
				tipWord = true;
				// 减少金币数量
				if (!handleCoins(-getTipCoins())) {
					// 金币数量不够，显示对话框，提示用户金币不够
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				break;
			}
		}
		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字来提示用户
			sparkTheWords();
		}
	}

	/**
	 * 删除一个文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 不能减少指定的金币,显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}

		// 将这个索引对应的WordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * 找到一个不是答案的文字并且当前文字是可见的
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
	 * 找到一个答案文字
	 * 
	 * @param index
	 *            当前需要填入答案框的索引
	 * @return 找到返回找到的答案，否则返回空
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
	 * 判断某个文字是否是答案
	 * 
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WorkButton word) {
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {// 遍历答案这个字符串，依次比较
			if (word.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i]))
				Log.d(TAG, "找到一个是答案的文字:::" + word.mWordString);
			return true;
		}
		return false;
	}

	/**
	 * 判断用户当前是否可以减少金币
	 * 
	 * @return 可以：返回true，否则返回false
	 */
	private boolean isAbleDelCoins(int data) {
		// 判断当前总的金币数量是否可被减少
		return (mCurrentCoins + data >= 0);
	}

	/**
	 * 增加或者减少指定数量的金币
	 * 
	 * @param data
	 * @return true 增加/减少成功，false 失败
	 */
	private boolean handleCoins(int data) {
		// 判断当前总的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {// 增加或者减少成功
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {// 金币不够
			Log.d(TAG, "金币不够");
			return false;
		}
	}

	/**
	 * 从配置文件里面读取删除操作用的金币
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 从配置文件里面读取提示操作用的金币
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 从配置文件里面读取过关操作用的金币
	 * 
	 * @return
	 */
	private int getPassEventCoins() {
		return this.getResources().getInteger(R.integer.get_passevent_word);
	}

	/**
	 * 处理删除待文字事件
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
	 * 处理提示按键事件
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

	// 自定义AlertDialog的事件响应
	// 删除错误答案
	private IAlertDialogButtonClickListener mBtnOkDeleteWordListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
	};

	// 答案提示
	private IAlertDialogButtonClickListener mBtnOkTipAnswerListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}
	};

	// 金币不足
	private IAlertDialogButtonClickListener mBtnOkLackCoinsListener = new IAlertDialogButtonClickListener() {
		@Override
		public void onClick() {
			// 执行事件
		}
	};

	/**
	 * 显示对话框
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins()
					+ "个金币去掉一个错误答案吗", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉" + getTipCoins()
					+ "个金币获得一个文字提示", mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不足，去商店购买吧",
					mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
}
