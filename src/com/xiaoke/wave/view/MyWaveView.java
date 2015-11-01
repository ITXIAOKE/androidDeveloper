package com.xiaoke.wave.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



public class MyWaveView extends View {
	//装载波浪的集合
	private List<Wave> waveList;
	//定义一个两个波浪中心点最小的间距,默认设为6个像素
	private final int DIS_WAVE_R=6;
	//判断是否刷新动画,默认不刷新
	private boolean isrunning=false;
	//wave的javabean
	private class Wave{
		//定义波浪的圆心的x和y左边
		public int waveX;
		public int waveY;
		//波浪的半径
		public int waveR;
		//画波浪的画笔
		public Paint waveP;
	}
	
	public MyWaveView(Context context) {
		super(context);
	}
	
	public MyWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//初始化一个集合,用于存储更多的圆环
		waveList=new ArrayList<Wave>();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	//创建handler
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//首先刷新数据
			flashData();
			//调用以下方法，导致onDraw方法，开始绘制圆环
			invalidate();
			//循环动画
			if (isrunning) {
				handler.sendEmptyMessageDelayed(0, 100);
			}
		}
	};
	//刷新数据
	private void flashData() {
		//遍历波浪的集合
		for (int i = 0; i < waveList.size(); i++) {
			//拿到每一个波浪
			Wave wave = waveList.get(i);
			//拿到画笔的透明度
			int alpha = wave.waveP.getAlpha();
			//随着圆环半径的增大，透明度随着依次减少6
			alpha-=6;
			if (alpha<6) {
				alpha=0;//0是完全透明
			}
			//当透明度是0是，就从波浪的集合中移除掉此波浪
			if (alpha==0) {
				waveList.remove(i);//删除i以后，i的值应该再减去1，否则会漏掉一个对象，在此处影响不大
				continue;
			}
			//画笔设置透明度
			wave.waveP.setAlpha(alpha);
			//半径依次加6
			wave.waveR+=6;
			//画笔圆环的厚度是半径的4分之一
			wave.waveP.setStrokeWidth(wave.waveR/4);
		}
		
		if (waveList.size()==0) {
			isrunning=false;//如果波浪集合里面被清空，就不在刷新动画
		}
		
	};
	

	@Override
	protected void onDraw(Canvas canvas) {
		//遍历波浪的集合
		for (int i = 0; i < waveList.size(); i++) {
			//拿到每一个波浪
			Wave wave = waveList.get(i);
			canvas.drawCircle(wave.waveX, wave.waveY, wave.waveR, wave.waveP);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			//不论是手指按下，还是手指在屏幕移动，获取改点x与y的坐标
			int x = (int) event.getX();
			int y = (int) event.getY();
			//把手指触摸屏幕的那个点作为新的波浪的中心点
			addPoint(x,y);
			break;
		}
		return true;//自己处理触摸事件
	}
	/**
	 * 添加新的波浪的中心点
	 * @param x
	 * @param y
	 */
	private void addPoint(int x, int y) {
		if (waveList.size()==0) {
			//添加新的波浪的中心点
			addPoint2List(x,y);
			/**
			 * 第一次启动动画，手指第一次在屏幕滑动
			 */
			isrunning=true;
			//给主线程发送消息
			handler.sendEmptyMessage(0);
		} else if(waveList.size()>0){
			//从波浪的集合中拿到最后一个波浪
			Wave wave = waveList.get(waveList.size()-1);
			//判断最后一个波浪的中心点与传进来的x或者y的值，进行比对，如果大于6，就将这个点添加到波浪的集合中
			if (Math.abs(wave.waveX-x)>DIS_WAVE_R||Math.abs(wave.waveY-y)>DIS_WAVE_R) {
				//添加新的波浪的中心点
				addPoint2List(x,y);
				isrunning=true;//刷新动画
				//handler.sendEmptyMessage(0);这句不能加，否则效果有所不同
			}
		}
		
	}
	/**
	 * 把波浪的中心点，加入波浪的集合中，随即就添加新的波浪
	 * @param x
	 * @param y
	 */
	private void addPoint2List(int x, int y) {
		//把波浪实例化
		Wave  wave=new Wave();
		wave.waveX=x;
		wave.waveY=y;
		//把画波浪的画笔实例化
		Paint paint=new Paint();
		paint.setAntiAlias(true);//打开抗锯齿
		paint.setStyle(Style.STROKE);//设置样式是圆环
		paint.setColor(color[(int) (Math.random()*6)]);//设置画笔的颜色为随机的5中颜色
		//把画笔添加到波浪中
		wave.waveP=paint;
		//把每个波浪添加到波浪的集合中
		waveList.add(wave);
	}
	
	//定义画笔的颜色
	private int color[] ={Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.BLACK,Color.DKGRAY};

}
