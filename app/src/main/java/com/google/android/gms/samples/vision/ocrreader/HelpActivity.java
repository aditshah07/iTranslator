package com.google.android.gms.samples.vision.ocrreader;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;


public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Help \n\n");
        String help = "1. You can use camera to capture text you want to translate. \n2. Click the text you want to translate. \n" +
                "3. Click the language you want to translate to.";
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(help);
    }

}
