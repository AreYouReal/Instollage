package are.you.real.getcollage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCPreferences {
    public static final String CLIENT_ID = "c34062307c0d4594bb3830eaab09488a";
    public static final String CALLBACK_URL = "instagram://connect";

    private static SharedPreferences    sharedPref;

    private static final String SHARED_PREF_NAME    = "GetCollage Preferences";
    private static final String ACCESS_TOKEN = "acces_token";
    private static final String DEFAULT_ACCESS_TOKEN = "983514433.f59def8.0ee1f107fa3e46d1ab9ce70f1e89914b";

    private static Handler mHandler;

    public static void init(Context context, Handler handler){
        if(sharedPref == null){
            sharedPref  = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
        mHandler = handler;
    }

    public static void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();

        Bundle b = new Bundle();
        b.putInt(GCMainActivity.RESULT, -2);
        Message msg = new Message();
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public static void resetAccessToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, DEFAULT_ACCESS_TOKEN);
        editor.commit();
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    public static String getAccessToken() {
        return sharedPref.getString(ACCESS_TOKEN, DEFAULT_ACCESS_TOKEN);
    }

    public static String getDefaultAccessToken(){
        return DEFAULT_ACCESS_TOKEN;
    }




}
