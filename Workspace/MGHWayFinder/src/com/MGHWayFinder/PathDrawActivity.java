package com.MGHWayFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class PathDrawActivity extends ListActivity implements OnTouchListener{
	PathView pv;
	Bundle bundle;
	ArrayList<Integer> xPoints = new ArrayList<Integer>();
	ArrayList<Integer> yPoints = new ArrayList<Integer>();
	int sWidth, sHeight, floor;
	AssetManager am;
	Button center;
	
	//PATH CALCULATION VARIABLES
	Hashtable<String, Node> localHash = MGHWayFinderActivity.masterHash;
	Dijkstra dijkstra;
	Node sNode, eNode, bNode;
	String startnID, endnID;
	ArrayList<Node> fullNodePath;
	ArrayList<Node> walkNodePath = new ArrayList<Node>();
	boolean multifloor;
	int bNodeIndex;
	
	//IMAGEVIEW - TOUCH EVENT VARIABLES
	Matrix m;
	Matrix savedM;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int MODE = NONE;
	Point sPoint = new Point();
	Rect imageBounds;
	
	TextView tvX, tvY;
	float[] mValues = new float[9];
	
	//ui things from calum
		Button next;
		Button prev;
		Button list;
		Button help;
		int index = 0;
		ArrayList<String> nodeList = new ArrayList<String>();
		private ArrayAdapter<Node> adapt;
		TabHost tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		Resources res = getResources();
		
        //tabs
        tabs=(TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec;
        
        //tab setup
        spec=tabs.newTabSpec("map");
        spec.setContent(R.id.pathTab);
        spec.setIndicator("Map Path", res.getDrawable(R.drawable.ic_tab_map));
        tabs.addTab(spec);
        
		//center = (Button)findViewById(R.id.buttonCenter);
		tvX = (TextView)findViewById(R.id.tvX);
		tvY = (TextView)findViewById(R.id.tvY);
		next = (Button)findViewById(R.id.btnNext);
		next.setBackgroundDrawable(res.getDrawable(R.drawable.smallright));
		prev = (Button)findViewById(R.id.btnPrev);
		prev.setBackgroundDrawable(res.getDrawable(R.drawable.smallleft));
		//list = (Button)findViewById(R.id.btnList);
		help = (Button)findViewById(R.id.btnHelp);
		
        pv = (PathView)findViewById(R.id.pathView);
        am = getAssets();
      
    //GET START AND END NODEID FROM BUNDLE
        bundle = getIntent().getExtras();
        startnID = bundle.getString("StartnID");
        endnID = bundle.getString("EndnID");
        
    //GET NODES FROM HASHTABLE
    	sNode = localHash.get(startnID);														
		eNode = localHash.get(endnID);
        
		floor = sNode.getNodeFloor();
		
		calcPath(sNode);
		
		fullNodePath = dijkstra.getPath(eNode);
		walkNodePath = stripIntermediateSteps(fullNodePath);
		
	//WHEN CALCULATING AN INTERFLOOR PATH, WE NEED TO BREAK IT UP INTO INDIVIDUAL FLOORS
        if(sNode.getNodeFloor() != eNode.getNodeFloor()){							

        	bNode = dijkstra.getBreakNode();										//SET BNODE TO THE FIRST NODE ON THE SECOND FLOOR OF TRAVEL (WE CAN GET AT IT'S PREDECESSOR VIA .getPreviousNode()
        	bNodeIndex = walkNodePath.indexOf(bNode.getPreviousNode());
        	
        	multifloor = true;
        	floor = sNode.getNodeFloor();
        	
        	for(int i = 0; i <= bNodeIndex; i++){
        		xPoints.add(walkNodePath.get(i).getX());
        		yPoints.add(walkNodePath.get(i).getY());
        	}
        	
        } else {

        	multifloor = false;
        	
        	for(Node it:walkNodePath){
        		xPoints.add(it.getX());
        		yPoints.add(it.getY());
        	}
        }
     
     //SETUP PATHVIEW OBJECT AND DISPLAY
		pv.makePathView(xPoints, yPoints, floor, am);
		pv.setBackgroundColor(Color.WHITE);
		pv.setOnTouchListener(this);

	//BUTTON LISTENERS
		next.setOnClickListener(
				new OnClickListener(){
					public void onClick(View v){
						index++;
						step();
					}
				}
		);	
		
		prev.setOnClickListener(
				new OnClickListener(){
					public void onClick(View v){
						index--;
						step();
					}
				}
		);	
		
		
		help.setOnClickListener(
				new OnClickListener(){
					public void onClick(View v){
						Uri uri = Uri.parse("tel:6177262000");
						Intent call = new Intent(Intent.ACTION_CALL,uri);
						startActivity(call);
					}
				}
		);
	
		Thread c1 = new Thread(centerOnLoad, "onCreate Centering Thread");
		c1.start();
		
        //tab setup
        spec=tabs.newTabSpec("List");
        spec.setContent(R.id.listTab);
        spec.setIndicator("List View", res.getDrawable(R.drawable.ic_tab_list));
        tabs.addTab(spec);
		//LIST VIEW TAB------------------------------------------------------------
        
        //create array list
        ArrayList<HashMap<String, String>> dirList = new ArrayList<HashMap<String, String>>();
       //populate
        for(int i = 0; i < walkNodePath.size(); i++){
        	Node tempNode = walkNodePath.get(i);
        	HashMap<String, String> temp = new HashMap<String, String>();
        	temp.put("title",tempNode.getNodeDepartment());
        	temp.put("floor", "Floor " + Integer.toString(tempNode.getNodeFloor()));
        	temp.put("nID", tempNode.getNodeID());
        	dirList.add(temp);
        }
       
        //the actual adapter
       SimpleAdapter custAdapter = new SimpleAdapter(this, dirList,R.layout.row,new String[] {"title", "floor", "nID"}, new int[] {R.id.toptext,R.id.bottomtext,R.id.nIDtext});
        setListAdapter(custAdapter);
        
        //set pictures
        
        
        
        
        //old list shit
//        adapt = new ArrayAdapter<Node> (this, android.R.layout.simple_list_item_1, walkNodePath);
//        setListAdapter(adapt);
        
	 }//end oncreate
	


	//CALCULATE ALL PATHS FROM START NODE
	protected void calcPath(Node start){
		if(dijkstra == null){
			dijkstra = new Dijkstra(start);
		} else{
			dijkstra.reset();
			dijkstra.restart(start);
		}
	}
	
	//HANDLES TOUCH EVENTS - TRANSLATING AND SCALING PATHVIEW OBJECT
	public boolean onTouch(View v, MotionEvent e) {
		PathView view = (PathView) v;
		m = view.matrix;
		savedM = view.savedMatrix;
		
		
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
					view.invalidate();
				}
				break;
		}
		
	//TESTING PURPOSES
		m.getValues(mValues);
		tvX.setText("X: " + Float.toString(mValues[Matrix.MTRANS_X]) + " ");
		tvY.setText("Y: " + Float.toString(mValues[Matrix.MTRANS_Y]));
		
		return true;
	}
	
	//STRIPS INTERMEDIATE NODES FROM A GIVEN ARRAYLIST WHERE Node(n) ANGLE == Node(n+1) ANGLE
	protected ArrayList<Node> stripIntermediateSteps(ArrayList<Node> listIn){
		ArrayList<Node> out = new ArrayList<Node>();
		Node currentNode, nextNode;
		double runningDist = 0;
		int i;
		
		currentNode = listIn.get(0);
		out.add(currentNode);																//INITIALIZE FIRST NODE
		
		for(i = 0; i < listIn.size()-1; i++){												//LOOP THROUGH UP TO SECOND TO LAST NODE, ONLY ADDING CHANGES IN DIRECTION
			currentNode = listIn.get(i);
			nextNode = listIn.get(i+1);
			
			runningDist += currentNode.getNNodeDistance();
			
			if(currentNode.getNNodeAngle() != nextNode.getNNodeAngle()) {
				currentNode.setStepDist(runningDist);
				out.add(nextNode);
				runningDist = 0;
			} 
		}
		
		return out;
		
	}
	
	public boolean step(){
		if(index < 0) {
			index = 0;
		} else if(index >= walkNodePath.size()) {
			index = walkNodePath.size()-1;
		}
		
		Node cNode = walkNodePath.get(index);
		int cNodeFloor = cNode.getNodeFloor();
		
		if(multifloor){
			if(index <= bNodeIndex && floor != cNodeFloor){
				xPoints.clear();
				yPoints.clear();
				for(int i = 0; i <= bNodeIndex; i++){
					xPoints.add(walkNodePath.get(i).getX());
					yPoints.add(walkNodePath.get(i).getY());
				}
				pv.updatePath(xPoints, yPoints, cNodeFloor);
				floor = cNodeFloor;
				pv.setCenterPoint(cNode);			
			} else if(index > bNodeIndex && floor != cNodeFloor){
				xPoints.clear();
				yPoints.clear();
				for(int i = bNodeIndex+1; i < walkNodePath.size(); i++){
					xPoints.add(walkNodePath.get(i).getX());
					yPoints.add(walkNodePath.get(i).getY());
				}
				pv.updatePath(xPoints, yPoints, cNodeFloor);
				floor = cNodeFloor;
				pv.setCenterPoint(cNode);
			} else if(floor == cNodeFloor){
				pv.setCenterPoint(cNode);	
			}
		} else {
			pv.setCenterPoint(cNode);
		}
		
		return true;
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.v("list pos", Integer.toString(position));
		index = position;
		step();
		tabs.setCurrentTab(0);
	}
	

///////////METHOD TO CENTER VIEW ON LOAD///////////////
	protected Handler handler = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
		     step();
		 }
	};
	
	Runnable centerOnLoad = new Runnable(){
   	public void run(){
   		try {
   				Thread.sleep(100);
   				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
   	}
   };
	
	
	
}
