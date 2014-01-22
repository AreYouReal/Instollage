package are.you.real.getcollage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCPagerAdapter extends FragmentStatePagerAdapter {
    private int mPages;

    public GCPagerAdapter(FragmentManager fm, int pages) {
        super(fm);
        this.mPages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return GCFragment.create(position);
    }

    @Override
    public int getCount() {
        return mPages;
    }
}
