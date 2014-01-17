package are.you.real.getcollage;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCFragment extends Fragment {
    private static final String TAG = "GCFragment";
    public  static final String ARG_PAGE = "page";

    private static Handler mHandler;

    private int numOfPage;

    public static void init(Handler handler){
        mHandler = handler;
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
                wv = new WebView(getActivity().getBaseContext());
                wv.setWebViewClient(new GCWebVIewClient(getActivity().getBaseContext()));
                wv.getSettings().setJavaScriptEnabled(true);
                wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                wv.loadUrl("https://instagram.com/oauth/authorize/?client_id=" + GCPreferences.CLIENT_ID +  "&redirect_uri=" + GCPreferences.CALLBACK_URL + "&response_type=token");
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
                            Toast.makeText(getActivity().getBaseContext(), R.string.error_too_short_username, Toast.LENGTH_SHORT).show();
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

            default:
                TextView tv = new TextView(getActivity().getBaseContext());
                tv.setText("Page# " + numOfPage);
                return tv;

        }
    }
}
