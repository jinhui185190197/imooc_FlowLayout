package rdc.jim.m_flowlayout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup{
	
	/*
	 * 保存所有的子View
	 * 以行做单位
	 */
	private List<List<View>> mAllChildView = new ArrayList<List<View>>();
	//保存所有的行高
	private List<Integer> mLinesHeight = new ArrayList<Integer>();

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public FlowLayout(Context context) {
		this(context,null);
	}
	
	/**
	 * 决定内部子view的宽和高
	 * 决定自身宽和高
	 * 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//转换该控件的宽高属性
		//获得宽的数值和布局模式
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modelWidth = MeasureSpec.getMode(widthMeasureSpec);
		//获得高的数值和布局模式
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modelHeight = MeasureSpec.getMode(heightMeasureSpec);
		
		
		//注意,以上情况仅当模式为fill_parent或 100dp或match_parent
		//这种参数才会为EXACTLY,即sizeWidth和sizeHeight有确定值
		//但模式为warp_content时,需要我们通过计算所有子控件所占的空间进行确定
		
		//保存宽高属性
		int width = 0;
		int height = 0;
		
		
		//记录每一行的高度
		int lineWidth = 0;
		int lineHeight = 0;
		
		//获取子控件格数
		int childCount = getChildCount();
		
		for(int i = 0;i<childCount;i++){
			View childView = getChildAt(i);
			//测量子View的宽和高
			//该方法会通知子view根据父控件的模式和自身的布局属性
			//对自身的宽高进行测量
			//这样子View才有确定的宽高
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
			//得到LayoutParams
			//子VIew的LayoutParams由其父类所决定
			//这里FLowLayout绑定的是MarginLayoutParams
			MarginLayoutParams lp = (MarginLayoutParams)childView.getLayoutParams();
			
			//子View占据的宽高
			//自身宽度+左边距+右边距
			int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			//高度同理
			int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			
			//换行
			if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
				//比较获得最大宽度
				width = Math.max(width, lineWidth);
				lineWidth = childWidth;
				
				height += lineHeight;
				lineHeight = childHeight;
			}else{
				//不换行
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			
			if(i == childCount - 1){
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}
		Log.w("TAG", sizeWidth+"");
		Log.w("TAG", width + getPaddingLeft() + getPaddingRight()+"");
		Log.w("TAG", height + getPaddingBottom() + getPaddingTop()+"");
		
		//根据模式判断使用何种宽高
		setMeasuredDimension(
				modelWidth == MeasureSpec.EXACTLY ? sizeWidth:width + getPaddingLeft() + getPaddingRight(), 
				modelHeight == MeasureSpec.EXACTLY ? sizeHeight:height + getPaddingBottom() + getPaddingTop());
		
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 控制子控件的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		
		mAllChildView.clear();
		mLinesHeight.clear();
		//当前控件的宽度
		//此时当前控件已经经过测量
		//所以getWidth有值
		int width  = getWidth();
		
		int lineWidth = 0;
		int lineHeight = 0; 
		MarginLayoutParams lp;
		
		List<View> lineViews = new ArrayList<View>();
		int childCount = getChildCount();
		
		
		for(int i = 0; i<childCount;i++){
			View childView = getChildAt(i);
			lp = (MarginLayoutParams)childView.getLayoutParams();
			
			int childWidth = childView.getMeasuredWidth();
			int childHeight = childView.getMeasuredHeight();
			
			//如果需要换行
			if((childWidth + lineWidth + lp.leftMargin + lp.rightMargin) > width - getPaddingLeft() - getPaddingRight()){
				 //记录行高
				mLinesHeight.add(lineHeight);
				//记录当前行的View
				mAllChildView.add(lineViews);
				
				//重置行宽和行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				lineViews = new ArrayList<View>();
			}
			lineWidth +=childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(childHeight + lp.topMargin + lp.bottomMargin, lineHeight);
			lineViews.add(childView);
		}
		//处理最后一行
		mAllChildView.add(lineViews);
		mLinesHeight.add(lineHeight);
		
		//设置子View位置
		int top = getPaddingLeft();
		int left = getPaddingTop();
		
		//行数
		int lineNum = mAllChildView.size();
		for(int i = 0; i<lineNum; i++){
			//当前行所有的view
			lineViews = mAllChildView.get(i);
			lineHeight = mLinesHeight.get(i);
			//逐个取出处理
			for(int j = 0; j<lineViews.size(); j++){
				View child = lineViews.get(j);
				//判断child的显示状态
				if(child.getVisibility() == View.GONE){
					continue;
				}
				lp = (MarginLayoutParams)child.getLayoutParams();
				
				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();
				child.layout(lc, tc, rc, bc);
				
				left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			}
			left = getPaddingLeft() ;
			top += lineHeight;
			
		}
		
		
		
		
	}
	
	
	/**
	 * 返回当前ViewGroup相关联的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		
		return new MarginLayoutParams(getContext(), attrs);
	}

}
