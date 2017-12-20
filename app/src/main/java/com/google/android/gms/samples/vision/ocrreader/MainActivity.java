package com.google.android.gms.samples.vision.ocrreader;

/**
 * Created by aditshah on 29/10/17.
 */

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.CameraSource;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.CameraSourcePreview;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.HeadlinesFragments;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends FragmentActivity
        implements HeadlinesFragments.OnHeadlineSelectedListener {

    private TextToSpeech tts;
    private String from = "";
    private String to = "";
    private String data ="";
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("OnInitListener", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("OnInitListener", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);

        final EditText edittext= (EditText) findViewById(R.id.editText);
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to = "en";
                tts.setLanguage(Locale.US);
                action(edittext.getText().toString().replaceAll(" ", "%20"));
            }
        });


        final Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to = "de";
                tts.setLanguage(Locale.US);
                action(edittext.getText().toString().replaceAll(" ", "%20"));
            }
        });


        final Button button2 = (Button) findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="es";
                tts.setLanguage(Locale.US);
                action(edittext.getText().toString().replaceAll(" ", "%20"));
            }
        });

        final Button button3 = (Button) findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="zh-CN";
                Locale myLocale = Locale.CHINESE;
                tts.setLanguage(myLocale);
                action(edittext.getText().toString().replaceAll(" ", "%20"));
            }
        });

        final Button button4 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="hi";
                tts.setLanguage(new Locale("hin-IND"));
                action(edittext.getText().toString().replaceAll(" ", "%20"));
            }
        });

    }

    public void onArticleSelected(int position) {
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        Intent intent2 = new Intent(this, HelpActivity.class);
        if(position == 0){
            startActivity(intent);
        }
        if(position == 1){
            startActivity(intent2);
        }
    }
    private void action(final String s) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(from.equals("")){
            if (networkInfo != null && networkInfo.isConnected()) {// fetch data
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new MainActivity.HttpAsyncTaskDetect().execute(s);
                    }
                });

            } else {// display error

            }
        }else {
            if (networkInfo != null && networkInfo.isConnected()) {// fetch data
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new MainActivity.HttpAsyncTask().execute(s);
                    }
                });

            } else {// display error

            }
        }
    }
    private class HttpAsyncTaskDetect extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            String line;
            try {
                //https://translation.googleapis.com/language/translate/v2/detect?q=hi%20how%20are%20you?&key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8
                //https:/translation.googleapis.com/language/translate/v2?source=en&q=hi&target=hi&key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8
                StringBuilder buildurl = new StringBuilder();
                data = args[0];
                buildurl.append("https://translation.googleapis.com/language/translate/v2/detect?").append("&q=").append(args[0].replaceAll("\n","%20")).append("&").append("key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8");
                URL url = new URL(buildurl.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setConnectTimeout(3000);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                urlConnection.disconnect();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONObject temp = jsonObj.getJSONObject("data");
                JSONObject weather = temp.getJSONArray("detections").getJSONArray(0).getJSONObject(0);
                final String icon = weather.getString("language");
                from = icon;
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new MainActivity.HttpAsyncTask().execute(data);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            String line;
            try {
                //https://translation.googleapis.com/language/translate/v2/detect?q=hi%20how%20are%20you?&key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8
                //https:/translation.googleapis.com/language/translate/v2?source=en&q=hi&target=hi&key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8
                StringBuilder buildurl = new StringBuilder();
                buildurl.append("https://translation.googleapis.com/language/translate/v2?").append("source=").append(from).append("&q=").append(args[0].replaceAll("\n","%20")).append("&target=").append(to).append("&").append("key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8");
                URL url = new URL(buildurl.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setConnectTimeout(3000);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                urlConnection.disconnect();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONObject temp = jsonObj.getJSONObject("data");
                JSONObject weather = temp.getJSONArray("translations").getJSONObject(0);
                final String icon = weather.getString("translatedText");
                Context context = findViewById(android.R.id.content).getContext();

                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(icon);
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
                tts.speak(icon, TextToSpeech.QUEUE_ADD, map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
