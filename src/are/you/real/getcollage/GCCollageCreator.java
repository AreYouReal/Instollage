package are.you.real.getcollage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alexander on 19/01/14.
 */
public class GCCollageCreator {
    private static final String TAG = "GCCollageCreator";
    private static Bitmap collage = null;

    public static Bitmap createCollage(){
        if(!GCPreferences.areAllBestImagesDownloaded())
            return null;
        if(collage != null)
            return collage;

        if(GCPreferences.isAnyPhotoIsChecked())
            return createSelectedCollage();

        ArrayList<Bitmap> bitmaps = GCPreferences.getBmpList();

        Bitmap bitmap = Bitmap.createBitmap(bitmaps.get(0).getWidth() * 5, bitmaps.get(0).getWidth() * 4, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        int left = 0, top = 0;
        for(Bitmap bmp: bitmaps){
            if(left >= 5 * bmp.getWidth()){
                left = 0;
                top += bmp.getHeight();
            }
            if(top >= 4 * bmp.getHeight())
                top = 0;
            canvas.drawBitmap(bmp, left, top, null);
            left += bmp.getWidth();
        }

        collage = bitmap;

        return collage;
    }

    public static void clear(){
        if(collage != null)
            collage.recycle();
        collage = null;
    }

    private static Bitmap createSelectedCollage(){
        ArrayList<Bitmap> checkedBitmaps = GCPreferences.getSelectedList();
        int horizontalCoef = 2;
        if(checkedBitmaps.size() >= 6)
            horizontalCoef = checkedBitmaps.size() / 3;
        if(checkedBitmaps.size() > 12)
            horizontalCoef = checkedBitmaps.size() / 4;

        int verticalCoef = (checkedBitmaps.size() - horizontalCoef);
        if(verticalCoef == 0)
            verticalCoef = horizontalCoef;
        if(verticalCoef < 0)
            verticalCoef = -verticalCoef;

        Bitmap bitmap = Bitmap.createBitmap(checkedBitmaps.get(0).getWidth() * horizontalCoef,
                checkedBitmaps.get(0).getHeight() * verticalCoef,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);




        int left = 0, top = 0;
        for(Bitmap bmp: checkedBitmaps){
            if(left >= horizontalCoef * bmp.getWidth()){
                left = 0;
                top += bmp.getHeight();
            }
            if(top >= 999 /* :) */ * bmp.getHeight())
                top = 0;
            canvas.drawBitmap(bmp, left, top, null);
            left += bmp.getWidth();
        }

        collage = bitmap;
        return collage;
    }


}
