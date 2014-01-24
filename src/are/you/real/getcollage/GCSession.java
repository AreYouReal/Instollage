package are.you.real.getcollage;

import android.os.Handler;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCSession {
    private static final String TAG               = "GCSession";
    private static final String INSTAGRAM_API_URL = "https://api.instagram.com";
    private static final String GET_USER_ID_URL   = "/v1/users/search?q=";
    private static final int    CONNECT_TIMEOUT   = 3000;
    private static final int    READ_TIMEOUT      = 5000;

    private static Handler mHandler;

    public static void init(Handler handler) {
        mHandler = handler;
    }

    public static void getUserId(final String userName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Fetching user id");
                    String stringUrl = INSTAGRAM_API_URL + GET_USER_ID_URL + userName + "&count=1&access_token=" + GCPreferences.getAccessToken();
                    URL url = new URL(stringUrl);
                    Log.d(TAG, "Request url:" + url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
                    urlConnection.setReadTimeout(CONNECT_TIMEOUT);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    Log.d(TAG, "Response: " + response);

                    JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray mArray = jsonObject.getJSONArray("data");
                    if (mArray.length() <= 0) {
                        mHandler.sendEmptyMessage(GCPreferences.MSG_USER_NOT_FOUND);
                        return;
                    }


                    Log.d(TAG, mArray.getJSONObject(0) + "\n" + mArray.getJSONObject(0).getString("id"));

                    String id = mArray.getJSONObject(0).getString("id");
                    Log.d(TAG, "ID: " + id);
                    GCPreferences.setUserId(id);
                    getUserImages();
                }catch(FileNotFoundException fnf){
                    Log.e(TAG, fnf.toString());
                    fnf.printStackTrace();
                    mHandler.sendEmptyMessage(GCPreferences.MSG_MAXIMUM_NUM_OF_REQUESTS);
                } catch(SocketTimeoutException ste){
                    Log.e(TAG, ste.toString());
                    ste.printStackTrace();
                    mHandler.sendEmptyMessage(GCPreferences.MSG_TIMEOUT_EXCEPTION);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(GCPreferences.MSG_ERROR);
                }
            }
        }.start();
    }

    public static void getUserImages() {

        GCPreferences.clearImageUrlArr();
        new Thread() {
            @Override
            public void run() {
                try {
                    GCPreferences.refreshRequestCounter();
                    Log.d(TAG, "Fetching user's images");
                    String stringUrl = "https://api.instagram.com/v1/users/" + GCPreferences.getUserId()
                                       + "/media/recent/?count=" + GCPreferences.getMediaLimit()
                                       + "&access_token=" + GCPreferences.getAccessToken();
                    do {
                        URL url = new URL(stringUrl);
                        Log.d(TAG, "Request url:" + url);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
                        urlConnection.setReadTimeout(CONNECT_TIMEOUT);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        String response = streamToString(urlConnection.getInputStream());
                        Log.d(TAG, "Response: " + response);
                        GCPreferences.increseRequestCounter();
                        JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                        if (jsonObject.getJSONObject("pagination").has("next_url"))
                            stringUrl = jsonObject.getJSONObject("pagination").getString("next_url");
                        else
                            stringUrl = null;
                        Log.d(TAG, stringUrl == null ? "null" : stringUrl);
                        JSONArray mArray = jsonObject.getJSONArray("data");
                        Log.d(TAG, "" + mArray.length());
                        for (int i = 0; i < mArray.length(); i++) {
                            String[] imageData = getImage(mArray.getJSONObject(i));
                            if (imageData != null)
                                GCPreferences.putImageUrl(Integer.parseInt(imageData[0]), imageData[1]);

                            if (GCPreferences.getRequestCounter() == 20)
                                mHandler.sendEmptyMessage(GCPreferences.MSG_A_LOT_OF_DATA);
                        }
                        if (GCPreferences.getRequestCounter() == 40) {
                            mHandler.sendEmptyMessage(GCPreferences.MSG_EXTREMELY_MANY_DATA);
                        }
                    } while (stringUrl != null);
                    GCPreferences.findTheBestImages();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(GCPreferences.MSG_ERROR);
                }
            }
        }.start();
    }

    private static String[] getImage(JSONObject jsObject) {
        try {
            if (!(jsObject.getString("type").equals("image")))
                return null;
            String[] returnValue = {jsObject.getJSONObject("likes").getString("count")
                    , jsObject.getJSONObject("images").getJSONObject("low_resolution").getString("url")};
            return returnValue;
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
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
