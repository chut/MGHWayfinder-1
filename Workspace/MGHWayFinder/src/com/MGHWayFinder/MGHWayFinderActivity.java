package com.MGHWayFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MGHWayFinderActivity extends Activity {
    /** Called when the activity is first created. */
	//hello git
	Spinner start, end;
	TextView tvPath;
	ImageView ivPath;
	Button go, drawPath;
	Button startQR;
	Button endSet;
	String startnID;
	String endnId;
	String contextNID[] = {"f1-sel", "f1-100s2", "f1-108_0", "f1-nr", "f1-100C1_3"};
	Dijkstra dPath;
	String sPath;
	ArrayAdapter<Node> aAdapter;
	DBHelper db;
    ArrayList<Node> aFloor = new ArrayList<Node>();
    ArrayList<Node> aPath;
    Hashtable<String, Node> hash;
    //ArrayList<Node> bFloor = new ArrayList<Node>();
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        db = new DBHelper(this.getApplicationContext());
        initializeDB();
        hash = db.buildFloorNodes(1);
        for(Node n:hash.values())
        	aFloor.add(n);
        
        //tabs
        TabHost tabs=(TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec;
        
        

//////////////////DIRECTIONS TAB//////////////////////
        //tab setup
        spec=tabs.newTabSpec("directions");
        spec.setContent(R.id.directionsTab);
        spec.setIndicator("Directions");
        tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
        start = (Spinner)findViewById(R.id.startSpin);
        end = (Spinner)findViewById(R.id.endSpin);
        tvPath = (TextView)findViewById(R.id.tvNP);
        ivPath = (ImageView)findViewById(R.id.imageView);
        go = (Button)findViewById(R.id.goButton);
        drawPath = (Button)findViewById(R.id.btnMakePath);
        
        aAdapter = new ArrayAdapter<Node>(this, android.R.layout.simple_spinner_item, aFloor);
        start.setAdapter(aAdapter);
        end.setAdapter(aAdapter);
        
        go.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		calculatePath();
        	}}); 
        drawPath.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		drawMap();
        	}});
        
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

        
//////////////////MAP TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("map");
spec.setContent(R.id.mapTab);
spec.setIndicator("Map");
tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
//stuff for map tab




//////////////////DIRECTORY TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("directory");
spec.setContent(R.id.dirTab);
spec.setIndicator("Directory");
tabs.addTab(spec);
///////////////////UI ELEMENTS////////////////////////
//stuff for directory tab



//////////////////HELP TAB//////////////////////
//tab setup
spec=tabs.newTabSpec("help");
spec.setContent(R.id.helpTab);
spec.setIndicator("Help");
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
	
	endnId = contextNID[itemId - 1];
	Log.v("context", endnId + title);
	
    //set spinner
	for(int i=0; i < aFloor.size(); i++){
		if(endnId.equals(aFloor.get(i).getNodeID())){
			end.setSelection(i);
		}}
		
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
            	for(int i=0; i < aFloor.size(); i++){
            		if(startnID.equals(aFloor.get(i).getNodeID())){
            			start.setSelection(i);
            		}
            	} 
            } else if (resultCode == Activity.RESULT_CANCELED) {
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    	
    private void calculatePath() {
		dPath = new Dijkstra((Node)start.getSelectedItem());
		aPath = dPath.getPath((Node)end.getSelectedItem());

		sPath = aPath.get(0).getNodeID();
		
		for(int i = 1; i < aPath.size(); i++){
			sPath += " -> " + aPath.get(i).getNodeID();
		}
		tvPath.setText(sPath);
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
    
    public void drawMap(){
    	String x,y;
    	String delim = ",";
    	
    	x = Integer.toString(aPath.get(0).getX());
    	y = Integer.toString(aPath.get(0).getY());
    	for(int i = 1; i < aPath.size(); i++){
    		x += delim + Integer.toString(aPath.get(i).getX());
    		y += delim + Integer.toString(aPath.get(i).getY());
    	}
    	Intent drawPath = new Intent(this, pathDraw.class);
    	drawPath.putExtra("x", x);
    	drawPath.putExtra("y", y);
        startActivity(drawPath);
    	//PathView pv = new PathView(this.getApplicationContext(), aPath);
    	//pv.setBackgroundResource(R.drawable.basemap);
    	//setContentView(pv);
        
    }

    
    public void contextDestination(){
    	//use this and onclick leading to it to create and open context menu with
    	//end destinations
    	Toast.makeText(this, "Context Menu", Toast.LENGTH_LONG).show();
    }
    
    
}//end of class
