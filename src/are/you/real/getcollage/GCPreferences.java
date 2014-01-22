package are.you.real.getcollage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.*;

/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCPreferences {
    private static final String TAG = "GCPreferences";

    public static final String CLIENT_ID    = "c34062307c0d4594bb3830eaab09488a";
    public static final String CALLBACK_URL = "instagram://connect";

    private static SharedPreferences sharedPref;

    private static final String SHARED_PREF_NAME       = "GetCollage Preferences";
    private static final String ACCESS_TOKEN           = "acces_token";
    private static final String DEFAULT_ACCESS_TOKEN   = "983514433.f59def8.0ee1f107fa3e46d1ab9ce70f1e89914b";
    private static final String USER_ID                = "UserId";
    private static final String MEDIA_LIMIT            = "MediaLimit";
    private static final String MEDIA_LIMIT_BY_DEFAULT = "1000";

    private static final TreeMap<Integer, List<String>>
                                                 imagesMap                   = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
    private static       String[]                imagesUrlsArr               = new String[20];
    private static       boolean[]               checkedGridCell             = new boolean[20];
    private static       HashMap<String, Bitmap> bitmaps                     = new HashMap<String, Bitmap>(20);
    private static       boolean
                                                 allBestÍmagesWereDownloaded = false;

    private static Handler mHandler;

    public static final int MSG_TURN_TO_FIRST_PAGE        = 0;
    public static final int MSG_TURN_TO_SECOND_PAGE       = 1;
    public static final int MSG_TURN_TO_THIRD_PAGE        = 2;
    public static final int MSG_TURN_TO_FORTH_PAGE        = 3;
    public static final int MSG_NOTHING_TO_SEND           = 4;
    public static final int MSG_SAVING_COLLAGE_TO_SD_CARD = 5;
    public static final int MSG_FETCHING_USER_INFO_START  = 6;
    public static final int MSG_FETCHING_USER_INFO_END    = 7;
    public static final int MSG_PROGRESS_DIALOG_DISMISS   = 8;
    public static final int MSG_ERROR                     = 9;
    public static final int MSG_A_LOT_OF_DATA             = 10;
    public static final int MSG_USER_NOT_FOUND            = 11;
    public static final int MSG_EXTREMELY_MANY_DATA       = 12;

    private static int requestCounter = 0;

    public static void init(Context context, Handler handler) {
        if (sharedPref == null) {
            sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
        mHandler = handler;
    }

    public static void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
        mHandler.sendEmptyMessage(GCPreferences.MSG_TURN_TO_FIRST_PAGE);
    }

    public static void resetAccessToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, DEFAULT_ACCESS_TOKEN);
        editor.commit();
    }

    public static boolean isUserLoggedIn() {
        return !(getAccessToken() == DEFAULT_ACCESS_TOKEN);
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    public static String getAccessToken() {
        return sharedPref.getString(ACCESS_TOKEN, DEFAULT_ACCESS_TOKEN);
    }

    public static void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public static String getUserId() {
        return sharedPref.getString(USER_ID, null);
    }

    public static void setMediaLimit(String limit) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MEDIA_LIMIT, limit);
        editor.commit();
    }

    public static String getMediaLimit() {
        return sharedPref.getString(MEDIA_LIMIT, MEDIA_LIMIT_BY_DEFAULT);
    }

    public static void putImageUrl(Integer key, String url) {
        if (imagesMap.get(key) != null)
            imagesMap.get(key).add(url);
        else {
            ArrayList arrayList = new ArrayList<String>();
            arrayList.add(url);
            imagesMap.put(key, arrayList);
        }
    }

    // TODO: This method should create SET and called in another way
    public static void findTheBestImages() {
        Set imagesUrls = imagesMap.entrySet();
        Iterator it = imagesUrls.iterator();
        int i = 0;
        while (it.hasNext() && i < imagesUrlsArr.length) {
            Map.Entry<Integer, List<String>> entry = (Map.Entry<Integer, List<String>>) it.next();
            ArrayList<String> aList = (ArrayList<String>) entry.getValue();
            if (i < imagesUrlsArr.length) {
                for (String s : aList) {
                    if (i < imagesUrlsArr.length)
                        imagesUrlsArr[i++] = s;
                }
            }
        }
/*        Bundle b = new Bundle();
        b.putInt(GCMainActivity.RESULT, GCPreferences.MSG_FETCHING_USER_INFO_END);
        Message msg = new Message();
        msg.setData(b);
        mHandler.sendMessage(msg);*/
        mHandler.sendEmptyMessage(GCPreferences.MSG_FETCHING_USER_INFO_END);
    }

    public static String getImageUrl(int position) {
        if (position >= imagesUrlsArr.length || position < 0)
            return null;

        return imagesUrlsArr[position];
    }

    public static void clearImageUrlArr() {
        imagesMap.clear();
        imagesUrlsArr = new String[20];
        clearCheckedState();
        bitmaps.clear();
        allBestÍmagesWereDownloaded = false;
    }

    public static void setCheckedGridCell(int cellNum, boolean checked) {
        if (cellNum < 0 && cellNum > checkedGridCell.length)
            return;
        checkedGridCell[cellNum] = checked;
    }

    public static boolean getCheckedCell(int cellNum) {
        return checkedGridCell[cellNum];
    }

    private static void clearCheckedState() {
        checkedGridCell = new boolean[20];
    }

    public static void putBitmap(String url, Bitmap bmp) {
        if (bitmaps.get(url) == null)
            bitmaps.put(url, bmp);
        if (bitmaps.size() == 20)
            allBestÍmagesWereDownloaded = true;
    }

    public static boolean areAllBestImagesDownloaded() {
        return allBestÍmagesWereDownloaded;
    }

    public static ArrayList<Bitmap> getBmpList() {
        ArrayList<Bitmap> returnValue = new ArrayList<Bitmap>(20);
        Set set = bitmaps.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Bitmap> entry = (Map.Entry<String, Bitmap>) it.next();
            Bitmap bmp = entry.getValue();
            returnValue.add(bmp);
        }
        return returnValue;
    }

    public static ArrayList<Bitmap> getSelectedList() {
        ArrayList<Bitmap> returnValue = new ArrayList<Bitmap>();
        for (int i = 0; i < imagesUrlsArr.length; i++) {
            if (checkedGridCell[i]) {
                returnValue.add(bitmaps.get(imagesUrlsArr[i]));
            }
        }

        for (int i = 0; i < returnValue.size(); i++) {
            Log.d(TAG, "" + returnValue.get(i));
        }
        Log.d(TAG, "" + returnValue.size());

        return returnValue;
    }

    public static boolean isAnyPhotoIsChecked() {
        for (int i = 0; i < checkedGridCell.length; i++) {
            if (checkedGridCell[i])
                return true;
        }
        return false;
    }

    public static boolean isAnyImageIsDownloaded() {
        return bitmaps.size() != 0;
    }

    public static void refreshRequestCounter() {
        requestCounter = 0;
    }

    public static void increseRequestCounter() {
        requestCounter++;
    }

    public static int getRequestCounter() {
        return requestCounter;
    }


}
