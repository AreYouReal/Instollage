package are.you.real.getcollage;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by AreYouReal on 17/01/14.
 */
class GCWebViewClient extends WebViewClient {

    private static final String TAG = "GCWebViewClient";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("instagram://connect")) {
            String urls[] = url.split("=");
            GCPreferences.setAccessToken(urls[1]);
            return true;
        }
        return false;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

}