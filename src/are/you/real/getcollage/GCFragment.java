package are.you.real.getcollage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by AreYouReal on 17/01/14.
 */
public class GCFragment extends Fragment {
    private static final String TAG = "GCFragment";
    public  static final String ARG_PAGE = "page";

    private static Context mContext;
    private static Handler mHandler;
    private static GCImageAdapter mImageAdapter;
    private static ImageView im;

    private int numOfPage;

    public static void init(Context context, Handler handler, GCImageAdapter imageAdapter){
        mHandler = handler;
        mContext = context;
        mImageAdapter = imageAdapter;
    }


    public static GCFragment create(int page){
        GCFragment fragment = new GCFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numOfPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (numOfPage){
            case 0:
                WebView wv;
                wv = new WebView(mContext);
                wv.setWebViewClient(new GCWebViewClient(mContext));
                wv.getSettings().setJavaScriptEnabled(true);
                wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                wv.loadUrl("https://instagram.com/oauth/authorize/?client_id=" + GCPreferences.CLIENT_ID +  "&redirect_uri=" + GCPreferences.CALLBACK_URL + "&response_type=token"); // TODO: WTF? This code crushs my app:))
                //wv.loadUrl("https://instagram.com/accounts/login/?next=/oauth/authorize/%3Fclient_id%3Dc34062307c0d4594bb3830eaab09488a%26redirect_uri%3Dinstagram%3A//connect%26response_type%3Dtoken"); // TODO: And this doesn't:)

                Log.v(TAG, "Container " + container);
                return wv;

            case 1:
                ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.user_selecting_screen, container, false);
                final EditText usernameText = (EditText)rootView.findViewById(R.id.user_name);
                Button btn = (Button) rootView.findViewById(R.id.button);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(usernameText.getText().length() <= 1)
                            Toast.makeText(mContext, R.string.error_too_short_username, Toast.LENGTH_SHORT).show();
                        else{
                            GCSession.getUserId(usernameText.getText().toString().trim());
                            Bundle b = new Bundle();
                            b.putInt(GCMainActivity.RESULT, -1);
                            Message msg = new Message();
                            msg.setData(b);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
                return rootView;
            case 2:
                ViewGroup collageView = (ViewGroup) inflater.inflate(R.layout.best_images_screen, container, false);
                GridView grid = (GridView) collageView.findViewById(R.id.collage_grid);
                grid.setAdapter(mImageAdapter);
                grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        GCPreferences.setCheckedGridCell(position, !GCPreferences.getCheckedCell(position));
                        Log.d(TAG, position + "\t" + view + "\t" + GCPreferences.getCheckedCell(position));
                        mImageAdapter.notifyDataSetChanged();
                        return false;
                    }
                });
                Button create_btn = (Button) collageView.findViewById(R.id.create_collage_btn);
                create_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GCCollageCreator.clear();
                        Bitmap bmp = GCCollageCreator.createCollage();
                        Log.d(TAG, "" + bmp);
                        if(im != null){
                            im.setImageBitmap(bmp);
                            im.invalidate();
                            Log.d(TAG, "Set bmp and invalidate");
                        }
                        Bundle b = new Bundle();
                        b.putInt(GCMainActivity.RESULT, 3);
                        Message msg = new Message();
                        msg.setData(b);
                        mHandler.sendMessage(msg);
                    }
                });
                return collageView;

            case 3:
                ViewGroup collage = (ViewGroup) inflater.inflate(R.layout.collage_screen, container, false);
                im = (ImageView) collage.findViewById(R.id.collage_image);
                if(GCCollageCreator.createCollage() != null)
                    im.setImageBitmap(GCCollageCreator.createCollage());
                Button button = (Button) collage.findViewById(R.id.sen_to_mail_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(GCCollageCreator.createCollage() == null){
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.nothing_to_send), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        File  mFile = savebitmap(GCCollageCreator.createCollage());

                        Uri u = null;
                        u = Uri.fromFile(mFile);

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("image/*");
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello...");
                        // + "\n\r" + "\n\r" +
                        // feed.get(Selectedposition).DETAIL_OBJECT.IMG_URL
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your tsxt here");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, u);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                return collage;
            default:
                return new Button(mContext);
        }
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "sendCollage.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory,"sendCollage.png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}
