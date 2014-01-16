package are.you.real.getcollage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCApp {
    private static final String TAG = "GC_App";

    private GCPreferences   mPreferences;
    private GCDialog        mDialog;
    private GCOAuthListener mListener;
    private ProgressDialog  mProgress;
    private String          mAuthUrl;
    private String          mTokenUrl;
    private String          mAccessToken;
    private Context         mContext;
    private String          mClientID;
    private String          mClientSecretKey;

    private enum RESULT{SUCCESS, ERROR, INFO}

    private static String mCallBackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";


    public GCApp(Context context, String clientID, String clientSecretKey, String callbackUrl ){
        mClientID           = clientID;
        mClientSecretKey    = clientSecretKey;
        mContext            = context;

        mPreferences = new GCPreferences(context);
        mAccessToken = mPreferences.getAccessToken();
        mCallBackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + mClientID + "&client_secret=" +
                    mClientSecretKey + "&redirect_uri=" + mCallBackUrl + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL + "?client_id=" + mClientID + "&redirect_uri=" +
                   mCallBackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";

        GCDialog.GCOAuthDialogListener listener = new GCDialog.GCOAuthDialogListener(){
            @Override
            public void onComplete(String accessToken) { /* getAccesToken(); */ }

            @Override
            public void onError(String error) { mListener.onFail("Authorization failed"); }
        };

        mDialog = new GCDialog(context, mAuthUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    public static String getCallBackUrl(){
        return mCallBackUrl;
    }

    private void getAccessToken(final String code){
        mProgress.setMessage("Getting access token...");
        mProgress.show();
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = RESULT.INFO.ordinal();
                try {
                    URL url = new URL(TOKEN_URL);
                    //URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    //urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write("client_id="+mClientID+
                            "&client_secret="+mClientSecretKey+
                            "&grant_type=authorization_code" +
                            "&redirect_uri="+mCallBackUrl+
                            "&code=" + code);
                    writer.flush();
                    String response = streamToString(urlConnection.getInputStream());
                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString("username");
                    String name = jsonObj.getJSONObject("user").getString("full_name");

                    mPreferences.storeAccessToken(mAccessToken, id, user, name);

                } catch (Exception ex) {
                    what = RESULT.ERROR.ordinal();
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    private void fetchUserName() {
        mProgress.setMessage("Finalizing ...");

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = RESULT.SUCCESS.ordinal();
                try {
                    URL url = new URL(API_URL + "/users/" + mPreferences.getId() + "/?access_token=" + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    String name = jsonObj.getJSONObject("data").getString("full_name");
                    String bio = jsonObj.getJSONObject("data").getString("bio");
                    Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
                } catch (Exception ex) {
                    what = RESULT.ERROR.ordinal();
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RESULT.ERROR.ordinal()) {
                mProgress.dismiss();
                if(msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                }
                else if(msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            }
            else if(msg.what == RESULT.INFO.ordinal()) {
                fetchUserName();
            }
            else {
                mProgress.dismiss();
                mListener.onSuccess();
            }
        }
    };

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(GCOAuthListener listener) {
        mListener = listener;
    }

    public String getUserName() {
        return mPreferences.getUsername();
    }

    public String getId() {
        return mPreferences.getId();
    }

    public String getName() {
        return mPreferences.getName();
    }

    public void authorize() {
        //Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        //webAuthIntent.setData(Uri.parse(AUTH_URL));
        //mCtx.startActivity(webAuthIntent);
        mDialog.show();
    }

    private String streamToString(InputStream is) throws IOException {
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


    public interface GCOAuthListener{
        public void onSuccess();
        public void onFail(String error);
    }
}
