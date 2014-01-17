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
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://api.instagram.com/oauth/authorize/?client_id=c34062307c0d4594bb3830eaab09488a&redirect_uri=instagram://connect&response_type=code");
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent.addView(mWebView);
    }

    public interface GCOAuthDialogListener{
        public void onComplete(String code);
        public void onError(String error);

    }

}
