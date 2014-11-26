package com.example.imoocmusic.myui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.imoocmusic.model.IWordButtonClickListener;
import com.example.imoocmusic.model.WorkButton;
import com.example.imoocmusic.ui.R;
import com.example.imoocmusic.util.Util;

public class MyGridView extends GridView {
	public final static int COUNTS_WORDS = 24;

	private ArrayList<WorkButton> mArrayList = new ArrayList<WorkButton>();

	private MyGridAdapter mAdapter;

	private Context mContext;
	
	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonClickListener;
	public MyGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		mContext = context;

		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WorkButton> list) {
		mArrayList = list;

		// 重新设置数据源
		setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter {
		public int getCount() {
			return mArrayList.size();
		}

		public Object getItem(int pos) {
			return mArrayList.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		public View getView(int pos, View v, ViewGroup p) {
			final WorkButton holder;

			if (v == null) {
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);

				holder = mArrayList.get(pos);
				// 加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext,
						R.anim.scale);
				//设置具体的延迟时间
				mScaleAnimation.setStartOffset(pos * 100);
				holder.mIndex = pos;
				holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
				holder.mViewButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mWordButtonClickListener.onWordButtonClick(holder);
					}
				});
				
				v.setTag(holder);
			} else {
				holder = (WorkButton) v.getTag();
			}
			//播放动画
			v.startAnimation(mScaleAnimation);
			
			holder.mViewButton.setText(holder.mWordString);

			return v;
		}
	}
	
	/**
	 * 注册监听接口
	 * @param listener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener){
		mWordButtonClickListener = listener;
	}
}
