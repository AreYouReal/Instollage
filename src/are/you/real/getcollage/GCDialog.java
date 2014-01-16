package are.you.real.getcollage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by AreYouReal on 16/01/14.
 */
public class GCDialog extends Dialog {

    private static final String TAG = "GetCollage_Dialog";

    private String                  mUrl;
    private GCOAuthDialogListener   mListener;
    private ProgressDialog          mSpinner;
    private WebView                 mWebView;
    private LinearLayout            mContent;
    private TextView                mTitle;

    public GCDialog(Context context, String url, GCOAuthDialogListener listener) {
        super(context);
        mUrl        = url;
        mListener   = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String spinnerMessage = getContext().getResources().getString(R.string.loading);
        mSpinner.setMessage(spinnerMessage);
        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpWebView();
        mTitle = new TextView(getContext());
        mTitle.setText("Title text");
        setTitle(mTitle.getText());
        addContentView(mContent, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setUpWebView(){
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new GCOAuthWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent.addView(mWebView);
    }

    private class GCOAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirect URL: " + url);

            if(url.startsWith(GCApp.getCallBackUrl())){
                String urls[] = url.split("=");
                mListener.onComplete(urls[1]);
                GCDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "Page error: " + description + failingUrl);

            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(description);
            GCDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Loading URL: " + url);

            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
            Log.d(TAG, "onPageFinished URL: " + url);
            mSpinner.dismiss();
        }

    }

    public interface GCOAuthDialogListener{
        public void onComplete(String code);
        public void onError(String error);

    }

}
