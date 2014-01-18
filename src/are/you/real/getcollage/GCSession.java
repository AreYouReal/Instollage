package are.you.real.getcollage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCSession {
    private static final String TAG = "GCSession";

    private static final String USER_ID_URL = "https://api.instagram.com/v1/users/search?q=";

    private static Handler mHandler;

    public static void init(Handler handler){
        mHandler = handler;
    }

    public static void getUserId(final String userName){
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Fetching user id");
                    String stringUrl = USER_ID_URL + userName + "&count=1&access_token=" + GCPreferences.getAccessToken();
                    URL url = new URL(stringUrl);
                    Log.d(TAG, "Request url:" + url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    Log.d(TAG,"Response: " + response);

                    JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray mArray = jsonObject.getJSONArray("data");

                    Log.d(TAG, mArray.getJSONObject(0)+ "\n" + mArray.getJSONObject(0).getString("id"));

                    String id = mArray.getJSONObject(0).getString("id");
                    Log.d(TAG, "ID: " + id);
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
                Bundle b = new Bundle();
                b.putInt(GCMainActivity.RESULT, 1);
                Message msg = new Message();
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }.start();
    }




    private static String streamToString(InputStream is) throws IOException {
        Log.d(TAG, "streamToString");
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

}
