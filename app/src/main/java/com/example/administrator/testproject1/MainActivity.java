package com.example.administrator.testproject1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView txtViewDescription;
    TextView tempMin;
    TextView tempMax;
    Button showWeather;
    ImageView weatherIcon;

    String strTmp;
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        showWeather = (Button) findViewById(R.id.showWeather);
        showWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WeatherClass().execute();
            }
        });
        new WeatherClass().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            weatherIcon.setImageBitmap(result);
        }
    }

    class WeatherClass extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                InputStream wheatherURLStream = new URL("http://api.openweathermap.org/data/2.5/weather?q=Lviv,ua&units=metric&lang=ua").openStream();

                InputStreamReader is = new InputStreamReader(wheatherURLStream);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();

                while (read != null) {
                    //System.out.println(read);
                    sb.append(read);
                    read = br.readLine();

                }

                strTmp = sb.toString();
                Log.d("MyTag", strTmp);

                jsonObj = new JSONObject(strTmp);
                return jsonObj;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray jsArr = jsonObject.getJSONArray("weather");

                txtViewDescription = (TextView) findViewById(R.id.description);
                txtViewDescription.setText("Погодні умови: " + jsArr.getJSONObject(0).getString("description"));

                tempMin = (TextView) findViewById(R.id.temperature_min);
                tempMin.setText("Мінімальна температура: " + jsonObject.getJSONObject("main").getInt("temp_min"));

                tempMax = (TextView) findViewById(R.id.temperature_max);
                tempMax.setText("Максимальна температура: " + jsonObject.getJSONObject("main").getInt("temp_max"));

                new DownloadImageTask().execute("http://openweathermap.org/img/w/" + jsArr.getJSONObject(0).getString("icon") + ".png");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


