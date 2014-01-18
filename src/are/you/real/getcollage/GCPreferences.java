package are.you.real.getcollage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCPreferences {
    private static final String TAG = "GCPreferences";

    public static final String CLIENT_ID = "c34062307c0d4594bb3830eaab09488a";
    public static final String CALLBACK_URL = "instagram://connect";

    private static SharedPreferences    sharedPref;

    private static final String SHARED_PREF_NAME    = "GetCollage Preferences";
    private static final String ACCESS_TOKEN = "acces_token";
    private static final String DEFAULT_ACCESS_TOKEN = "983514433.f59def8.0ee1f107fa3e46d1ab9ce70f1e89914b";
    private static final String USER_ID = "UserId";
    private static final String MEDIA_LIMIT = "PhotoLimit";
    private static final String MEDIA_LIMIT_BY_DEFAULT = "1000";

    private static final TreeMap<Integer, String> imagesMap = new TreeMap<Integer, String>();
    private static String[] imagesUrlsArr;

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

    public static void setUserId(String userId){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public static String getUserId(){
        return sharedPref.getString(USER_ID, null);
    }

    public static void setMediaLimit(String limit){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MEDIA_LIMIT, limit);
        editor.commit();
    }

    public static String getMediaLimit(){
        return sharedPref.getString(MEDIA_LIMIT, MEDIA_LIMIT_BY_DEFAULT);
    }

    public static void putImageUrl(Integer key, String url){
        imagesMap.put(key, url);
        //Log.d(TAG, key + "\t" + imagesMap.get(key));
    }

    // TODO: This method should create SET and called in another way
    public static void printFirst20thPhotos(){
        Set imagesUrls = imagesMap.entrySet();
        Iterator it = imagesUrls.iterator();
        int i = 0;
        while(it.hasNext()){
            if(i < 20)
                imagesUrlsArr[i] = it.next().toString();

            Log.d(TAG, it.next().toString());
        }
    }

    public static String getImageUrl(int position){
        if(position >= imagesUrlsArr.length || position < 0)
            return null;

        return imagesUrlsArr[position];
    }
}
