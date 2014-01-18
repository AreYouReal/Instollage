package are.you.real.getcollage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.*;
import com.androidquery.AQuery;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCFragment extends Fragment {
    private static final String TAG = "GCFragment";
    public  static final String ARG_PAGE = "page";

    private static Context mContext;
    private static Handler mHandler;

    private int numOfPage;

    public static void init(Context context, Handler handler){
        mHandler = handler;
        mContext = context;
    }


    public static GCFragment create(int page){
        GCFragment fragment = new GCFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numOfPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // TODO: make switch
        switch (numOfPage){
            case 0:
                WebView wv;
                wv = new WebView(mContext);
                wv.setWebViewClient(new GCWebViewClient(mContext));
                wv.getSettings().setJavaScriptEnabled(true);
                wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                wv.loadUrl("https://instagram.com/oauth/authorize/?client_id=" + GCPreferences.CLIENT_ID +  "&redirect_uri=" + GCPreferences.CALLBACK_URL + "&response_type=token"); // TODO: WTF? This code crushs my app:))
                //wv.loadUrl("https://instagram.com/accounts/login/?next=/oauth/authorize/%3Fclient_id%3Dc34062307c0d4594bb3830eaab09488a%26redirect_uri%3Dinstagram%3A//connect%26response_type%3Dtoken"); // TODO: And this doesn't:)

                Log.v(TAG, "Container " + container);
                return wv;

            case 1:
                ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.second_screen, container, false);
                final EditText usernameText = (EditText)rootView.findViewById(R.id.user_name);
                Button btn = (Button) rootView.findViewById(R.id.button);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(usernameText.getText().length() <= 1)
                            Toast.makeText(mContext, R.string.error_too_short_username, Toast.LENGTH_SHORT).show();
                        else{
                            GCSession.getUserId(usernameText.getText().toString().trim());
                            Bundle b = new Bundle();
                            b.putInt(GCMainActivity.RESULT, -1);
                            Message msg = new Message();
                            msg.setData(b);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
                return rootView;
            case 2:
                ViewGroup collageView = (ViewGroup) inflater.inflate(R.layout.collage_screen, container, false);
                GridView grid = (GridView) collageView.findViewById(R.id.collage_grid);
                grid.setAdapter(new GCImageAdapter(mContext));
                Button button = (Button) collageView.findViewById(R.id.send_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(emailIntent);
                    }
                });
                return collageView;

            default:
                TextView tv = new TextView(mContext);
                tv.setText("Page# " + numOfPage);
                return tv;
        }
    }
}
