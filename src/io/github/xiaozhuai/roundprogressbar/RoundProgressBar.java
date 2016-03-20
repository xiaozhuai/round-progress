package io.github.xiaozhuai.roundprogressbar;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;

import io.github.xiaozhuai.roundprogressbar.R;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 * 
 * @author xiaanming
 * 
 */
public class RoundProgressBar extends View implements AnimatorListener, AnimatorUpdateListener{
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 圆环背景色
	 */
	private int roundBackgroundColor;

	/**
	 * 圆环进度前景色
	 */
	private int roundForegroundColor;


	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 进度
	 */
	private float percent;

	private float zeroPointRoundSize;
	private int zeroPointRoundColor;
	
	private Float tmpPercent = null;
	
	private boolean isAnimating = false;
	private static final long minDur = 600; //动画的最短时间
	private static final long maxDur = 1000; //动画的最长时间

	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);

		// 获取自定义属性和默认值
		roundBackgroundColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundBackgroundColor, Color.RED);
		roundForegroundColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundForegroundColor, Color.GREEN);
		roundWidth = mTypedArray.getDimension(
				R.styleable.RoundProgressBar_roundWidth, 5);
		percent = mTypedArray.getFloat(
				R.styleable.RoundProgressBar_percent, 0f);
		zeroPointRoundColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_zeroPointRoundColor, Color.WHITE);
		zeroPointRoundSize = mTypedArray.getDimension(
				R.styleable.RoundProgressBar_zeroPointRoundSize, 0);
		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		
		/**
		 * 画最外层的大圆环
		 */
		paint.setColor(roundBackgroundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(getWidth() / 2, // 圆心x
				getWidth() / 2, // 圆心y
				getWidth() / 2 - roundWidth / 2, // 半径
				paint
			); // 画出圆环

		


		
		if(percent==0){
			/**
			 * 画进度为0时的进度圆点
			 */
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(roundForegroundColor); // 设置圆环的颜色
			paint.setAntiAlias(true); // 消除锯齿
			canvas.drawCircle(
					getWidth() / 2, // x
					getRoundWidth() / 2, // y
					getRoundWidth() / 2, // 半径
					paint
				); // 画出圆环
		}else{
			/**
			 * 画进度百分比固定的0点半圆形进度
			 */
			paint.setColor(roundForegroundColor);
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(getRoundWidth() / 2);
			RectF oval0 = new RectF(
					getWidth() / 2 - getRoundWidth() / 2 + getRoundWidth() / 4,
					0 + getRoundWidth() / 4,
					getWidth() / 2 + getRoundWidth() / 2 - getRoundWidth() / 4,
					getRoundWidth() - getRoundWidth() / 4
				); // 用于定义的圆弧的形状和大小的界限

			
			canvas.drawArc(oval0, 80, 200, false, paint);
			
			
			/**
			 * 画圆弧 ，画圆环的进度
			 */

			paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
			paint.setColor(roundForegroundColor); // 设置进度的颜色
			RectF oval1 = new RectF(
					roundWidth / 2,
					roundWidth / 2,
					getWidth() - roundWidth / 2,
					getWidth() - roundWidth / 2
				); // 用于定义的圆弧的形状和大小的界限

			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval1, -90, 360 * percent, false, paint);
			
			/**
			 * 画进度百分比终点圆形进度  这里用圆形 ！！！
			 */
			paint.setColor(roundForegroundColor);
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);

			
			double sita = Math.PI * 2d * (double)(percent) - Math.PI / 2;
			float tmpx = (float) (Math.cos(sita) * ( getWidth() / 2 - getRoundWidth() / 2 )) + getWidth() / 2;
			float tmpy = (float) (Math.sin(sita) * ( getWidth() / 2 - getRoundWidth() / 2 )) + getWidth() / 2;
			Log.e("ccccccc", tmpx+" --- "+tmpy);
			canvas.drawCircle(
					tmpx, // x
					tmpy, // y
					getRoundWidth() / 2, // 半径
					paint
				); // 画出圆环
			
		}
		
		

		
		
		/**
		 * 画0点的白色点
		 */
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(zeroPointRoundColor); // 设置圆环的颜色
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(
				getWidth() / 2, // x
				getRoundWidth() / 2, // y
				getZeroPointRoundSize() / 2, // 半径
				paint
			); // 画出圆环

	}
	
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//animationToNow();
	}

	public void animationToNow(){
		animationToPercent(getPercent());
	}
	
	
	public void animationToPercent(float f){
		if(!isAnimating){
			isAnimating = true;
			ValueAnimator ani = new ValueAnimator();
			ani.setFloatValues(0,f);
			
			//计算动画实际时间,根据percent，使动画时长在minDur和maxDur之间线性变化
			long dur = minDur + (long)((maxDur - minDur) * percent);
			
			ani.setDuration(dur);
			//系统自带的先加速后减速插值器使用余弦函数的方法
			//此方法不符合要求，重新写一个先加后减的插值器，使用多次方根和开放的方法和余弦结合的方法
			//ani.setInterpolator(new AccelerateDecelerateInterpolator());
			ani.setInterpolator(new TimeInterpolator() {
				@Override
				public float getInterpolation(float input) {
					float f = input;
					f = (float)(Math.cos((f + 1) * Math.PI) / 2.0f) + 0.5f;
					//方根大小，越大，则加速和加速的效果越明显，越小则越接近匀速
					//大于1的时候加减速变明显
					//小于1的时候加减速变不明显
					//等于1的时候为AccelerateDecelerateInterpolator同样的效果
					//等于0的时候为匀速
					//不能小于0
					float fanggen = 1.8f;
					if(f<1f/2f){
						f = f * 2f;
						f = (float) Math.pow(f, fanggen);
						f = f / 2f;
					}else if(f==1f/2f){
						// do noting
					}else{
						f = f * 2f - 1;
						f = (float) Math.pow(f, 1f / fanggen);
						f = (f + 1) / 2f;
					}
					return f;
				}
			});
			ani.addListener(this);
			ani.addUpdateListener(this);
			ani.start();
		}
	}
	
	//动画进度
	@Override
	public void onAnimationUpdate(ValueAnimator ani) {
		setPercentImmediately((Float)ani.getAnimatedValue());
	}
	
	// 动画开始
	@Override
	public void onAnimationStart(Animator animator) {
		
	}
	
	// 动画重复开始
	@Override
	public void onAnimationRepeat(Animator animator) {
		
	}
	
	// 动画结束
	@Override
	public void onAnimationEnd(Animator animator) {
		isAnimating = false;
		if(tmpPercent!=null){
			setPercentImmediately(tmpPercent);
		}
		tmpPercent = null;
	}
	
	// 动画中途终止
	@Override
	public void onAnimationCancel(Animator animator) {
		isAnimating = false;
		if(tmpPercent!=null){
			setPercentImmediately(tmpPercent);
		}
		tmpPercent = null;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized float getPercent() {
		return percent;
	}

	/**
	 * 设置进度
	 * 
	 * @param percent
	 */
	public synchronized void setPercent(float p) {
		if(isAnimating){
			//Log.e("ddddddd","111");
			tmpPercent = p;
		}else{
			//Log.e("ddddddd","222");
			setPercentImmediately(p);
			postInvalidate();
		}
	}
	
	/**
	 * 设置进度,立即设置，不判断动画状态，用于内部调用
	 * 
	 * @param percent
	 */
	private synchronized void setPercentImmediately(float p) {
		float per;
		if (percent < 0f) {
			per = 0;
		}else if (percent > 1f) {
			per = 1f;
		}else{
			per = p;
		}
		tmpPercent = null;
		percent = per;
		postInvalidate();
	}

	public int getCricleColor() {
		return roundBackgroundColor;
	}

	public void setCricleColor(int c) {
		this.roundBackgroundColor = c;
	}

	public int getCricleProgressColor() {
		return roundForegroundColor;
	}

	public void setCricleProgressColor(int c) {
		this.roundForegroundColor = c;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float s) {
		this.roundWidth = s;
	}

	public float getZeroPointRoundSize() {
		return zeroPointRoundSize;
	}

	public void setZeroPointRoundSize(float s) {
		this.zeroPointRoundSize = s;
	}

	public int getZeroPointRoundColor() {
		return zeroPointRoundColor;
	}

	public void setzeroPointRoundColor(int c) {
		this.zeroPointRoundColor = c;
	}
	
	public int getRoundForegroundColor() {
		return roundForegroundColor;
	}

	public void setRoundForegroundColor(int c) {
		this.roundForegroundColor = c;
	}
	public int getRoundBackgroundColor() {
		return roundBackgroundColor;
	}

	public void setRoundBackgroundColor(int c) {
		this.roundBackgroundColor = c;
	}


}