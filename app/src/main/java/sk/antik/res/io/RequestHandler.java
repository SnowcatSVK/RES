package sk.antik.res.io;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
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
    private Context context;


    public RequestHandler(String url, Context context) {
        try {
            apiUrl = new URL(url);
            this.context = context;
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
            String response = convertStreamToString(urlConnection.getInputStream());
            Log.e("Handler response", response);
            return new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertStreamToString(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            is.close();
            urlConnection.disconnect();

            return sb.toString();
        } catch (Exception en) {
            return "";
        }
    }

    public void downloadFile(String address, String apkName) throws IOException {
        FileOutputStream out = new FileOutputStream(context.getExternalFilesDir(null).getPath() + "/" +apkName);
        Log.e("ApkResponse", context.getExternalFilesDir(null).getPath() + apkName);
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        InputStream is = connection.getInputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        int bufferLength;
        while ((bufferLength = is.read(buffer)) > 0) {
            bytesRead += bufferLength;
            out.write(buffer, 0,bufferLength);
        }

        is.close();

        out.flush();
        out.close();
        Log.e("Server test", "Bytes for address: " + address + " = " + String.valueOf(bytesRead));
    }

}
