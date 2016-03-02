package com.example.adapter;

import java.util.List;

import com.example.expandtabview.R;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextAdapter extends ArrayAdapter<String> {

    /**
     * 重新定义菜单选项单击接口
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

	private Context mContext;
	private List<String> mListData;                      //集合数据源
	private String[] mArrayData;                         //数组数据源
	private int selectedPos = -1;                       //点击获取到的数据列表ID
	private String selectedText = "";                   //点击获取到条目的名称
	private int normalDrawbleId;                        //item的背景
	private Drawable selectedDrawble;                   //item的checked图
	private float textSize;                             //设置列表的字体大小
	private OnClickListener onClickListener;            //按钮点击事件监听
	private OnItemClickListener mOnItemClickListener; //条目点击事件监听

	/**
	 * 对适配器进行初始化
	 * @param context 上下文
	 * @param listData 列表数据-双列
	 * @param sId item的checked图
	 * @param nId item的背景
	 */
	public TextAdapter(Context context, List<String> listData, int sId, int nId) {
		super(context, R.string.no_data, listData);
		mContext = context;
		mListData = listData;
		selectedDrawble = mContext.getResources().getDrawable(sId);
		normalDrawbleId = nId;
		init();
	}

	private void init() {
		onClickListener = new OnClickListener() {                                             //设置每个条目的监听

			@Override
			public void onClick(View view) {
				selectedPos = (Integer) view.getTag();                                         //获取到被点击条目的id标签
				setSelectedPosition(selectedPos);                                              //保存该位置和item内容
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(view, selectedPos);
				}
			}
		};
	}

    /**
     * 对适配器进行初始化
     * @param context 上下文
     * @param arrayData 列表数据-单列
     * @param sId item的checked图
     * @param nId item的背景
     */
	public TextAdapter(Context context, String[] arrayData, int sId, int nId) {
		super(context, R.string.no_data, arrayData);
		mContext = context;
		mArrayData = arrayData;
		selectedDrawble = mContext.getResources().getDrawable(sId);
		normalDrawbleId = nId;
		init();
	}

	/**
	 * 设置选中的position,并通知列表刷新
	 */
	public void setSelectedPosition(int pos) {
		if (mListData != null && pos < mListData.size()) {                 //如果集合数据源不为空且存在该位置
			selectedPos = pos;                                               //保存该选中位置信息
			selectedText = mListData.get(pos);                             //保存选中条目的内容
			notifyDataSetChanged();                                           //刷新adapter数据
		} else if (mArrayData != null && pos < mArrayData.length) {     //同上（数组）
			selectedPos = pos;
			selectedText = mArrayData[pos];
			notifyDataSetChanged();
		}

	}

	/**
	 * 设置选中的position,但不通知刷新
	 */
	public void setSelectedPositionNoNotify(int pos) {
		selectedPos = pos;                                                  //保存该选中位置内容
		if (mListData != null && pos < mListData.size()) {
			selectedText = mListData.get(pos);                            //保存集合中选中条目的内容
		} else if (mArrayData != null && pos < mArrayData.length) {
			selectedText = mArrayData[pos];                               //保存数组中选中条目的内容
		}
	}

	/**
	 * 获取选中的position
	 */
	public int getSelectedPosition() {                                       //用于回调获取被选内容位置
		if (mArrayData != null && selectedPos < mArrayData.length) {
			return selectedPos;
		}
		if (mListData != null && selectedPos < mListData.size()) {
			return selectedPos;
		}

		return -1;
	}

	/**
	 * 设置列表字体大小
	 */
	public void setTextSize(float tSize) {
		textSize = tSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView == null) {
			view = (TextView) LayoutInflater.from(mContext).
                    inflate(R.layout.choose_item, parent, false);                           //设置item的属性（TextView）
		} else {
			view = (TextView) convertView;
		}
		view.setTag(position);                                                                //自动设置view的标签
		String mString = "";
		if (mListData != null) {
			if (position < mListData.size()) {
				mString = mListData.get(position);                                          //取出集合数据源里面的单个item内容
			}
		} else if (mArrayData != null) {
			if (position < mArrayData.length) {
				mString = mArrayData[position];                                             //取出数组数据源里面的单个item内容
			}
		}
		if (mString.contains("不限"))
			view.setText("不限");
		else
			view.setText(mString);                                                             //设置item内容
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);                             //设置item字体大小

		if (selectedText != null && selectedText.equals(mString)) {
			view.setBackgroundDrawable(selectedDrawble);//设置选中的背景图片
		} else {
			view.setBackgroundDrawable(mContext.getResources().getDrawable(normalDrawbleId));//设置未选中状态背景图片
		}
		view.setPadding(20, 0, 0, 0);                                                            //边距空白
		view.setOnClickListener(onClickListener);
		return view;
	}


}
