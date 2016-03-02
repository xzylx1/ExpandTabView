package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.example.adapter.TextAdapter;
import com.example.expandtabview.R;


public class ViewLeft extends RelativeLayout implements ViewBaseAction{

	private ListView mListView;
	private final String[] items = new String[] { "item1", "item2", "item3", "item4", "item5", "item6" };//显示字段
	private final String[] itemsVaule = new String[] { "1", "2", "3", "4", "5", "6" };//隐藏id
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
	private String mDistance;
	private String showText = "item1";					//显示在btn上的内容
	private Context mContext;

	public String getShowText() {
		return showText;
	}

	public ViewLeft(Context context) {
		super(context);
		init(context);
	}

	public ViewLeft(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ViewLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_distance, this, true);									//填充一个单列布局
		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_left));		//设置布局背景
		mListView = (ListView) findViewById(R.id.listView);										//找到条目listview控件
		adapter = new TextAdapter(context, items,
				R.drawable.choose_item_right, R.drawable.choose_eara_item_selector);			//初始化并绑定adapter
		adapter.setTextSize(17);																	//设置列表字体大小
		if (mDistance != null) {
			for (int i = 0; i < itemsVaule.length; i++) {										//遍历列表
				if (itemsVaule[i].equals(mDistance)) {											//列表的id等于已选择的id
					adapter.setSelectedPositionNoNotify(i);									//设置选中的position,但不通知刷新
					showText = items[i];															//设置显示在btn上的内容
					break;
				}
			}
		}
		mListView.setAdapter(adapter);
		adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (mOnSelectListener != null) {
					showText = items[position];
					mOnSelectListener.getValue(itemsVaule[position], items[position]);
				}
			}
		});
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String distance, String showText);
	}

	@Override
	public void hide() {

	}

	@Override
	public void show() {

	}

}
