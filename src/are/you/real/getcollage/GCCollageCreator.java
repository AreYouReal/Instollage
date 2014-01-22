package are.you.real.getcollage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alexander on 19/01/14.
 */
public class GCCollageCreator {

    // TODO: Create kind of collages
    private static final String TAG     = "GCCollageCreator";
    private static       Bitmap collage = null;

    public static Bitmap createCollage() {
        if (!(GCPreferences.areAllBestImagesDownloaded() || GCPreferences.isAnyPhotoIsChecked() || GCPreferences.getRequestCounter() == 1))
            return null;
        if (collage != null)
            return collage;

        if (GCPreferences.isAnyPhotoIsChecked()) {
            return (collage = createSelectedCollage(GCPreferences.getSelectedList()));
        }

        ArrayList<Bitmap> bitmaps = GCPreferences.getBmpList();
        if (bitmaps.size() == 0)   // Just in case
            return null;

        int bmpWidth = bitmaps.get(0).getWidth();
        int bmpHeight = bitmaps.get(0).getHeight();

        return (collage = createHxWCollage(bitmaps, 4, 5, bmpWidth, bmpHeight));
    }

    private static Bitmap createSelectedCollage(ArrayList<Bitmap> bitmaps) {
        int size = bitmaps.size();
        Log.d(TAG, "size:" + size);
        if (size == 0) // Just in case
            return null;
        int bmpWidth = bitmaps.get(0).getWidth();
        int bmpHeight = bitmaps.get(0).getHeight();

        switch (size) {
            case 1:
                return createHxWCollage(bitmaps, 1, 1, bmpWidth, bmpHeight);
            case 2:
                return create2x1Collage(bitmaps, bmpWidth, bmpHeight);
            case 3:
                return createHxWCollage(bitmaps, 3, 1, bmpWidth, bmpHeight);
            case 4:
                return create2x2Collage(bitmaps, bmpWidth, bmpHeight);
            case 5:
                return createCollageFrom5thBitmaps(bitmaps, bmpWidth, bmpHeight);
            case 6:
                return createHxWCollage(bitmaps, 3, 2, bmpWidth, bmpHeight);
            case 7:
                return null;
            case 8:
                return createHxWCollage(bitmaps, 2, 4, bmpWidth, bmpHeight);
            case 9:
                return create3x3Collage(bitmaps, bmpWidth, bmpHeight);
            case 10:
                return createHxWCollage(bitmaps, 2, 5, bmpWidth, bmpHeight);
            case 11:
                return null;
            case 12:
                return createHxWCollage(bitmaps, 3, 4, bmpWidth, bmpHeight);
            case 13:
                return null;
            case 14:
                return createHxWCollage(bitmaps, 2, 7, bmpWidth, bmpHeight);
            case 15:
                return createHxWCollage(bitmaps, 3, 5, bmpWidth, bmpHeight);
            case 16:
                return create4x4Collage(bitmaps, bmpWidth, bmpHeight);
            case 17:
                return null;
            case 18:
                return createHxWCollage(bitmaps, 3, 6, bmpWidth, bmpHeight);
            case 19:
                return null;
            case 20:
                return createHxWCollage(bitmaps, 4, 5, bmpWidth, bmpHeight);
            default:
                return null;
        }
    }

    private static Bitmap createHxWCollage(ArrayList<Bitmap> bitmaps, int horizontal, int vertical, int bmpWidth, int bmpHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bmpWidth * horizontal, bmpHeight * vertical, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int left = 0, top = 0;
        for (Bitmap bmp : bitmaps) {
            if (left >= bmpWidth * horizontal) {
                left = 0;
                top += bmpHeight;
            }
            canvas.drawBitmap(bmp, left, top, null);
            left += bmpWidth;
        }
        return bitmap;
    }

    private static Bitmap create3x3Collage(ArrayList<Bitmap> bitmaps, int bmpWidth, int bmpHeight) {
        return createHxWCollage(bitmaps, 3, 3, bmpWidth, bmpHeight);
    }

    private static Bitmap create4x4Collage(ArrayList<Bitmap> bitmaps, int bmpWidth, int bmpHeight) {
        return createHxWCollage(bitmaps, 4, 4, bmpWidth, bmpHeight);
    }

    private static Bitmap create2x2Collage(ArrayList<Bitmap> bitmaps, int bmpWidth, int bmpHeight) {
        return createHxWCollage(bitmaps, 2, 2, bmpWidth, bmpHeight);
    }

    private static Bitmap create2x1Collage(ArrayList<Bitmap> bitmaps, int bmpWidth, int bmpHeight) {
        return createHxWCollage(bitmaps, 2, 1, bmpWidth, bmpHeight);
    }

    private static Bitmap createCollageFrom5thBitmaps(ArrayList<Bitmap> bitmaps, int bmpWidth, int bmpHeight) {
        ArrayList<Bitmap> drawBmpList = new ArrayList<Bitmap>(9);
        Bitmap emptyBmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 4; i++) {
            drawBmpList.add(bitmaps.get(i));
            drawBmpList.add(emptyBmp);
        }
        drawBmpList.add(bitmaps.get(bitmaps.size() - 1));
        return create3x3Collage(drawBmpList, bmpWidth, bmpHeight);
    }

    public static void clear() {
        if (collage != null)
            collage.recycle();
        collage = null;
    }
}
