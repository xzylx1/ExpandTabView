package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.adapter.TextAdapter;
import com.example.expandtabview.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class ViewMiddle extends LinearLayout implements ViewBaseAction {


	public interface OnSelectListener {														    //提供依附列item选中回调接口
		public void getValue(String showText);														//用于activity实现调用更新截面数据
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {							//传递回调实例
		mOnSelectListener = onSelectListener;
	}

	private ListView regionListView;															    //首列listview
	private ListView plateListView; 															    //依附列listview
	private ArrayList<String> groups = new ArrayList<String>();                                  //首列数据源
	private LinkedList<String> childrenItem = new LinkedList<String>();							//依附列数据源
	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();	//包含首列+依附列数据源
	private TextAdapter earaListViewAdapter;											    	//首列listview适配器
	private TextAdapter plateListViewAdapter;											    	//依附列listview适配器
	private OnSelectListener mOnSelectListener;											    	//回调接口
	private int tEaraPosition = 0;											    				//保存首列item中的position
	private int tBlockPosition = 0;											    				//保存依附列item选中的position
	private String showString = "不限";											    			//需要传递/显示的内容

	public ViewMiddle(Context context) {
		super(context);
		init(context);
	}

	public ViewMiddle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

//	public void updateShowText(String showArea, String showBlock) {
//		mShowArea = showArea;
//		mShowBlock = showBlock;
//		if (showArea == null || showBlock == null) {
//			return;
//		}
//		for (int i = 0; i < groups.size(); i++) {
//			if (groups.get(i).equals(showArea)) {
//				earaListViewAdapter.setSelectedPosition(i);
//				childrenItem.clear();
//				if (i < children.size()) {
//					childrenItem.addAll(children.get(i));
//				}
//				tEaraPosition = i;
//				break;
//			}
//		}
//		for (int j = 0; j < childrenItem.size(); j++) {
//			if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
//				plateListViewAdapter.setSelectedPosition(j);
//				tBlockPosition = j;
//				break;
//			}
//		}
//		setDefaultSelect();
//	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_region, this, true);									//填充一个双列PopWindow布局
		regionListView = (ListView) findViewById(R.id.listView);								//首列listview
		plateListView = (ListView) findViewById(R.id.listView2);								//依附listview
		setBackgroundDrawable(getResources().getDrawable(
				R.drawable.choosearea_bg_mid));												//设置背景图

		for(int i=0;i<10;i++){																	//初始化首列数据源
			groups.add(i+"行");																//添加进首列集合
			LinkedList<String> tItem = new LinkedList<String>();
			for(int j=0;j<15;j++){
				tItem.add(i+"行"+j+"列");														//每首列每行数据源（依附列）
			}
			children.put(i, tItem);															//添加进首列集合
		}

		earaListViewAdapter = new TextAdapter(context, groups,								//绑定首列适配器
				R.drawable.choose_item_selected,												//设置首列被选中item的背景logo
				R.drawable.choose_eara_item_selector);										//根据状态设置item的背景
		earaListViewAdapter.setTextSize(17);													//设置item的字体大小
		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);					//设置选中的position,但不通知刷新
		regionListView.setAdapter(earaListViewAdapter);									//首列listview添加适配器
		earaListViewAdapter																	//设置item监听
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {		//对应adapter中的item点击事件 传入监听器

					@Override
					public void onItemClick(View view, int position) {						//实现回调方法
						if (position < children.size()) {
							childrenItem.clear();												//依附列数据源清空
							childrenItem.addAll(children.get(position));						//依附列数据源更新
							plateListViewAdapter.notifyDataSetChanged();						//更新依附列数据显示
						}
					}
				});
		if (tEaraPosition < children.size())													//如果首列item中的position存在
			childrenItem.addAll(children.get(tEaraPosition));								//设置依附列被选中item的依附列数据

		plateListViewAdapter = new TextAdapter(context, childrenItem,						//绑定依附列适配器
				R.drawable.choose_item_right,													//设置依附列被选中item的状态图标
				R.drawable.choose_plate_item_selector);										//根据状态设置item的背景
		plateListViewAdapter.setTextSize(15);												//设置item的字体大小
		plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);					//设置选中的position,但不通知刷新
		plateListView.setAdapter(plateListViewAdapter);									//依附列listview添加适配器
		plateListViewAdapter																	//设置item监听
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {		//对应adapter中的item点击事件 传入监听器

					@Override
					public void onItemClick(View view, final int position) {					//实现回调方法
						showString = childrenItem.get(position);								//传回依附列的item内容
						if (mOnSelectListener != null) {
							mOnSelectListener.getValue(showString);							//回传activity选中item内容
						}

					}
				});
		if (tBlockPosition < childrenItem.size())											//如果来依附列item中的position存在
			showString = childrenItem.get(tBlockPosition);				    			//设置需要传递/显示的内容
		if (showString.contains("不限")) {										    		//如果该内容包含“不限”
			showString = showString.replace("不限", "");									//则设置为空内容
		}
		setDefaultSelect();										    							//设置listview的默认显示的内容
	}

	public void setDefaultSelect() {						    								//设置listview的默认显示的内容
		regionListView.setSelection(tEaraPosition);
		plateListView.setSelection(tBlockPosition);
	}

	public String getShowText() {
		return showString;
	}

//	public void setShowText_Ntfy(int str){
//		tEaraPosition = str;
//		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);					//设置选中的position,但不通知刷新
//	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		Log.e("-------------", "mid: hide");
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Log.e("-------------", "mid: show");
	}
}
