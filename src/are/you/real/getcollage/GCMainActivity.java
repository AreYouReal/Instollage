package are.you.real.getcollage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class GCMainActivity extends FragmentActivity {

    public static final String RESULT = "RESULT";

    private static final String TAG = "GCMainActivity";

    private static final int NUM_PAGES = 3;
    private enum PAGES{FIRST_PAGE, SECOND_PAGE, THIRD_PAGE};

    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;

    private ProgressDialog mProgress;


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int result = message.getData().getInt(RESULT);
            if(result == 1){
                if(mProgress != null && mProgress.isShowing()){
                    mProgress.dismiss();
                    mPager.setCurrentItem(2);
                }
            }
            if(result == -1){
                mProgress = new ProgressDialog(GCMainActivity.this);
                mProgress.setMessage(GCMainActivity.this.getResources().getString(R.string.loading));
                mProgress.setCancelable(false);
                mProgress.show();
            }
            if(result == -2)
                mPager.setCurrentItem(1);

            return false;
        }
    });

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);

        GCPreferences.init(this, mHandler);
        GCSession.init(mHandler);
        GCFragment.init(this, mHandler);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new GCPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(PAGES.FIRST_PAGE.ordinal());

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
}
