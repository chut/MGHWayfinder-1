package com.DrawProto;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class DrawProtoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         
    	 WebView wv = (WebView)findViewById(R.id.wv);
    	 
    	 //String url = "<img src = \"floor1.jpg\" />";
         
    	 wv.loadUrl("file:///android_asset/floor1.png");
        // wv.loadDataWithBaseURL("file:///android_asset/", url, "text/html", "utf-8", "");
         
    }
}