package com.example.imoocmusic.myui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.imoocmusic.model.WorkButton;
import com.example.imoocmusic.ui.R;
import com.example.imoocmusic.util.Util;

public class MyGridView extends GridView {
	public final static int COUNTS_WORDS = 24;
	
	private ArrayList<WorkButton> mArrayList = new ArrayList<WorkButton>();

	private MyGridAdapter mAdapter;
	
	private Context mContext;
	
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
			WorkButton holder;
			
			if (v == null) {
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);
				
				holder = mArrayList.get(pos);
				
				holder.mIndex = pos;
				holder.mViewButton = (Button)v.findViewById(R.id.item_btn);
				
				v.setTag(holder);
			} else {
				holder = (WorkButton)v.getTag();
			}
			
			holder.mViewButton.setText(holder.mWordString);
			
			return v;
		}
	}
}
