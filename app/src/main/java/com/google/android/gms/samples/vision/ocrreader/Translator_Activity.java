package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


public class Translator_Activity extends Activity {

    private TextToSpeech tts;
    private String from = "";
    private String to = "";
    private String data ="";
    private String previous = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator_);
        Intent intent = getIntent();
        final String message = intent.getStringExtra(OcrCaptureActivity.EXTRA_MESSAGE);

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

        // Capture the layout's TextView and set the string as its text
        final EditText edittext= (EditText) findViewById(R.id.editText);
        edittext.setText(message);

        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to = "en";
                data = edittext.getText().toString();
                tts.setLanguage(Locale.US);
                data.replaceAll(" ", "%20");
                action(data);
            }
        });


        final Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to = "de";
                data = edittext.getText().toString();
                tts.setLanguage(Locale.US);
                data.replaceAll(" ", "%20");
                action(data);
            }
        });


        final Button button2 = (Button) findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="es";
                data = edittext.getText().toString();
                tts.setLanguage(Locale.US);
                data.replaceAll(" ", "%20");
                action(data);
            }
        });

        final Button button3 = (Button) findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="zh-CN";
                data = edittext.getText().toString();
                data.replaceAll(" ", "%20");
                Locale myLocale = Locale.CHINESE;
                tts.setLanguage(myLocale);
                action(data);
            }
        });

        final Button button4 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to ="hi";
                data = edittext.getText().toString();
                data.replaceAll(" ", "%20");
                tts.setLanguage(new Locale("hin-IND"));
                action(data);
            }
        });
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
                        new HttpAsyncTaskDetect().execute(s);
                    }
                });

            } else {// display error

            }
        }else if(!from.equals(to)) {
            if (networkInfo != null && networkInfo.isConnected()) {// fetch data
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new HttpAsyncTask().execute(s);
                    }
                });

            } else {// display error

            }
        }else{
            return;
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
                data.replaceAll(" ", "%20");
                buildurl.append("https://translation.googleapis.com/language/translate/v2/detect?").append("&q=").append(args[0].replaceAll(" ", "%20").replaceAll(" ","%20").replaceAll("\n","%20")).append("&").append("key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8");
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
                if(!from.equals(to)) {
                    Handler mHandler = new Handler(getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            data.replaceAll(" ", "%20");
                            new HttpAsyncTask().execute(data);
                        }
                    });
                }else{
                    return;
                }
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
                buildurl.append("https://translation.googleapis.com/language/translate/v2?").append("source=").append(from).append("&q=").append(args[0].replaceAll(" ","%20").replaceAll("\n","%20")).append("&target=").append(to).append("&").append("key=AIzaSyClcFkzl_nDwrnCcwAruIz99WInWc0oRg8");
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
                tts.setLanguage(Locale.US);
                tts.speak(icon, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
