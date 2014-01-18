package are.you.real.getcollage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Alexander on 18/01/14.
 */
public class GCImageAdapter extends BaseAdapter {
    private Context mContext;
    private int[] thumbsId = {};

    public GCImageAdapter(Context context){
        this.mContext = context;
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

        imageView.setImageResource(android.R.drawable.ic_menu_send);
        return imageView;
    }


}
