package are.you.real.getcollage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCPreferences {
    public static final String CLIENT_ID = "c34062307c0d4594bb3830eaab09488a";
    public static final String CALLBACK_URL = "instagram://connect";

    private static SharedPreferences           sharedPref;

    private static final String SHARED_PREF_NAME    = "GetCollage Preferences";
    private static final String API_USERNAME        = "username";
    private static final String API_ID              = "id";
    private static final String API_NAME            = "name";
    private static final String API_ACCESS_TOKEN = "acces_token";

    public static void init(Context context){
        if(sharedPref == null){
            sharedPref  = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public static void resetAccessToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(API_ACCESS_TOKEN, null);
        editor.commit();
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    public static String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }




}
