package com.example.katecatlin.jsonpractice;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main extends Activity {
    TextView textView;
    TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_title);
        descriptionView = (TextView) findViewById(R.id.desciption);
        Button button = (Button) findViewById(R.id.main_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetJSONInfo jsonCaller = new GetJSONInfo();
                jsonCaller.execute();

            }
        });
    }

            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);

                textView = (TextView) rootView.findViewById(R.id.text_title);
                Button button = (Button) rootView.findViewById(R.id.main_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GetJSONInfo jsonCaller = new GetJSONInfo();
                        jsonCaller.execute();
                        textView.setText("");
                    }
                });

                return rootView;
            }


        public class GetJSONInfo extends AsyncTask<Void, Void, String[]> {
            private final String LOG_TAG = "GetJSONInfo";
            private final String NAME_OF_GOOD = "title";
            private final String RESULTS = "results";
            private final String DESCRIPTION = "description";
            String etsyJsonString = "If this is your string, there was an error!";

            @Override
            protected String[] doInBackground(Void... params) {

                //We only need to put two pieces together in this example, but in real coding you may have many more.
                //If there are lots of parameters in your search, you'll have to adjust the string below and add even more.
                final String URL_BASE = "https://openapi.etsy.com/v2/listings/active?api_key=";
                final String API_KEY = "8yuclbhlmoijgd6jld92l6t4";

                //Declare the variables up here that you'll need in both the "try" and the "catch"
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;


                try {
                    //Concatenate the URL
                    String urlString = URL_BASE + API_KEY;
                    URL urlToUse = new URL(urlString);

                    //Connect to the URL
                    urlConnection = (HttpURLConnection) urlToUse.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Get all the bytes of input from the inputStream
                    inputStream = urlConnection.getInputStream();


                    //Put the bytes into an ultra-speedy reader to read faster!
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    //Read the bytes and add them to a special String type called "StringBuilder" one by one.
                    int bytesRead;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((bytesRead = bufferedInputStream.read()) != -1) {
                        stringBuilder.append((char)bytesRead);
                    }

                    //We have a big long string of JSON words!
                    etsyJsonString = stringBuilder.toString();


                } catch (IOException e) {
                    // Catch if the code didn't successfully get the weather data.
                    Log.e(LOG_TAG, "IOException ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "IOException", e);
                    }
                }


                //Now that we have the big long JSON string, we gotta parse it.
                try {
                    String[] theInfoYouWanted = parsePostingFromJsonString(etsyJsonString);
                    //Once you get the two strings you want form the JSON string, return them! Returning from this ASYNC task
                    //automatically sends you down to "onPostExecute".
                    return theInfoYouWanted;
                } catch (JSONException e) {
                    return null;
                }
            }


            private String[] parsePostingFromJsonString(String etsyJsonString) throws JSONException {
                String title;
                String description;

                //Create a JSONOBject from the JSON string
                JSONObject postingJSONObject = new JSONObject(etsyJsonString);

                //Get the Array from within the object called "RESULTS"
                JSONArray arrayOfPostings = postingJSONObject.getJSONArray(RESULTS);

                //Fetch the first JSONObject within the results array, which is the most recent posting.
                JSONObject mostRecentPosting = arrayOfPostings.getJSONObject(0);

                //Get the title and description of the most recent posting
                title = mostRecentPosting.getString(NAME_OF_GOOD);
                description = mostRecentPosting.getString(DESCRIPTION);

                //Make an array out of the two strings you found, the name and the description.
                String[] arrayOfTitleAndDescription = new String[2];
                arrayOfTitleAndDescription[0] = title;
                arrayOfTitleAndDescription[1] = description;

                return arrayOfTitleAndDescription;
            }

            protected void onPostExecute(String[] result) {
                if (result != null) {
                    //Set the your textviews in the Activity to your new results!
                    //In real coding, you'd have to return this info through an "interface", but that's a lesson for another day...
                    textView.setText(result[0]);
                    descriptionView.setText(result[1]);
                }

            }
        }


    }


