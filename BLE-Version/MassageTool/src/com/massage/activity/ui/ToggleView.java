package com.massage.activity.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * �Զ��忪��
 * @author poplar
 * 
 * Android �Ľ����������
 * ����			 �ڷ�		����
 * measure	->	layout	->	draw
 * 	  | 		  |			 |
 * onMeasure -> onLayout -> onDraw ��д��Щ����, ʵ���Զ���ؼ�
 * 
 * onResume()֮��ִ��
 * 
 * View
 * onMeasure() (�����������ָ���Լ��Ŀ���) -> onDraw() (�����Լ�������)
 * 
 * ViewGroup
 * onMeasure() (ָ���Լ��Ŀ���, ������View�Ŀ���)-> onLayout() (�ڷ�������View) -> onDraw() (��������)
 */
public class ToggleView extends View {

	private Bitmap switchBackgroupBitmap; // ����ͼƬ
	private Bitmap slideButtonBitmap; // ����ͼƬ
	private Paint paint; // ����
	private boolean mSwitchState = false; // ����״̬, Ĭ��false
	private float currentX;

	/**
	 * ���ڴ��봴���ؼ�
	 * @param context
	 */
	public ToggleView(Context context) {
		super(context);
		init();
	}

	/**
	 * ������xml��ʹ��, ��ָ���Զ�������
	 * @param context
	 * @param attrs
	 */
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// ��ȡ���õ��Զ�������
		String namespace = "http://schemas.android.com/apk/res/com.massage.activity";
		int switchBackgroundResource = attrs.getAttributeResourceValue(namespace , "switch_background", -1);
		int slideButtonResource = attrs.getAttributeResourceValue(namespace , "slide_button", -1);
		
		mSwitchState = attrs.getAttributeBooleanValue(namespace, "switch_state", false);
		setSwitchBackgroundResource(switchBackgroundResource);
		setSlideButtonResource(slideButtonResource);
	}

	/**
	 * ������xml��ʹ��, ��ָ���Զ�������, ���ָ������ʽ, ���ߴ˹��캯��
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		paint = new Paint();
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
	}

	// Canvas ����, ����. ���ϱ߻��Ƶ����ݶ�����ʾ��������.
	@Override
	protected void onDraw(Canvas canvas) {
		// 1. ���Ʊ���
		canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);
		
		// 2. ���ƻ���
		
		if(isTouchMode){
			// ���ݵ�ǰ�û���������λ�û�����
			
			// �û��������ƶ�����һ���С��λ��
			float newLeft = currentX - slideButtonBitmap.getWidth() / 2.0f;
			
			int maxLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
			
			// �޶����鷶Χ
			if(newLeft < 0){
				newLeft = 0; // ��߷�Χ
			}else if (newLeft > maxLeft) {
				newLeft = maxLeft; // �ұ߷�Χ
			}
			
			canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
		}else {
			// ���ݿ���״̬boolean, ֱ������ͼƬλ��
			if(mSwitchState){// ��
				int newLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
				canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
			}else {// ��
				canvas.drawBitmap(slideButtonBitmap, 0, 0, paint);
			}
		}
		
	}
	
	boolean isTouchMode = false;
	private OnSwitchStateUpdateListener onSwitchStateUpdateListener;
	// ��д�����¼�, ��Ӧ�û��Ĵ���.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchMode = true;
			System.out.println("event: ACTION_DOWN: " + event.getX());
			currentX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("event: ACTION_MOVE: " + event.getX());
			currentX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			isTouchMode = false;
			System.out.println("event: ACTION_UP: " + event.getX());
			currentX = event.getX();
			
			float center = switchBackgroupBitmap.getWidth() / 2.0f;
			
			// ���ݵ�ǰ���µ�λ��, �Ϳؼ����ĵ�λ�ý��бȽ�. 
			boolean state = currentX > center;
			
			// �������״̬�仯��, ֪ͨ����. ��߿���״̬������.
			if(state != mSwitchState && onSwitchStateUpdateListener != null){
				// �����µ�boolean, ״̬����ȥ��
				onSwitchStateUpdateListener.onStateUpdate(state);
			}
			
			mSwitchState = state;
			break;

		default:
			break;
		}

		// �ػ����
		invalidate(); // ������onDraw()������, ��ߵı�����������Ч.��������
		
		return true; // �������û��Ĵ����¼�, �ſ����յ��������¼�.
	}

	/**
	 * ���ñ���ͼ
	 * @param switchBackground
	 */
	public void setSwitchBackgroundResource(int switchBackground) {
		switchBackgroupBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	/**
	 * ���û���ͼƬ��Դ
	 * @param slideButton
	 */
	public void setSlideButtonResource(int slideButton) {
		slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}

	/**
	 * ���ÿ���״̬
	 * @param b
	 */
	public void setSwitchState(boolean mSwitchState) {
		this.mSwitchState = mSwitchState;
	}
	
	public interface OnSwitchStateUpdateListener{
		// ״̬�ص�, �ѵ�ǰ״̬����ȥ
		void onStateUpdate(boolean state);
	}

	public void setOnSwitchStateUpdateListener(
			OnSwitchStateUpdateListener onSwitchStateUpdateListener) {
				this.onSwitchStateUpdateListener = onSwitchStateUpdateListener;
	}

}