package are.you.real.getcollage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.webkit.WebView;

public class GCMainActivity extends FragmentActivity {

    private static final String TAG = "GCMainActivity";

    private static final int NUM_PAGES = 2;

    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);

        GCPreferences.init(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new GCPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        mPager.setAdapter(mPagerAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
