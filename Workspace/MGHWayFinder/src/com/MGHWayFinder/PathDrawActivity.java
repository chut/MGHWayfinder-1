package com.MGHWayFinder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class PathDrawActivity extends Activity implements OnTouchListener{
	PathView pv;
	Bundle bundle;
	ArrayList<Integer> xPoints = new ArrayList<Integer>();
	ArrayList<Integer> yPoints = new ArrayList<Integer>();
	int sWidth, sHeight, floor;
	String delim;
	
	Matrix m = new Matrix();
	Matrix savedM = new Matrix();
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int MODE = NONE;
	Point sPoint = new Point();
	Rect imageBounds;
	static final float offset = 10f;
	static final float minX = 0f - offset;
	static final float minY = 0f - offset;
	float maxX, maxY, tranX, tranY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
        pv = (PathView)findViewById(R.id.pathView);
        
		updateBundle();
		AssetManager am = getAssets();
		
		pv.updatePathView(xPoints, yPoints, floor, am);
		
		pv.setBackgroundColor(Color.WHITE);
		
		pv.setOnTouchListener(this);
	
	 }
	
	@Override
	public void onPause(){
		super.onPause();
		Toast.makeText(getApplicationContext(), "PAUSED", 1000).show();
		pv.recycleImage();
		System.gc();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Toast.makeText(getApplicationContext(), "RESUMED", 1000).show();
	}

	protected void updateBundle(){
		bundle = getIntent().getExtras();													//get info passed from starting intent
		
		floor = bundle.getInt("floor");														//working floor number
		delim = bundle.getString("delim");													//delimiter used to concat string of values

		for(String it:bundle.getString("xString").split(delim)){							//rebuilding arrays
			xPoints.add(Integer.parseInt(it));
		}
		for(String it:bundle.getString("yString").split(delim)){
			yPoints.add(Integer.parseInt(it));
		}
	}
	
	public boolean onTouch(View v, MotionEvent e) {
		PathView view = (PathView) v;
		
		switch(e.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN:
				savedM.set(m);
				sPoint.set((int)e.getX(), (int)e.getY());
				MODE = DRAG;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				MODE = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (MODE == DRAG) {
					m.set(savedM);
					m.postTranslate(e.getX() - sPoint.x, e.getY() - sPoint.y);
				}
				break;
		}
		
		
		view.setMatrix(m);
		
		return true;
	}
	
	
}
