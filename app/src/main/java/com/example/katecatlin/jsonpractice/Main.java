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
            String etsyJsonString = "there was an error!";

            @Override
            protected String[] doInBackground(Void... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                final String URL_BASE = "https://openapi.etsy.com/v2/listings/active?api_key=";
                final String API_KEY = "8yuclbhlmoijgd6jld92l6t4";
//        URL urlToUse = null;

                try {
                    String urlString = URL_BASE + API_KEY;
                    URL urlToUse = new URL(urlString);
                    Log.d(LOG_TAG, "The URL string is " + urlString);

                    urlConnection = (HttpURLConnection) urlToUse.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    etsyJsonString = buffer.toString();
                    Log.d(LOG_TAG, "EtsyJSONString is " + etsyJsonString);


                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.d(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    Log.d(LOG_TAG, "Ok so it's making it to the last line...");
                    String[] theInfoYouWanted = parsePostingFromJsonString(etsyJsonString);
                    return theInfoYouWanted;

                } catch (JSONException e) {
                    return null;
                }
            }


            private String[] parsePostingFromJsonString(String etsyJsonString) throws JSONException {
//                Log.d(LOG_TAG, "inside the last class, the string is... " + etsyJsonString);
                String title;
                String description;
                JSONObject postingJSONObject = new JSONObject(etsyJsonString);
                JSONArray arrayOfPostings = postingJSONObject.getJSONArray(RESULTS);

                JSONObject mostRecentPosting = arrayOfPostings.getJSONObject(0);
//                Log.d(LOG_TAG, "mostRecentPosting is " + mostRecentPosting);
                title = mostRecentPosting.getString(NAME_OF_GOOD);
                description = mostRecentPosting.getString(DESCRIPTION);
                Log.d(LOG_TAG, "title at last is " + title);
                Log.d(LOG_TAG, "description at last is " + description);

                String[] arrayOfTitleAndDescription = new String[2];
                arrayOfTitleAndDescription[0] = title;
                arrayOfTitleAndDescription[1] = description;
                return arrayOfTitleAndDescription;
            }

            protected void onPostExecute(String[] result) {
                if (result != null) {
                    Log.d(LOG_TAG, "IT'S NOT NULL IN THE POST EXECUTE, MANNNN!");
                    textView.setText(result[0]);
                    descriptionView.setText(result[1]);

                }

            }
        }


    }


