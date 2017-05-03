package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.devsoul.dima.kindergarten.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.*;
import com.devsoul.dima.kindergarten.fabbo.FabOptions;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.helper.SessionManager;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Teacher Main Activity screen
 */

public class TeacherActivity extends AppCompatActivity implements View.OnClickListener{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private TextView txtGan;
    private TextView txtCls;
    private CircleImageView imageView;
    private FabOptions mFabOptions;
    private ImageView call;
    private ImageView sms;
    EditText msg;
    private SQLiteHandler db;

    public static Intent newStartIntent(Context context) {
        return new Intent(context, TeacherActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        mFabOptions = (FabOptions) findViewById(R.id.fab_options);
        mFabOptions.setOnClickListener(this);
        txtGan = (TextView) findViewById(R.id.ganName);
        txtCls = (TextView) findViewById(R.id.clsNum);
        imageView = (CircleImageView) findViewById(R.id.baby1);
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = null;
        String path = null;
        if (db.getRowCount(db.TABLE_KIDS) > 0)
        {
            // Fetching user details from SQLite Kids table
            user = db.getKidDetails();
            path = user.get(db.KEY_PHOTO);
        }
     /*   String ganName = user.get(db.KEY_KINDERGAN_NAME);
        String ganCls = user.get(db.KEY_CLASS);
        txtGan.setText(ganName);
        txtCls.setText(ganCls);
        Picasso.with(getApplicationContext()).load(path).placeholder(R.drawable.profile).error(R.drawable.profile)
                .into(imageView);
    */

        final ImageView frame = (ImageView) findViewById(R.id.frame);
        final ImageView BtnClose = (ImageView) findViewById(R.id.BtnClose);
        final ImageView BtnCall = (ImageView) findViewById(R.id.BtnCall);
        final ImageView BtnSms = (ImageView) findViewById(R.id.BtnSms);
        final ImageView BtnDetails = (ImageView) findViewById(R.id.BtnDetails);
        final ImageView missBtn = (ImageView) findViewById(R.id.missBtn);
        final ImageView arrvBtn = (ImageView) findViewById(R.id.arrvBtn);
        final CircleImageView b1 = (CircleImageView) findViewById(R.id.baby1);
        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                frame.setVisibility(View.VISIBLE);
                BtnClose.setVisibility(View.VISIBLE);
                BtnCall.setVisibility(View.VISIBLE);
                BtnSms.setVisibility(View.VISIBLE);
                BtnDetails.setVisibility(View.VISIBLE);
                if (b1.getBorderColor()== getResources().getColor(R.color.color2)){
                    missBtn.setVisibility(View.GONE);
                    arrvBtn.setVisibility(View.VISIBLE);
                }
                else {
                    missBtn.setVisibility(View.VISIBLE);
                    arrvBtn.setVisibility(View.GONE);
                }
                missBtn.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        missBtn.setVisibility(View.GONE);
                        arrvBtn.setVisibility(View.VISIBLE);
                        b1.setBorderColor(getResources().getColor(R.color.color2));
                    }
                });
                arrvBtn.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        missBtn.setVisibility(View.VISIBLE);
                        arrvBtn.setVisibility(View.GONE);
                        b1.setBorderColor(getResources().getColor(R.color.green));
                    }
                });
            }
        });
        BtnClose.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                frame.setVisibility(View.GONE);
                BtnClose.setVisibility(View.GONE);
                BtnCall.setVisibility(View.GONE);
                BtnSms.setVisibility(View.GONE);
                BtnDetails.setVisibility(View.GONE);
                missBtn.setVisibility(View.GONE);
                arrvBtn.setVisibility(View.GONE);
            }
        });

        call = (ImageView) findViewById(R.id.BtnCall);
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 123));
                //Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+123));
                startActivity(callIntent);
            }
        });
 /*       sms = (ImageView) findViewById(R.id.BtnSms);
        msg = (EditText) findViewById(R.id.smsText);
        sms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendSms();
            }
        });

    }
    protected void sendSms() {
        String number = "123";
        String message = msg.getText().toString();
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS was sent successfully", Toast.LENGTH_LONG).show();
  */  }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.faboptions_favorite:
                Toast.makeText(TeacherActivity.this, "Favorite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.faboptions_textsms:
                Toast.makeText(TeacherActivity.this, "Message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.faboptions_download:
                Toast.makeText(TeacherActivity.this, "Download", Toast.LENGTH_SHORT).show();
                break;
            case R.id.faboptions_share:
                Toast.makeText(TeacherActivity.this, "Mail Sent", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
    }

}
