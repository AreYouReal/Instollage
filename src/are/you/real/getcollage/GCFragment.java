package are.you.real.getcollage;

import android.app.Activity;
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

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCFragment extends Fragment {
    private static final String TAG = "GCFragment";
    public  static final String ARG_PAGE = "page";

    private static Handler mHandler;
    private Context mContext;

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
                wv = new WebView(getActivity().getApplicationContext());
                wv.setWebViewClient(new GCWebViewClient(getActivity().getApplicationContext()));
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
                            Toast.makeText(getActivity().getApplicationContext(), R.string.error_too_short_username, Toast.LENGTH_SHORT).show();
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
                grid.setAdapter(new GCImageAdapter(getActivity().getApplicationContext()));
                Button button = (Button) collageView.findViewById(R.id.send_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().getApplicationContext().startActivity(emailIntent);
                    }
                });
                return collageView;

            default:
                TextView tv = new TextView(getActivity().getApplicationContext());
                tv.setText("Page# " + numOfPage);
                return tv;
        }
    }
}
