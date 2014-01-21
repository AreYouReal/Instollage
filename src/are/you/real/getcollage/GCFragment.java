package are.you.real.getcollage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
    private static ImageView collageView, bestImagesStub, collageStub;

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
            case 3:
                ViewGroup loggingView = (ViewGroup) inflater.inflate(R.layout.logging_page, container, false);
                WebView wv = (WebView) loggingView.findViewById(R.id.webView);
                wv.setWebViewClient(new GCWebViewClient());
                wv.getSettings().setJavaScriptEnabled(true);
                wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                wv.loadUrl("https://instagram.com/oauth/authorize/?client_id=" + GCPreferences.CLIENT_ID +  "&redirect_uri=" + GCPreferences.CALLBACK_URL + "&response_type=token"); // TODO: WTF? This code crushs my app:))
                //wv.loadUrl("https://instagram.com/accounts/login/?next=/oauth/authorize/%3Fclient_id%3Dc34062307c0d4594bb3830eaab09488a%26redirect_uri%3Dinstagram%3A//connect%26response_type%3Dtoken"); // TODO: And this doesn't:)
                return loggingView;

            case 0:
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
                            mHandler.sendEmptyMessage(GCPreferences.MSG_FETCHING_USER_INFO_START);
                            if(bestImagesStub != null) bestImagesStub.setVisibility(View.INVISIBLE);

                        }
                    }
                });
                return rootView;
            case 1:
                ViewGroup bestImagesView = (ViewGroup) inflater.inflate(R.layout.best_images_screen, container, false);
                GridView grid = (GridView) bestImagesView.findViewById(R.id.collage_grid);
                bestImagesStub = (ImageView) bestImagesView.findViewById(R.id.best_images_image);
                grid.setAdapter(mImageAdapter);
                Log.d(TAG, bestImagesStub + "\t" + mImageAdapter.getCount());
                if(GCPreferences.isAnyImageIsDownloaded()){
                    if(bestImagesStub != null) bestImagesStub.setVisibility(View.INVISIBLE);
                }else{
                    if(bestImagesStub != null) bestImagesStub.setVisibility(View.VISIBLE);
                }
                grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        GCPreferences.setCheckedGridCell(position, !GCPreferences.getCheckedCell(position));
                        if(GCPreferences.getCheckedCell(position))
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue_light));
                        else
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                        return false;
                    }
                });
                Button create_btn = (Button) bestImagesView.findViewById(R.id.create_collage_btn);
                create_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GCCollageCreator.clear();
                        Bitmap bmp = GCCollageCreator.createCollage();
                        if(GCFragment.collageView != null){
                            Log.d(TAG, "" + bmp);
                            GCFragment.collageView.setImageBitmap(bmp);
                            GCFragment.collageView.invalidate();
                        }else{
                            collageStub.setVisibility(View.VISIBLE);
                        }
                        mHandler.sendEmptyMessage(GCPreferences.MSG_TURN_TO_THIRD_PAGE);
                        collageStub.setVisibility(View.INVISIBLE);
                    }
                });
                return bestImagesView;

            case 2:
                ViewGroup collageView = (ViewGroup) inflater.inflate(R.layout.collage_screen, container, false);
                GCFragment.collageView = (ImageView) collageView.findViewById(R.id.collage_image);
                collageStub = (ImageView) collageView.findViewById(R.id.collage_image_stub);
                if(GCCollageCreator.createCollage() != null){
                    GCFragment.collageView.setImageBitmap(GCCollageCreator.createCollage());
                    if(collageStub != null) collageStub.setVisibility(View.INVISIBLE);
                }
                else{
                    if(collageStub != null) collageStub.setVisibility(View.VISIBLE);
                }
                Button button = (Button) collageView.findViewById(R.id.sen_to_mail_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(){
                            @Override
                            public void run() {
                                if(GCCollageCreator.createCollage() == null){
                                    /** Nothing to send :( */
                                    mHandler.sendEmptyMessage(GCPreferences.MSG_NOTHING_TO_SEND);
                                    return;
                                }
                                mHandler.sendEmptyMessage(GCPreferences.MSG_SAVING_COLLAGE_TO_SD_CARD);

                                File  mFile = savebitmap(GCCollageCreator.createCollage());

                                if(mFile == null){
                                    mHandler.sendEmptyMessage(GCPreferences.MSG_ERROR);
                                    return;
                                }

                                Uri u = null;
                                u = Uri.fromFile(mFile);
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("image/*");
                                // TODO: String resource need to be added
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello...");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Your text here");
                                emailIntent.putExtra(Intent.EXTRA_STREAM, u);
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));

                                mHandler.sendEmptyMessage(GCPreferences.MSG_PROGRESS_DIALOG_DISMISS);

                            }
                        }.start();

                    }
                });
                return collageView;
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
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
        return file;
    }
}
