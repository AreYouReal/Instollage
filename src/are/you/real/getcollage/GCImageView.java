package are.you.real.getcollage;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by Alexander on 19/01/14.
 */
public class GCImageView extends ImageView {
    public GCImageView(Context context) {
        super(context);
    }

    public GCImageView(Context context, AttributeSet attrSet){
        super(context, attrSet);
    }
    public GCImageView(Context context, AttributeSet attrSet, int defStyle){
        super(context, attrSet, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
