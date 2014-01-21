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

    private static final int NUM_PAGES = 4;
    private enum PAGES{FIRST_PAGE, SECOND_PAGE, THIRD_PAGE, FOURTH_PAGE};

    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;
    private GCImageAdapter  mImageAdapter;

    private ProgressDialog mProgress;


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what){
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
                    // was 1
                    if(mProgress != null && mProgress.isShowing()){
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
                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                    return true;
                case GCPreferences.MSG_ERROR:
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
