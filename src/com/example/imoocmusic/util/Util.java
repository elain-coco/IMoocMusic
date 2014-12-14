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
	 * ���һ��View
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
	 * ������ת
	 * 
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti) {
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent); // ��ת

		// �رյ�ǰ��Activity
		((Activity) context).finish();
	}

	/**
	 * ��ʾ�Զ���Ի���
	 * 
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(final Context context, String message,
			final IAlertDialogButtonClickListener listener) {

		View dialogView = null;
		// ����builder
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_Transparent);
		// ����view
		dialogView = getView(context, R.layout.dialog_view);
		// ��ʼ��View�Ŀؼ�
		ImageButton btnOkView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);
		TextView txtMessageView = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);
		// Ϊ txtMessageView ������������
		txtMessageView.setText(message);
		// ���ð�ť����¼�
		btnOkView.setOnClickListener(new View.OnClickListener() {// ����ok�ĵ���¼�

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// ��Ϊ�վ͹رնԻ���
						if (mAlertDialog != null) {
							mAlertDialog.cancel();
						}
						// �����Ϊ�գ��¼��ص�
						if (listener != null) {
							listener.onClick();
						}

						// ������Ч
						MyMediaPlayer.playTone(context,
								MyMediaPlayer.INDEX_STONE_ENTER);
					}
				});

		btnCancelView.setOnClickListener(new View.OnClickListener() {// ����ȡ���ĵ���¼�

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// ��Ϊ�վ͹رնԻ���
						if (mAlertDialog != null) {
							mAlertDialog.cancel();
						}

						// ������Ч
						MyMediaPlayer.playTone(context,
								MyMediaPlayer.INDEX_STONE_CANCEL);
					}
				});
		// Ϊdialog����view
		builder.setView(dialogView);
		mAlertDialog = builder.create();// �����Ի���

		// ��ʾ�Ի���
		mAlertDialog.show();

	}

	/**
	 * ��Ϸ�����ݱ���
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

			// д����
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
	 * ��Ϸ���ݵĶ�ȡ
	 * 
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context) {
		FileInputStream fis = null;
		int[] datas = { -1, Const.TOTAL_COINS };// ��ʼ������
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
