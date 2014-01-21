package are.you.real.getcollage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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
    private Context mContext;
    private static AQuery androidAQuery;

    public GCImageAdapter(Context context){
        this.mContext = context;
        androidAQuery = new AQuery(mContext);
    }

    public int getCount(){
        return 20;
    }

    public Object getItem(int position){
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
        if(GCPreferences.getCheckedCell(position))
            rl.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));

        androidAQuery.id(im).progress(progress).image(GCPreferences.getImageUrl(position), true, true, 0, 0, new BitmapAjaxCallback(){
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                iv.setImageBitmap(bm);
                Log.d(TAG, "" + bm + "\t" + url);
                GCPreferences.putBitmap(url, bm);
            }
        });
        return rl;
    }


}
