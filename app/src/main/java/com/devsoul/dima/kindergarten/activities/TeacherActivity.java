package com.devsoul.dima.kindergarten.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.fabbo.FabOptions;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Teacher Main Activity screen
 */
public class TeacherActivity extends AppCompatActivity
{
    /*
    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    */
    private TextView txtGan;
    private TextView txtCls;
    private CircleImageView imageView;
    private FabOptions mFabOptions;
    private ImageView call;
    private ImageView sms;
    EditText msg;
    private SQLiteHandler db;
/*
    public static Intent newStartIntent(Context context) {
        return new Intent(context, TeacherActivity.class);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        mFabOptions = (FabOptions) findViewById(R.id.fab_options);

        txtGan = (TextView) findViewById(R.id.ganName);
        txtCls = (TextView) findViewById(R.id.clsNum);
        imageView = (CircleImageView) findViewById(R.id.baby1);

        db = new SQLiteHandler(getApplicationContext());

        final ImageView frame = (ImageView) findViewById(R.id.frame);
        final ImageView BtnClose = (ImageView) findViewById(R.id.BtnClose);
        final ImageView BtnCall = (ImageView) findViewById(R.id.BtnCall);
        final ImageView BtnSms = (ImageView) findViewById(R.id.BtnSms);
        final ImageView BtnDetails = (ImageView) findViewById(R.id.BtnDetails);
        final ImageView missBtn = (ImageView) findViewById(R.id.missBtn);
        final ImageView arrvBtn = (ImageView) findViewById(R.id.arrvBtn);
        final CircleImageView b1 = (CircleImageView) findViewById(R.id.baby1);

        // Baby 1
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                frame.setVisibility(View.VISIBLE);
                BtnClose.setVisibility(View.VISIBLE);
                BtnCall.setVisibility(View.VISIBLE);
                BtnSms.setVisibility(View.VISIBLE);
                BtnDetails.setVisibility(View.VISIBLE);

                if (b1.getBorderColor()== getResources().getColor(R.color.color2))
                {
                    missBtn.setVisibility(View.GONE);
                    arrvBtn.setVisibility(View.VISIBLE);
                }
                else
                {
                    missBtn.setVisibility(View.VISIBLE);
                    arrvBtn.setVisibility(View.GONE);
                }

                missBtn.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        missBtn.setVisibility(View.GONE);
                        arrvBtn.setVisibility(View.VISIBLE);
                        b1.setBorderColor(getResources().getColor(R.color.color2));
                    }
                });

                arrvBtn.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v){
                        missBtn.setVisibility(View.VISIBLE);
                        arrvBtn.setVisibility(View.GONE);
                        b1.setBorderColor(getResources().getColor(R.color.green));
                    }
                });
            }
        });

        // Close button
        BtnClose.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                frame.setVisibility(View.GONE);
                BtnClose.setVisibility(View.GONE);
                BtnCall.setVisibility(View.GONE);
                BtnSms.setVisibility(View.GONE);
                BtnDetails.setVisibility(View.GONE);
                missBtn.setVisibility(View.GONE);
                arrvBtn.setVisibility(View.GONE);
            }
        });

        // Menu options
        mFabOptions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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
        });
    }

}
