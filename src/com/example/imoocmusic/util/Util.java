package com.example.imoocmusic.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.imoocmusic.R;
import com.example.imoocmusic.data.Const;
import com.example.imoocmusic.model.IAlertDialogButtonClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Util {
	public static AlertDialog mAlertDialog;

	/**
	 * 获得一个View
	 * 
	 * @param context
	 * @param layoutId
	 * @return
	 */
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
	}

	/**
	 * 界面跳转
	 * 
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti) {
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent); // 跳转

		// 关闭当前的Activity
		((Activity) context).finish();
	}

	/**
	 * 显示自定义对话框
	 * 
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(final Context context, String message,
			final IAlertDialogButtonClickListener listener) {

		View dialogView = null;
		// 创建builder
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_Transparent);
		// 创建view
		dialogView = getView(context, R.layout.dialog_view);
		// 初始化View的控件
		ImageButton btnOkView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);
		TextView txtMessageView = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);
		// 为 txtMessageView 设置文字内容
		txtMessageView.setText(message);
		// 设置按钮点击事件
		btnOkView.setOnClickListener(new View.OnClickListener() {// 设置ok的点击事件

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 不为空就关闭对话框
						if (mAlertDialog != null) {
							mAlertDialog.cancel();
						}
						// 如果不为空，事件回调
						if (listener != null) {
							listener.onClick();
						}

						// 播放音效
						MyMediaPlayer.playTone(context,
								MyMediaPlayer.INDEX_STONE_ENTER);
					}
				});

		btnCancelView.setOnClickListener(new View.OnClickListener() {// 设置取消的点击事件

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 不为空就关闭对话框
						if (mAlertDialog != null) {
							mAlertDialog.cancel();
						}

						// 播放音效
						MyMediaPlayer.playTone(context,
								MyMediaPlayer.INDEX_STONE_CANCEL);
					}
				});
		// 为dialog设置view
		builder.setView(dialogView);
		mAlertDialog = builder.create();// 创建对话框

		// 显示对话框
		mAlertDialog.show();

	}

	/**
	 * 游戏的数据保存
	 * 
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context, int stageIndex, int coins) {
		FileOutputStream fiStream = null;
		try {
			fiStream = context.openFileOutput(Const.FILE_NAME_SAVE_DATA,
					context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fiStream);

			// 写数据
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fiStream != null) {
				try {
					fiStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 游戏数据的读取
	 * 
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context) {
		FileInputStream fis = null;
		int[] datas = { -1, Const.TOTAL_COINS };// 初始化数据
		try {
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);

			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return datas;
	}
}
