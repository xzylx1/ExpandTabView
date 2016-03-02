package com.example.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;
import com.example.expandtabview.R;

import java.util.ArrayList;

/**
 * 菜单控件头部，封装了下拉动画，动态生成头部按钮个数
 *
 * @author yueyueniao
 */

public class ExpandTabView extends LinearLayout implements OnDismissListener {


	/**
	 * 自定义tabitem点击回调接口
	 */
	public interface OnButtonClickListener {
		public void onClick(int selectPosition);
	}

	/**
	 * 设置tabitem的点击监听事件
	 */
	public void setOnButtonClickListener(OnButtonClickListener l) {
		mOnButtonClickListener = l;
	}

	private OnButtonClickListener mOnButtonClickListener;									//定义btn的监听事件
	private ToggleButton selectedButton;														//存入选中btn状态
	private ArrayList<String> mTextArray = new ArrayList<String>();							//数据列表名称
	private ArrayList<RelativeLayout> mViewArray = new ArrayList<RelativeLayout>();			//按钮各布局PopWindow布局集合
	private ArrayList<ToggleButton> mToggleButton = new ArrayList<ToggleButton>();			//按钮集合
	private Context mContext;
	private final int SMALL = 0;
	private int displayWidth;
	private int displayHeight;
	private PopupWindow popupWindow;
	private int selectPosition;													//存入的按钮的ID

	public ExpandTabView(Context context) {
		super(context);
		init(context);
	}

	public ExpandTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 根据选择的位置设置tabitem显示的值
	 */
	public void setTitle(String valueText, int position) {
		if (position < mToggleButton.size()) {
			mToggleButton.get(position).setText(valueText);
		}
	}

	/**
	 * 根据选择的位置获取tabitem显示的值
	 */
	public String getTitle(int position) {
		if (position < mToggleButton.size() && mToggleButton.get(position).getText() != null) {
			return mToggleButton.get(position).getText().toString();
		}
		return "";
	}

	/**
	 * 设置tabitem的个数和初始值
	 * @param textArray 横向点击条目名称
	 * @param viewArray 横向点击条目个数
	 */
	public void setValue(ArrayList<String> textArray, ArrayList<View> viewArray) {
		if (mContext == null) {
			return;
		}
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTextArray = textArray;
		for (int i = 0; i < viewArray.size(); i++) {
			final RelativeLayout r = new RelativeLayout(mContext);						//新建布局
			int maxHeight = (int) (displayHeight * 0.5);									//定义布局高度
			RelativeLayout.LayoutParams rl = new RelativeLayout.
					LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);		//填充布局宽高
			rl.leftMargin = 20;
			rl.rightMargin = 20;															//Margin留边左右距离
			r.addView(viewArray.get(i), rl);												//生成各自弹窗布局
			mViewArray.add(r);																//几个条目的弹窗布局集合
			r.setTag(SMALL);
			ToggleButton tButton = (ToggleButton) inflater.
					inflate(R.layout.toggle_button, this, false);						//按钮样式
			addView(tButton);																//添加按钮
			View line = new TextView(mContext);
			line.setBackgroundResource(R.drawable.choosebar_line);						//定义view的背景为分割线
			if (i < viewArray.size() - 1) {
				LinearLayout.LayoutParams lp = new LinearLayout.
						LayoutParams(1, LinearLayout.LayoutParams.FILL_PARENT);
				addView(line, lp);															//如果不是最后一个按钮添加该分割线
			}
			mToggleButton.add(tButton);													//添加到按钮集合
			tButton.setTag(i);																//设定按钮标签
			tButton.setText(mTextArray.get(i));											//设置按钮名称

			r.setOnClickListener(new OnClickListener() {									//设置布局监听点击收回Popwindow
				@Override
				public void onClick(View v) {
					onPressBack();
				}
			});

			r.setBackgroundColor(mContext.getResources().
					getColor(R.color.popup_main_background));							//设置PopWindow的背景颜色
			tButton.setOnClickListener(new OnClickListener() {								//设置按钮的监听事件
				@Override
				public void onClick(View view) {
					// initPopupWindow();
					ToggleButton tButton = (ToggleButton) view;

					if (selectedButton != null && selectedButton != tButton) {
						selectedButton.setChecked(false);
					}
					selectedButton = tButton;												//传入Btn和选中Btn进行切换
					selectPosition = (Integer) selectedButton.getTag();					//保存选中的按钮id
					startAnimation();														//初始化PopWindow
					if (mOnButtonClickListener != null && tButton.isChecked()) {
						mOnButtonClickListener.onClick(selectPosition);
						Log.e("000", "onClick: 111111111111111111111111");
					}
				}
			});
		}
	}

	private void startAnimation() {
		if (popupWindow == null) {
			popupWindow = new PopupWindow(mViewArray.get(selectPosition),
					displayWidth, displayHeight);										//初始化PopWindow
			popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);				//设置弹窗效果
			popupWindow.setFocusable(false);												//设置不允许焦点
			popupWindow.setOutsideTouchable(true);										//设置留边触摸事件
		}

		if (selectedButton.isChecked()) {													//如果按钮已被选中
			if (!popupWindow.isShowing()) {												//如果Popwindow未显示
				showPopup(selectPosition);												//显示PopWindow
			} else {
				popupWindow.setOnDismissListener(this);									//撤销监听
				popupWindow.dismiss();														//撤销PopWindow
				hideView();																	//隐藏PopWindow
			}
		} else {
			if (popupWindow.isShowing()) {												//如果Popwindow显示
				popupWindow.dismiss();														//撤销PopWindow
				hideView();																	//隐藏PopWindow
			}
		}
	}

	private void showPopup(int position) {												//显示弹窗
		View tView = mViewArray.get(selectPosition).getChildAt(0);						//选中该按钮id对应的PopWindow
		if (tView instanceof ViewBaseAction) {											//如果是实现了VBA的viewLf.Md.Ri.
			ViewBaseAction f = (ViewBaseAction) tView;										//强转VBA
			f.show();																		//回调该Veiw的show方法
		}
		if (popupWindow.getContentView() != mViewArray.get(position)) {					//如果当前PopWindow非所选btn的pw
			popupWindow.setContentView(mViewArray.get(position));						//设置PopWindow为所选btn的pw
		}
		popupWindow.showAsDropDown(this, 0, 0);											//以下降的anmi显示popwindow
	}

	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			hideView();
			if (selectedButton != null) {
				selectedButton.setChecked(false);
			}
			return true;
		} else {
			return false;
		}

	}

	private void hideView() {																	//隐藏弹窗
		View tView = mViewArray.get(selectPosition).getChildAt(0);							//选中该按钮id对应的PopWindow
		if (tView instanceof ViewBaseAction) {												//如果是实现了VBA的viewLf.Md.Ri.
			ViewBaseAction f = (ViewBaseAction) tView;											//强转VBA
			f.hide();																			//回调该Veiw的hide方法
		}
	}

	private void init(Context context) {
		mContext = context;
		displayWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
		setOrientation(LinearLayout.HORIZONTAL);
	}

	@Override
	public void onDismiss() {
		showPopup(selectPosition);
		popupWindow.setOnDismissListener(null);
	}
}
