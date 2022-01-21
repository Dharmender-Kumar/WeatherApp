package com.deku.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView;

    public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;


            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!=-1){
                    char curr=(char)data;
                    result.append(curr);
                    data=reader.read();
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String message="";
                String weatherInfo = jsonObject.getString("weather");

                JSONArray wArr=new JSONArray(weatherInfo);

                for(int i=0;i<wArr.length();i++){
                    JSONObject jsonPart=wArr.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message+=main+": "+description+"\r\n";
                    }

                }
                   JSONObject js=jsonObject.getJSONObject("main");
                    String temp=js.getString("temp");
                    double t=Double.parseDouble(temp);
                    t-=273.15;
                    temp=Double.toString(Double.parseDouble(String.format("%.3f",t)));
                    String pressure=js.getString("pressure");
                    String temp_min=js.getString("temp_min");
                    String temp_max=js.getString("temp_max");
                      double mnt=Double.parseDouble(temp_min);
                      mnt-=273.15;
                      temp_min=Double.toString(Double.parseDouble(String.format("%.3f",mnt)));
                      double mxt=Double.parseDouble(temp_max);
                      mxt-=273.15;
                      temp_max=Double.toString(Double.parseDouble(String.format("%.3f",mxt)));
                    message+="Temperature: "+temp+" C\n";
                    message+="Pressure: "+pressure+" Pa\n";
                    message+="Minimum Temperature: "+temp_min+" C\n";
                    message+="Maximum Temperature: "+temp_max+" C\n";

                resultTextView.setText(message);
            }catch (Exception e){
                e.printStackTrace();
                resultTextView.setText("");
                Toast.makeText(MainActivity.this, "Please Enter Correct City Name", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText =findViewById(R.id.editText);
        resultTextView=findViewById(R.id.resultTextView);

    }

    public void getWeather(View view){

        DownloadTask task=new DownloadTask();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=f42ac2906f30282380877692f397fc58");

        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }
}