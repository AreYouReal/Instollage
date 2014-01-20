package are.you.real.getcollage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class GCMainActivity extends FragmentActivity {

    public static final String RESULT = "RESULT";

    private static final String TAG = "GCMainActivity";

    private static final int NUM_PAGES = 3;
    private enum PAGES{FIRST_PAGE, SECOND_PAGE, THIRD_PAGE, FOURTH_PAGE};

    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;
    private GCImageAdapter  mImageAdapter;

    private ProgressDialog mProgress;


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int result = message.getData().getInt(RESULT);

            switch(result){
                case -4:
                    Toast.makeText(GCMainActivity.this, getResources().getText(R.string.nothing_to_send), Toast.LENGTH_SHORT).show();
                    return true;
                case -3:
                    mProgress = new ProgressDialog(GCMainActivity.this);
                    mProgress.setTitle(GCMainActivity.this.getResources().getString(R.string.generating_image));
                    mProgress.setMessage(GCMainActivity.this.getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.setIcon(android.R.drawable.picture_frame);
                    mProgress.show();
                    return true;
                case -2:
                    mPager.setCurrentItem(PAGES.FIRST_PAGE.ordinal());
                    return true;
                case -1:
                    mProgress = new ProgressDialog(GCMainActivity.this);
                    mProgress.setTitle(GCMainActivity.this.getResources().getString(R.string.fetching_user_info));
                    mProgress.setMessage(GCMainActivity.this.getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.setIcon(android.R.drawable.ic_menu_camera);
                    mProgress.show();
                    return true;
                case 1:
                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                        mPager.setCurrentItem(PAGES.SECOND_PAGE.ordinal());
                        mImageAdapter.notifyDataSetChanged();
                    }
                    return true;
                case 2:
                    mPager.setCurrentItem(PAGES.FOURTH_PAGE.ordinal());
                    return true;
                case 3:
                    mPager.setCurrentItem(PAGES.THIRD_PAGE.ordinal());
                    return true;
                case 4:
                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                    return true;
                case 666:
                    mProgress.dismiss();
                    Toast.makeText(GCMainActivity.this, getResources().getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    });

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.pager);

        mImageAdapter = new GCImageAdapter(this);

        GCPreferences.init(this, mHandler);
        GCSession.init(mHandler);
        GCFragment.init(this, mHandler, mImageAdapter);

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
