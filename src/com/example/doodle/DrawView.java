package com.example.doodle;
import java.util.LinkedList;
import java.util.Queue;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	// drawing path
	private Path drawPath;
	// drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	// initial color
	private int paintColor = 0xFF660000;
	// canvas
	private Canvas drawCanvas;
	// canvas bitmap
	private Bitmap canvasBitmap;
	private Queue<byte[]>buffer;
	byte []pre;
   private int height;
	

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		buffer = new LinkedList<byte[]>();
		pre=new byte[2];
		setupDrawing();
	}

	private void setupDrawing() {
		// get drawing area setup for interaction
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float touchX = event.getX();
		float touchY = event.getY();
		float temp=touchY-height;
		float xx=((touchX/this.getWidth())*33);
		float yy=((temp/this.getHeight())*33);
		byte[]elem=new byte[2];
		elem[0]=(byte)xx;
		elem[1]=(byte)yy;
		if(buffer.size()>0){
		if(pre[0]!=elem[0]||pre[1]!=elem[1]){
			buffer.add(elem);	
			pre[0]=elem[0];
			pre[1]=elem[1];
		}
		}else{
			buffer.add(elem);
			pre[0]=elem[0];
			pre[1]=elem[1];
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_UP:
			drawCanvas.drawPath(drawPath, drawPaint);
			drawPath.reset();
			byte[]elem2=new byte[2];
			elem2[0]=(byte)255;
			elem2[1]=(byte)255;
			buffer.add(elem2);
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}

	public void startNew() {
		drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
	}
	public void setStartY(int y){
		height=y;
	}
	public Queue<byte[]> getBuffer(){
		return buffer;
	}
	public byte[] pop(){
		return buffer.poll();
	}

}
