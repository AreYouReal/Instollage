package are.you.real.getcollage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GCMainActivity extends Activity {

    private GCApp       mApp;
    private Button      btnConnect;
    private TextView    tvSummary;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mApp = new GCApp(this, GCAppStaticData.CLIENT_ID, GCAppStaticData.CLIENT_SECRET_KEY, GCAppStaticData.CALLBACK_URL);
        mApp.setListener(listener);

        tvSummary = (TextView) findViewById(R.id.tv_summary);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mApp.hasAccessToken()){
                    final AlertDialog.Builder builder= new AlertDialog.Builder(GCMainActivity.this);
                    builder.setMessage("Disconnect from Instagram?").setCancelable(false);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mApp.resetAccessToken();
                            btnConnect.setText("Connect");
                            tvSummary.setText("Not connected");
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    mApp.authorize();
                }
            }
        });

        if(mApp.hasAccessToken()){
            tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
        }

    }


    GCApp.GCOAuthListener listener = new GCApp.GCOAuthListener() {
        @Override
        public void onSuccess() {
            tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
        }

        @Override
        public void onFail(String error) { Toast.makeText(GCMainActivity.this, error, Toast.LENGTH_SHORT).show(); }
    };
}
