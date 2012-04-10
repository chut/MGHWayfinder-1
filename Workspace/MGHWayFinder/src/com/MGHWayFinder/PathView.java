package com.MGHWayFinder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class PathView extends View{
	int x1, y1, x2, y2, i;
	Paint p = new Paint();
	ArrayList<Integer> xArray, yArray;
	Matrix scale = new Matrix();

	float nativeWidth, nativeHeight;
	Drawable baseMap, pathWay;
	Rect bounds;
	
	Bitmap pathBitmap;
	Canvas pathCanvas;
	Path path = new Path();

	
	public PathView(Context context, ArrayList<Integer> xArray, ArrayList<Integer> yArray, float width, float height) {
		super(context);
		p.setColor(Color.BLACK);
		p.setStrokeWidth(4);
		this.xArray = xArray;
		this.yArray = yArray;
		this.nativeHeight = height;
		this.nativeWidth = width;
		
		baseMap = getResources().getDrawable(R.drawable.basemap700);

		bounds = new Rect(0, 0, baseMap.getIntrinsicWidth(), baseMap.getIntrinsicHeight());
		baseMap.setBounds(bounds);

		//pathWay.setBounds(bounds);
		//drawCanvas();
	}
	
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.save();
		//pathBitmap = Bitmap.createBitmap(bounds.right, bounds.bottom, Bitmap.Config.ARGB_4444);
		//pathCanvas = new Canvas();
		//baseMap.draw(canvas);
		//pathCanvas.setMatrix(scale);
		scale.postScale((nativeWidth/(float)bounds.right), (nativeHeight/(float)bounds.bottom));
		canvas.setMatrix(scale);
		
		baseMap.draw(canvas);
		
		x1 = xArray.get(0);
		y1 = xArray.get(0);
		canvas.drawCircle(x1, y1, 5, p);
		
		for(i = 0; i < (xArray.size()-1); i++){
			x1 = xArray.get(i);
			y1 = yArray.get(i);
			x2 = xArray.get(i+1);
			y2 = yArray.get(i+1); 
					
			
			canvas.drawLine(x1, y1, x2, y2, p);
		}		
		canvas.drawCircle(x2, y2, 5, p);
		//canvas.save();
		//canvas.scale(2f,2f);
				
				
		
		//makePath();
		//path.transform(scale);
		//pathCanvas.drawPath(path, p);

		
		//canvas.restore();
	}
	
	private void makePath(){
		x1 = xArray.get(0);
		y1 = yArray.get(0);
		
		path.addCircle(x1, y1, 5, Path.Direction.CW);
		path.moveTo(x1, y1);
		
		for(i = 0; i < xArray.size(); i++){
			x1 = xArray.get(i);
			y1 = yArray.get(i);
			path.lineTo(x1, y1);
		}
		
		path.addCircle(x1, y1, 5, Path.Direction.CW);
	}
	
}
