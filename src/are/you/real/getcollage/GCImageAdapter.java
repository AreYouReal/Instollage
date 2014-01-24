package are.you.real.getcollage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * Created by Alexander on 18/01/14.
 */
public class GCImageAdapter extends BaseAdapter {
    private static final String TAG = "GCImageAdapter";
    private static AQuery  androidAQuery;
    private        Context mContext;

    public GCImageAdapter(Context context) {
        this.mContext = context;
        androidAQuery = new AQuery(mContext);
    }

    public int getCount() {
        return 20;
    }

    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout rl;
        rl = new FrameLayout(mContext);

        ImageView im = new ImageView(mContext);
        ProgressBar progress = new ProgressBar(mContext);
        rl.addView(progress);
        rl.addView(im);
        if (GCPreferences.getCheckedCell(position))
            rl.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));

//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        rl.setLayoutParams(params);

        androidAQuery.id(im).progress(progress).image(GCPreferences.getImageUrl(position), true, true, 0, 0, new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                iv.setImageBitmap(bm);
                GCPreferences.putBitmap(url, bm);
            }
        });
        return rl;
    }
}
