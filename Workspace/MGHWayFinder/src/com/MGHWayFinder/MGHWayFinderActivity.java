package com.MGHWayFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MGHWayFinderActivity extends Activity {
	
	public static Hashtable<String, Node> masterHash = new Hashtable<String,Node>();
	Dijkstra dijkstra;
	
	Spinner start, end;
	TextView tvPath;
	ImageView ivPath;
	Button go, drawPath;
	Button startQR;
	Button endSet;
	String startnID;
	String endnID;
	String contextNID[] = {"f1-sel", "f1-100s2", "f1-108_0", "f1-nr", "f1-100C1_3"};
	String sPath;
	ArrayAdapter<String> aAdapter;
	DBHelper db;
	ArrayList<String> validNodeIds;
    
    Button mapFirst;
    Button mapSec;
	ImageView viewMap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        db = new DBHelper(this.getApplicationContext());
        initializeDB();
        
        validNodeIds = db.getAllNids();
        
        //tabs
        TabHost tabs=(TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec;
        
        Resources res = getResources();

//////////////////DIRECTIONS TAB//////////////////////
        //tab setup
        spec=tabs.newTabSpec("directions");
        spec.setContent(R.id.directionsTab);
        spec.setIndicator("Navigation", res.getDrawable(R.drawable.ic_tab_navigate));
        tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
        start = (Spinner)findViewById(R.id.startSpin);
        end = (Spinner)findViewById(R.id.endSpin);
        go = (Button)findViewById(R.id.goButton);
        
        
        //tvPath = (TextView)findViewById(R.id.tvNP);	//dont need
        
        //drawPath = (Button)findViewById(R.id.btnMakePath); //dont need
        
        aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, validNodeIds);
        start.setAdapter(aAdapter);
        end.setAdapter(aAdapter);
        
        go.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		startPathDraw();
        	}}); 
        
        /*TODO DELETE
        drawPath.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//drawMapPath(1);
        	}}); */
        
        //logo comented out currently
        //ImageView logo = (ImageView)findViewById(R.id.logoView);
        
        //scan buttons
    	startQR = (Button)findViewById(R.id.scanStart);
    	startQR.setOnClickListener(new OnClickListener(){
    	    public void onClick(View v) {
    	    	Intent scanStart = new Intent("com.google.zxing.client.android.SCAN");   
    	        scanStart.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
    	        startActivityForResult(scanStart, 0);
    	    }});

    	//do we need an end button??
    	//no end scan, end context menu
    	//COMING SOON
    	endSet = (Button)findViewById(R.id.setEnd);
    	registerForContextMenu(endSet);	//how to make short press?
    	
    	//auto set end point FOR TESTING
    	end.setSelection(12);

        
//////////////////MAP TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("map");
spec.setContent(R.id.mapTab);
spec.setIndicator("Map", res.getDrawable(R.drawable.ic_tab_map));
tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
//stuff for map tab
		viewMap = (ImageView)findViewById(R.id.mapView);
		mapFirst = (Button)findViewById(R.id.btnMapFirst);
		mapSec = (Button)findViewById(R.id.btnMapSec);
		
        mapFirst.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		viewMap.setImageResource(R.drawable.floor1color);
        		//viewMap.setImageDrawable(Drawable.createFromPath("floor1color.png"));
        	}});
        
        mapSec.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		viewMap.setImageResource(R.drawable.floor2color);
        		//viewMap.setImageDrawable(Drawable.createFromPath("floor2color.png"));
        	}});
        


//////////////////DIRECTORY TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("directory");
spec.setContent(R.id.dirTab);
spec.setIndicator("Directory", res.getDrawable(R.drawable.ic_tab_directory));
tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
//stuff for directory tab



//////////////////HELP TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("help");
spec.setContent(R.id.helpTab);
spec.setIndicator("Help", res.getDrawable(R.drawable.ic_tab_help));
tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
//help tab stuff




    }//end of oncreate
    
    
    //context menu
@Override
public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {		
	super.onCreateContextMenu(menu, v, menuInfo);
	//add a couple of options to the context menu
	menu.setHeaderTitle("Patient Destinations");
	menu.add(0, 1, Menu.NONE, "Elevator");
	menu.add(0, 2, Menu.NONE, "Stairs");
	menu.add(0, 3, Menu.NONE, "Clinic Lobby");
	menu.add(0, 4, Menu.NONE, "Bathroom");
	menu.add(0, 5, Menu.NONE, "Hospital Exit");
	
}


@Override
public boolean onContextItemSelected(MenuItem item) {
	super.onContextItemSelected(item);
	String title = item.getTitle().toString(); //get menu item title
	int itemId = item.getItemId();             //get menu item id
	//Toast.makeText(this, title + " " + itemId, Toast.LENGTH_LONG).show();
	
	endnID = contextNID[itemId - 1];
	Log.v("context", endnID + title);
	
    //set spinner
	/*
	for(int i=0; i < aFloor.size(); i++){
		if(endnId.equals(aFloor.get(i).getNodeID())){
			end.setSelection(i);
		}}
		*/
	return false;
}
    
    
    
    //receive scan result back from scanner intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent scanStart) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String startnID = scanStart.getStringExtra("SCAN_RESULT");	//get the result from extra
                //test code
                Log.v("QR", startnID);
                
                
                //set spinner
            	for(int i=0; i < validNodeIds.size(); i++){
            		if(startnID.equals(validNodeIds.get(i))){
            			start.setSelection(i);
            		}
            	} 
            } else if (resultCode == Activity.RESULT_CANCELED) {
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    	
    private void startPathDraw() {
    	startnID = (String)start.getSelectedItem();
    	endnID = (String)end.getSelectedItem();
    	
    	int startFloor = db.getNodeFloor(startnID);
    	int endFloor = db.getNodeFloor(endnID);
    	
    	
    //BUILD NODE SET
    	if(startFloor != endFloor){																	//If start and end are on different floors, build master node set of both floors
    		masterHash.putAll(db.buildFloorNodes(startFloor));
    		masterHash.putAll(db.buildFloorNodes(endFloor));										
   
    		db.buildInterFloor(startFloor, endFloor, masterHash);									//Create interconnections between these two floors only
    	} else {
    		masterHash.putAll(db.buildFloorNodes(startFloor));
    	}
		
	//START PATHDRAWACTIVITY
		Intent drawPath = new Intent(this, PathDrawActivity.class);
		drawPath.putExtra("StartnID", startnID);
		drawPath.putExtra("EndnID", endnID);
        startActivity(drawPath);
    	
	}
    
    private synchronized void initializeDB(){
    	try { 
        	db.createDataBase();
        } 
        catch (IOException ioe) {
        	throw new Error("Unable to create database");
        }
 
        try {
        	db.openDataBase();
        } 
        catch(SQLException sqle){
        	throw sqle;
        }
    }
    
    
    /* TODO DELETE
    ///Called to start a new activity which draws the path over the image of the floor plan
    public void drawMapPath(int floor, ArrayList<Point> coords){
    	String x,y;
    	String delim = ",";
    	
    	x = Integer.toString(coords.get(0).x);
    	x = Integer.toString(aPath.get(0).getX());
    	y = Integer.toString(aPath.get(0).getY());
    	for(int i = 1; i < aPath.size(); i++){											//builds the strings to pass to the activity with delimiter delim
    		x += delim + Integer.toString(aPath.get(i).getX());
    		y += delim + Integer.toString(aPath.get(i).getY());
    	}
    	
    	Intent drawPath = new Intent(this, PathDrawActivity.class);
    	drawPath.putExtra("xString", x);
    	drawPath.putExtra("yString", y);
    	drawPath.putExtra("delim", delim);
    	drawPath.putExtra("floor", floor);
    	
        startActivity(drawPath);

    }
	*/
    
    public void contextDestination(){
    	//use this and onclick leading to it to create and open context menu with
    	//end destinations
    	Toast.makeText(this, "Context Menu", Toast.LENGTH_LONG).show();
    }
    
    
}//end of class
