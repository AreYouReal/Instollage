package are.you.real.getcollage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
    private String          mTokenUrl;
    private String          mAccessToken;
    private Context         mContext;
    private String          mClientID;
    private String          mClientSecretKey;

    private enum RESULT{SUCCESS, ERROR, INFO}

    private static String mCallBackUrl = "";
    private static final String TOKEN_URL = "https://instagram.com/oauth/authorize/";


    public GCApp(Context context, String clientID, String clientSecretKey, String callbackUrl ){
        mClientID           = clientID;
        mClientSecretKey    = clientSecretKey;
        mContext            = context;


        mAccessToken = mPreferences.getAccessToken();
        mCallBackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + mClientID + "&redirect_uri=" + mCallBackUrl + "&response_type=token";

        GCDialog.GCOAuthDialogListener listener = new GCDialog.GCOAuthDialogListener(){
            @Override
            public void onComplete(String code) { /* getAccessToken(code);*/  }

            @Override
            public void onError(String error) { mListener.onFail("Authorization failed: " + error); }
        };

        mDialog = new GCDialog(context, mTokenUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    public static String getCallBackUrl(){
        return mCallBackUrl;
    }
/*


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
*/


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
                /*fetchUserName();*/
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


    public void authorize() {
/*        Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        webAuthIntent.setData(Uri.parse(AUTH_URL));
        mContext.startActivity(webAuthIntent);*/
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

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mPreferences.resetAccessToken();
            mAccessToken = null;
        }
    }


    public interface GCOAuthListener{
        public void onSuccess();
        public void onFail(String error);
    }
}
