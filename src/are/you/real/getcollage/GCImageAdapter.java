package are.you.real.getcollage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //androidAQuery.id(imageView).image(GCPreferences.getImageUrl(position), true, true, 150,android.R.drawable.btn_default );
        imageView.setImageResource(android.R.drawable.ic_menu_send);
        return imageView;
    }


}
