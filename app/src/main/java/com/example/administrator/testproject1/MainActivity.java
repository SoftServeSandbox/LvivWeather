package com.example.administrator.testproject1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtViewDescription;
    TextView tempMin;
    TextView tempMax;
    Button showWeather;
    ImageView weatherIcon;
    ImageView weatherIcon1;
    ImageView weatherIcon2;
    ImageView weatherIcon3;
    WeatherData weatherData;

    ProgressBar progressBar;
    RelativeLayout layoutContent;

    private static final String IS_WEATHER_LOADED = "IS_WEATHER_LOADED";
    private boolean mIsWeatherLoaded = false;

    String strTmp;
    JSONObject jsonObj;

    ArrayList<Bitmap> bitmapArray;
    ArrayList<ImageView> weatherIcoArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        weatherIcon1 = (ImageView) findViewById(R.id.weather_icon1);
        weatherIcon2 = (ImageView) findViewById(R.id.weather_icon2);
        weatherIcon3 = (ImageView) findViewById(R.id.weather_icon3);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layoutContent = (RelativeLayout) findViewById(R.id.content);

        txtViewDescription = (TextView) findViewById(R.id.description);
        tempMin = (TextView) findViewById(R.id.temperature_min);
        tempMax = (TextView) findViewById(R.id.temperature_max);

        weatherData = WeatherData.getInstance();

        //weatherIcoArray.add(weatherIcon);
       // weatherIcoArray.add(weatherIcon1);
        //weatherIcoArray.add(weatherIcon2);
        //weatherIcoArray.add(weatherIcon3);

        showWeather = (Button) findViewById(R.id.showWeather);
        showWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WeatherClass().execute();
            }
        });

        mIsWeatherLoaded = savedInstanceState != null && savedInstanceState.getBoolean(IS_WEATHER_LOADED);
        if (!mIsWeatherLoaded) {
            WeatherClass weatherLoadingTask = new WeatherClass();
            weatherLoadingTask.execute();
        } else {
            txtViewDescription.setText(weatherData.getInstance().txtViewDescription);
            tempMin.setText(weatherData.getInstance().tempMin);
            tempMax.setText(weatherData.getInstance().tempMax);
            weatherIcon.setImageBitmap(weatherData.getInstance().icon);

            layoutContent.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_WEATHER_LOADED, mIsWeatherLoaded);
        super.onSaveInstanceState(outState);
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
            //Integer intPos = Integer.parseInt(urls[1]);
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
            //bitmapArray.add(result);
            weatherIcon.setImageBitmap(result);
            weatherData.SetIcon(result);
            mIsWeatherLoaded = true;
            //for (int i=1; i < bitmapArray.size(); i++) {
            //    weatherIcoArray.get(i).setImageBitmap(bitmapArray.get(i));
           // }

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
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                layoutContent.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray jsArr = jsonObject.getJSONArray("weather");


                txtViewDescription.setText("Погодні умови: " + jsArr.getJSONObject(0).getString("description"));
                weatherData.SetTxtViewDescription("Погодні умови: " + jsArr.getJSONObject(0).getString("description"));


                tempMin.setText("Мінімальна температура: " + jsonObject.getJSONObject("main").getInt("temp_min"));
                weatherData.SetTempMin("Мінімальна температура: " + jsonObject.getJSONObject("main").getInt("temp_min"));


                tempMax.setText("Максимальна температура: " + jsonObject.getJSONObject("main").getInt("temp_max"));
                weatherData.SetTempMax("Максимальна температура: " + jsonObject.getJSONObject("main").getInt("temp_max"));

                new DownloadImageTask().execute("http://openweathermap.org/img/w/" + jsArr.getJSONObject(0).getString("icon") + ".png", "0");

                //new DownloadImageTask().execute("http://openweathermap.org/img/w/" + "0" + "1" + "d" + ".png", "1");
                //for(int i=1; i <= 4; i++)
                //    new DownloadImageTask().execute("http://openweathermap.org/img/w/" + "0" + i + "d" + ".png");

                //Thread.sleep(2000);

                layoutContent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}


