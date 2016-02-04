package sk.antik.res.io;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Snowcat on 23-Aug-15.
 */
public class RequestHandler {

    private URL apiUrl;
    private HttpURLConnection urlConnection;


    public RequestHandler(String url) {
        try {
            apiUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JSONObject handleRequest(JSONObject request) throws IOException {
        try {
            urlConnection = (HttpURLConnection) apiUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(10000);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(request.toString().getBytes());
            return new JSONObject(convertStreamToString(urlConnection.getInputStream()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertStreamToString(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            is.close();
            urlConnection.disconnect();
            Log.e("Response", sb.toString());

            return sb.toString();
        } catch (Exception en) {
            return "";
        }
    }


}
