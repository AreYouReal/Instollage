package are.you.real.getcollage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by AreYouReal on 17/01/14.
 */
class GCWebViewClient extends WebViewClient {

    private static final String TAG = "GCWebViewClient";

    private ProgressDialog mProgress;

    GCWebViewClient(Context context){
        super();
        mProgress = new ProgressDialog(context);
        mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String spinnerMessage = context.getResources().getString(R.string.loading);
        mProgress.setMessage(spinnerMessage);
        mProgress.setCancelable(false);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "Redirect URL: " + url);

/*        if(url.startsWith("instagram://connect")){
            String urls[] = url.split("=");
            Log.d(TAG, "TOKEN: " + urls[1]);
            GCPreferences.setAccessToken(urls[1]);
            return true;
        }*/
        return false;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.d(TAG, "Page error: " + description + failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "Loading URL: " + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: " + url);
        super.onPageFinished(view, url);
    }

}