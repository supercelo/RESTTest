package com.example.mc.resttest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String code = ((EditText) findViewById(R.id.code)).getText().toString();
        if (code != "") {
            btn.setClickable(false);
            new Rest().execute("http://services.groupkt.com/country/get/iso2code/" + code);
        }
    }

    private class Rest extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            try {
                // create connection
                URL urlToRequest = new URL(urls[0]);
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                // handle issues
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    // handle unauthorized (if service requires user login)
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    // handle any other errors, like 404, 500,..
                }

                // create JSON object from content
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getResponseText(in));
                return json;

            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                // data retrieval or connection timed out
            } catch (IOException e) {
                // could not read response body
                // (could not create input stream)
            } catch (JSONException e) {
                // response body is no valid JSON string
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private String getResponseText(InputStream inStream) {
            return new Scanner(inStream).useDelimiter("\\A").next();
        }

        protected void onPostExecute(JSONObject json) {
            String result = "";
            try {
                result = json.getJSONObject("RestResponse").getJSONObject("result").getString("name");
            } catch (JSONException e) {
                result = "not found...";
                e.printStackTrace();
            }
            ((TextView)findViewById(R.id.result)).setText(result);
            btn.setClickable(true);
        }

    }
}