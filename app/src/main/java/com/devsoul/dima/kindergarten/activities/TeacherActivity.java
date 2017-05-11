package com.devsoul.dima.kindergarten.activities;

import android.Manifest;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.app.AppConfig;
import com.devsoul.dima.kindergarten.app.AppController;
import com.devsoul.dima.kindergarten.fabbo.FabOptions;
import com.devsoul.dima.kindergarten.fragments.TimePickerFragment;
import com.devsoul.dima.kindergarten.helper.GridViewAdapter;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import de.hdodenhof.circleimageview.CircleImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Teacher Main Activity screen
 */
public class TeacherActivity extends AppCompatActivity implements TimePickerFragment.TimeDialogListener
{
    private static final String TAG = TeacherActivity.class.getSimpleName();
    private static final String DIALOG_TIME = "TeacherActivity.TimeDialog";

    private TextView txtGan;
    private TextView txtCls;
    private FabOptions mFabOptions;

    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private HashMap<String, String> teacher;
    private HashMap<String, String> kid;

    private String KinderGan_Name, KinderGan_Class;

    //An ArrayLists for storing kids path pictures
    private ArrayList<String> KIDPICS_LIST;
    //An ArrayLists for storing kids presence
    private ArrayList<Integer> KIDSPRESENCE_LIST;

    private GridView gridView;

    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        txtGan = (TextView) findViewById(R.id.ganName);
        txtCls = (TextView) findViewById(R.id.clsNum);

        mFabOptions = (FabOptions) findViewById(R.id.fab_options);

        final ImageView frame = (ImageView) findViewById(R.id.frame);
        final ImageView BtnClose = (ImageView) findViewById(R.id.BtnClose);
        final ImageView BtnCall = (ImageView) findViewById(R.id.BtnCall);
        final ImageView BtnSms = (ImageView) findViewById(R.id.BtnSms);
        final ImageView BtnDetails = (ImageView) findViewById(R.id.BtnDetails);
        final ImageView missBtn = (ImageView) findViewById(R.id.missBtn);
        final ImageView arrvBtn = (ImageView) findViewById(R.id.arrvBtn);

        //Initializing the ArrayLists
        KIDPICS_LIST = new ArrayList<String>();
        KIDSPRESENCE_LIST = new ArrayList<Integer>();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        // Fetching teacher details from SQLite teachers table
        teacher = db.getTeacherDetails();
        KinderGan_Name = teacher.get(db.KEY_KINDERGAN_NAME);
        KinderGan_Class = teacher.get(db.KEY_KINDERGAN_CLASS).toString();
        // Set teacher Gan name as label
        txtGan.setText("Gan " + KinderGan_Name);
        // Set teacher Gan class as label
        txtCls.setText("Class " + KinderGan_Class);

        // Load kids pictures from SQLite
        KIDPICS_LIST = db.getKidsPictures();
        // Load kids presence from SQLite
        KIDSPRESENCE_LIST = db.getKidsPresence();

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter(this, KIDPICS_LIST, KIDSPRESENCE_LIST));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id)
            {
                // Open menu
                frame.setVisibility(View.VISIBLE);
                BtnClose.setVisibility(View.VISIBLE);
                BtnCall.setVisibility(View.VISIBLE);
                BtnSms.setVisibility(View.VISIBLE);
                BtnDetails.setVisibility(View.VISIBLE);

                // Get the item
                final CircleImageView item = (CircleImageView) v.findViewById(R.id.grid_item_image);

                // Get image path
                final String image_path = KIDPICS_LIST.get(position);

                // Item frame with red color
                if (item.getBorderColor()== getResources().getColor(R.color.color2))
                {
                    missBtn.setVisibility(View.GONE);
                    arrvBtn.setVisibility(View.VISIBLE);
                }
                // Item frame with green color
                else
                {
                    missBtn.setVisibility(View.VISIBLE);
                    arrvBtn.setVisibility(View.GONE);
                }

                // Miss Button
                missBtn.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        missBtn.setVisibility(View.GONE);
                        arrvBtn.setVisibility(View.VISIBLE);
                        item.setBorderColor(getResources().getColor(R.color.color2));
                        int presence = 0;
                        String ParentID = db.getParentID(image_path);

                        // Update in SQLite presence column in kid table
                        db.UpdateKidPresence(ParentID, presence);
                        // Update column in MySQL
                        UpdatePresence(ParentID, presence);
                        Toast.makeText(TeacherActivity.this,"Presence updated successfully",Toast.LENGTH_SHORT).show();
                    }
                });

                // Arrive Button
                arrvBtn.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v){
                        missBtn.setVisibility(View.VISIBLE);
                        arrvBtn.setVisibility(View.GONE);
                        item.setBorderColor(getResources().getColor(R.color.green));
                        int presence = 1;
                        String ParentID = db.getParentID(image_path);

                        // Update in SQLite presence column in kid table
                        db.UpdateKidPresence(ParentID, presence);
                        // Update column in MySQL
                        UpdatePresence(ParentID, presence);
                        Toast.makeText(TeacherActivity.this,"Presence updated successfully",Toast.LENGTH_SHORT).show();
                    }
                });

                // Call Button
                BtnCall.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        phone_number = db.getParentPhone(image_path);
                        callPhoneNumber(phone_number);
                    }
                });

                // SMS Button
                BtnSms.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        phone_number = db.getParentPhone(image_path);
                        sendSMS(phone_number);
                    }
                });

                // Details Button
                BtnDetails.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TeacherActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_details,null);
                        final TextView KidName = (TextView) mView.findViewById(R.id.Kid_Name);
                        final TextView ParentFulName = (TextView) mView.findViewById(R.id.Parent_FullName);
                        final TextView BirthDate = (TextView) mView.findViewById(R.id.Kid_BirthDate);
                        final TextView Address = (TextView) mView.findViewById(R.id.Address);
                        final TextView Requests = (TextView) mView.findViewById(R.id.Special_Requests);
                        final TextView Contact1 = (TextView) mView.findViewById(R.id.Contact1);
                        final TextView Contact2 = (TextView) mView.findViewById(R.id.Contact2);
                        final TextView Contact3 = (TextView) mView.findViewById(R.id.Contact3);

                        // Get details of kid and his parent
                        kid = db.getDetails(image_path);

                        // Set details to text views
                        KidName.setText("Kid Name: " + kid.get(db.KEY_NAME));
                        ParentFulName.setText("Parent Full Name: " + kid.get(db.KEY_FIRST_NAME) + " " + kid.get(db.KEY_LAST_NAME));
                        BirthDate.setText("Birth Date: " + kid.get(db.KEY_BIRTHDATE));
                        Address.setText("Address: " + kid.get(db.KEY_ADDRESS));

                        // Have already a special request
                        if (!kid.get(db.KEY_SPECIAL).contentEquals("null") && kid.get(db.KEY_SPECIAL).length() != 0)
                        {
                            Requests.setVisibility(View.VISIBLE);
                            Requests.setText("Special Requests: " + kid.get(db.KEY_SPECIAL));
                        }

                        // Have already contact1
                        if (!kid.get(db.KEY_CONTACT1).contentEquals("null") && kid.get(db.KEY_CONTACT1).length() != 0)
                        {
                            Contact1.setVisibility(View.VISIBLE);
                            Contact1.setText("Contact: " + kid.get(db.KEY_CONTACT1));
                        }
                        // Have already contact2
                        if (!kid.get(db.KEY_CONTACT2).contentEquals("null") && kid.get(db.KEY_CONTACT2).length() != 0)
                        {
                            Contact2.setVisibility(View.VISIBLE);
                            Contact2.setText("Contact: " + kid.get(db.KEY_CONTACT2));
                        }
                        // Have already contact3
                        if (!kid.get(db.KEY_CONTACT3).contentEquals("null") && kid.get(db.KEY_CONTACT3).length() != 0)
                        {
                            Contact3.setVisibility(View.VISIBLE);
                            Contact3.setText("Contact: " + kid.get(db.KEY_CONTACT3));
                        }

                        mBuilder.setView(mView);
                        AlertDialog dialogz = mBuilder.create();
                        dialogz.show();
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
                    case R.id.faboptions_notification:
                        //createNotification();
                        TimePickerFragment dialog = new TimePickerFragment();
                        dialog.show(getSupportFragmentManager(), DIALOG_TIME);
                        break;
                    case R.id.faboptions_camera:
                        Toast.makeText(TeacherActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.faboptions_download:
                        Toast.makeText(TeacherActivity.this, "Download", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.faboptions_share:
                        ArrayList<String> emails = db.getEmails();
                        String[] emailsArray = new String[emails.size()];
                        emailsArray = emails.toArray(emailsArray);
                        sendEmail(emailsArray);
                        //Toast.makeText(TeacherActivity.this, "Mail Sent", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }
        });
    }

    public void callPhoneNumber(String number)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        // Permission to call doesn't granted
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            // Request permission to call on runtime
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
        }
        // Permission to call granted
        else
        {
            // Start call
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }
    }

    @Override
    // Request permission to call on runtime
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 101)
        {
            // Permission to call granted
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                callPhoneNumber(phone_number);
            }
            else
            {
                Log.d(TAG, "Call Permission Not Granted");
            }
        }
    }

    protected void sendSMS(String number)
    {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        //smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , number);

        try
        {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(TeacherActivity.this,
                    "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendEmail(String[] recipient)
    {
        Log.i("Send email", "");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient);

        try
        {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(TeacherActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function to update kid presence in MySQL,
     * will post all params to login url
     * @param ParentID
     * @param Presence - 1 Arrived, 0 Missing
     */
    private void UpdatePresence(final String ParentID, final int Presence)
    {
        // Tag used to cancel the request
        String tag_string_req = "presence_request";

        pDialog.setMessage("Updating kid presence ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "kid presence Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error)
                    {
                        // Error occurred in update. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        onLoadFailed(errorMsg);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, "Load Error: " + error.getMessage());
                onLoadFailed(error.getMessage());
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "update_presence");
                params.put("parent_id", ParentID);
                params.put("presence", Integer.toString(Presence));
                return params;
            }
        };

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
        //Volley does retry for you if you have specified the policy.
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * This function shows a message to the user that the load has failed.
     */
    public void onLoadFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show the progress dialog
     */
    private void showDialog()
    {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**
     * Hide the progress dialog
     */
    private void hideDialog()
    {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onFinishDialog(String time)
    {
        Toast.makeText(this, "Selected Time : "+ time, Toast.LENGTH_SHORT).show();
    }

    public void createNotification()
    {
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(this, MissingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Missing child")
                .setContentText("Your child is absent today from kindergarten")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }

}
