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
	 * �������е���View
	 * ��������λ
	 */
	private List<List<View>> mAllChildView = new ArrayList<List<View>>();
	//�������е��и�
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
	 * �����ڲ���view�Ŀ�͸�
	 * ���������͸�
	 * 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//ת���ÿؼ��Ŀ������
		//��ÿ����ֵ�Ͳ���ģʽ
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modelWidth = MeasureSpec.getMode(widthMeasureSpec);
		//��øߵ���ֵ�Ͳ���ģʽ
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modelHeight = MeasureSpec.getMode(heightMeasureSpec);
		
		
		//ע��,�����������ģʽΪfill_parent�� 100dp��match_parent
		//���ֲ����Ż�ΪEXACTLY,��sizeWidth��sizeHeight��ȷ��ֵ
		//��ģʽΪwarp_contentʱ,��Ҫ����ͨ�����������ӿؼ���ռ�Ŀռ����ȷ��
		
		//����������
		int width = 0;
		int height = 0;
		
		
		//��¼ÿһ�еĸ߶�
		int lineWidth = 0;
		int lineHeight = 0;
		
		//��ȡ�ӿؼ�����
		int childCount = getChildCount();
		
		for(int i = 0;i<childCount;i++){
			View childView = getChildAt(i);
			//������View�Ŀ�͸�
			//�÷�����֪ͨ��view���ݸ��ؼ���ģʽ������Ĳ�������
			//������Ŀ�߽��в���
			//������View����ȷ���Ŀ��
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
			//�õ�LayoutParams
			//��VIew��LayoutParams���丸��������
			//����FLowLayout�󶨵���MarginLayoutParams
			MarginLayoutParams lp = (MarginLayoutParams)childView.getLayoutParams();
			
			//��Viewռ�ݵĿ��
			//������+��߾�+�ұ߾�
			int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			//�߶�ͬ��
			int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			
			//����
			if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
				//�Ƚϻ�������
				width = Math.max(width, lineWidth);
				lineWidth = childWidth;
				
				height += lineHeight;
				lineHeight = childHeight;
			}else{
				//������
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
		
		//����ģʽ�ж�ʹ�ú��ֿ��
		setMeasuredDimension(
				modelWidth == MeasureSpec.EXACTLY ? sizeWidth:width + getPaddingLeft() + getPaddingRight(), 
				modelHeight == MeasureSpec.EXACTLY ? sizeHeight:height + getPaddingBottom() + getPaddingTop());
		
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * �����ӿؼ���λ��
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		
		mAllChildView.clear();
		mLinesHeight.clear();
		//��ǰ�ؼ��Ŀ��
		//��ʱ��ǰ�ؼ��Ѿ���������
		//����getWidth��ֵ
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
			
			//�����Ҫ����
			if((childWidth + lineWidth + lp.leftMargin + lp.rightMargin) > width - getPaddingLeft() - getPaddingRight()){
				 //��¼�и�
				mLinesHeight.add(lineHeight);
				//��¼��ǰ�е�View
				mAllChildView.add(lineViews);
				
				//�����п���и�
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				lineViews = new ArrayList<View>();
			}
			lineWidth +=childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(childHeight + lp.topMargin + lp.bottomMargin, lineHeight);
			lineViews.add(childView);
		}
		//�������һ��
		mAllChildView.add(lineViews);
		mLinesHeight.add(lineHeight);
		
		//������Viewλ��
		int top = getPaddingLeft();
		int left = getPaddingTop();
		
		//����
		int lineNum = mAllChildView.size();
		for(int i = 0; i<lineNum; i++){
			//��ǰ�����е�view
			lineViews = mAllChildView.get(i);
			lineHeight = mLinesHeight.get(i);
			//���ȡ������
			for(int j = 0; j<lineViews.size(); j++){
				View child = lineViews.get(j);
				//�ж�child����ʾ״̬
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
	 * ���ص�ǰViewGroup�������LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		
		return new MarginLayoutParams(getContext(), attrs);
	}

}
