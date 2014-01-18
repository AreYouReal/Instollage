package are.you.real.getcollage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.androidquery.AQuery;

/**
 * Created by Alexander on 18/01/14.
 */
public class GCImageAdapter extends BaseAdapter {
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
        RelativeLayout rl;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            rl = new RelativeLayout(mContext);
        } else {
            rl = (RelativeLayout) convertView;
        }


        ImageView im = new ImageView(mContext);
        ProgressBar progress = new ProgressBar(mContext);

        androidAQuery.id(im).progress(progress).image(GCPreferences.getImageUrl(position), false, false);
        //imageView.setImageResource(android.R.drawable.ic_menu_send);
        return rl;
    }


}
