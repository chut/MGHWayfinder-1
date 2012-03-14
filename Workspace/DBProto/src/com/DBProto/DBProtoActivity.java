package com.DBProto;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;

public class DBProtoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Cursor RSa, RSb, RSn;
        
        DBHelper db = new DBHelper(this);
        
        ArrayList<node> aFloor = new ArrayList<node>();
        node t;
        
        ArrayList<String> nStringArray = new ArrayList<String>();
        ArrayList<String[]> neighborArray = new ArrayList<String[]>();
        
        
        
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
    
        RSa = db.getFloorNodes(1);
        RSa.moveToFirst();
        
        while(!RSa.isAfterLast()){
        	t = new node(	RSa.getString(1),
        					RSa.getInt(2),
        					RSa.getInt(3),
        					RSa.getString(4),
        					RSa.getInt(5),
        					RSa.getString(6));
        	aFloor.add(t);
        	RSa.moveToNext();
        }
        
        for(node it: aFloor){
        	nStringArray.add(it.getNodeID());
        }
        
        RSn = db.getNodeNeighbors(nStringArray);
        RSn.moveToFirst();
        
        for(node it: aFloor){
        	it.getNodeID()
        }
        
        
    }
}