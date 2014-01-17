package are.you.real.getcollage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCFragment extends Fragment {
    private static final String TAG = "GCFragment";
    public  static final String ARG_PAGE = "page";

    private int numOfPage;

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
        if(numOfPage == 0){
            WebView wv;
            wv = new WebView(getActivity().getBaseContext());
            wv.setWebViewClient(new GCWebVIewClient(getActivity().getBaseContext()));
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.loadUrl("https://instagram.com/oauth/authorize/?client_id=" + GCPreferences.CLIENT_ID +  "&redirect_uri=" + GCPreferences.CALLBACK_URL + "&response_type=token");
            return wv;
        }else{
            Button btn = new Button(getActivity().getBaseContext());

            btn.setText(GCPreferences.getAccessToken() == null ? "Something went wrong:(": GCPreferences.getAccessToken());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return btn;
        }

    }
}
