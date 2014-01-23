package are.you.real.getcollage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by AreYouReal on 23/01/14.
 */
public class GCDialog extends AlertDialog {
    private ImageView image;
    private int imageId;

    public GCDialog(Context context, int id) {
        super(context);
        this.imageId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_screen);
        image = (ImageView)findViewById(R.id.im_view);
        Bitmap bmp = GCPreferences.getBmpList2().get(imageId);
        image.setImageBitmap(bmp);
    }
}
