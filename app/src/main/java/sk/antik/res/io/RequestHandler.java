package sk.antik.res.io;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Snowcat on 23-Aug-15.
 */
public class RequestHandler {

    private URL apiUrl;
    private HttpURLConnection urlConnection;
    private SecretKeySpec key;
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
