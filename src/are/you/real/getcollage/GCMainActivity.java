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

    private static final String TAG = "GCMainActivity";

    private static final int NUM_PAGES = 4;

    private enum PAGES {FIRST_PAGE, SECOND_PAGE, THIRD_PAGE, FOURTH_PAGE};

    private ViewPager      mPager;
    private PagerAdapter   mPagerAdapter;
    private GCImageAdapter mImageAdapter;
    private ProgressDialog mProgress;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case GCPreferences.MSG_NOTHING_TO_SEND:
                    Toast.makeText(GCMainActivity.this, getResources().getText(R.string.nothing_to_send), Toast.LENGTH_SHORT).show();
                    return true;
                case GCPreferences.MSG_SAVING_COLLAGE_TO_SD_CARD:
                    mProgress = new ProgressDialog(GCMainActivity.this);
                    mProgress.setTitle(GCMainActivity.this.getResources().getString(R.string.generating_image));
                    mProgress.setMessage(GCMainActivity.this.getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.setIcon(android.R.drawable.picture_frame);
                    mProgress.show();
                    return true;
                case GCPreferences.MSG_TURN_TO_FIRST_PAGE:
                    mPager.setCurrentItem(PAGES.FIRST_PAGE.ordinal());
                    Toast.makeText(GCMainActivity.this, getResources().getString(R.string.successfully_logged), Toast.LENGTH_SHORT).show();
                    return true;
                case GCPreferences.MSG_FETCHING_USER_INFO_START:
                    mProgress = new ProgressDialog(GCMainActivity.this);
                    mProgress.setTitle(GCMainActivity.this.getResources().getString(R.string.fetching_user_info));
                    mProgress.setMessage(GCMainActivity.this.getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.setIcon(android.R.drawable.ic_menu_camera);
                    mProgress.show();
                    return true;
                case GCPreferences.MSG_FETCHING_USER_INFO_END:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                        mPager.setCurrentItem(PAGES.SECOND_PAGE.ordinal());
                        mImageAdapter.notifyDataSetChanged();
                    }
                    return true;
                case GCPreferences.MSG_TURN_TO_FORTH_PAGE:
                    mPager.setCurrentItem(PAGES.FOURTH_PAGE.ordinal());
                    return true;
                case GCPreferences.MSG_TURN_TO_THIRD_PAGE:
                    mPager.setCurrentItem(PAGES.THIRD_PAGE.ordinal());
                    return true;
                case GCPreferences.MSG_PROGRESS_DIALOG_DISMISS:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                    }
                    return true;
                case GCPreferences.MSG_ERROR:
                    mProgress.dismiss();
                    Toast.makeText(GCMainActivity.this, getResources().getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return true;
                case GCPreferences.MSG_A_LOT_OF_DATA:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.setTitle(R.string.a_lot_of_user_info);
                    }
                    return true;
                case GCPreferences.MSG_USER_NOT_FOUND:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                        Toast.makeText(GCMainActivity.this, getResources().getText(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case GCPreferences.MSG_EXTREMELY_MANY_DATA:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.setTitle(R.string.extremely_many);
                    }
                    return true;
                case GCPreferences.MSG_TIMEOUT_EXCEPTION:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                        Toast.makeText(GCMainActivity.this, getResources().getText(R.string.timeout_has_expired), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case GCPreferences.MSG_MAXIMUM_NUM_OF_REQUESTS:
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                        Toast.makeText(GCMainActivity.this, getResources().getText(R.string.exceeded_maximum_number_of_requests), Toast.LENGTH_LONG).show();
                        if(!GCPreferences.isUserLoggedIn())
                            mPager.setCurrentItem(PAGES.FOURTH_PAGE.ordinal());
                    }
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
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == PAGES.FOURTH_PAGE.ordinal() && GCPreferences.isUserLoggedIn()) {
                    mPager.setCurrentItem(PAGES.FIRST_PAGE.ordinal());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == PAGES.FOURTH_PAGE.ordinal()) {
            mPager.setCurrentItem(PAGES.FIRST_PAGE.ordinal());
            return;
        }
        if (mPager.getCurrentItem() == PAGES.FIRST_PAGE.ordinal()) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }

    }
}
