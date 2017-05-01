package com.devsoul.dima.kindergarten.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.app.AppConfig;
import com.devsoul.dima.kindergarten.app.AppController;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Teacher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parent Main Activity screen
 */
public class ParentActivity extends Activity
{
    private static final String TAG = ParentActivity.class.getSimpleName();

    private Button btnContact;

    private ProgressDialog pDialog;
    private SQLiteHandler db;

    private Teacher Nanny;
    private KinderGan Gan;

    private HashMap<String, String> parent;
    private HashMap<String, String> kid;

    //An ArrayLists for Dialog Items
    private ArrayList<String> FULLNAME_LIST;

    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        btnContact = (Button) findViewById(R.id.btn_Contact);

        //Initializing the ArrayList
        FULLNAME_LIST = new ArrayList<String>();

        Nanny = new Teacher();
        Gan = new KinderGan();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching parent details from SQLite parents table
        parent = db.getParentDetails();
        // Fetching kid details from SQLite kids table
        kid = db.getKidDetails();

        LoadTeachers();

        btnContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Load the child's nannies from SQLite
                getTeacherNames();
                ShowAlertDialogNamesWithListview();
            }
        });
    }

    /**
     * Function to load Teachers from MySQL database to SQLite,
     * will post all params to login url
     */
    private void LoadTeachers()
    {
        // Tag used to cancel the request
        String tag_string_req = "teachers_request";

        pDialog.setMessage("Loading teachers data ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "teachers Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        // Teachers successfully loaded from MySQL
                        // Now store the Teachers in SQLite
                        JSONArray teachers = jObj.getJSONArray("Teachers");
                        for (int i = 0; i < teachers.length(); i++)
                        {
                            JSONObject teacher = teachers.getJSONObject(i);
                            Nanny.SetID(teacher.getString("ID"));
                            Nanny.SetFirstName(teacher.getString("first_name"));
                            Nanny.SetLastName(teacher.getString("last_name"));
                            Nanny.SetPhone(teacher.getString("phone"));
                            Gan.SetName(teacher.getString("kindergan_name"));
                            Nanny.SetClass(teacher.getString("kindergan_class"));
                            Nanny.SetEmail(teacher.getString("email"));

                            // Inserting row in teachers table
                            db.addTeacher(Nanny, Gan);
                        }
                    }
                    else
                    {
                        // Error occurred in registration. Get the error message
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
                params.put("tag", "get_teachers");
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

    /**
     * Load the child's nannies from SQLite
     */
    private void getTeacherNames()
    {
        FULLNAME_LIST = db.getTeacherNames(parent.get(db.KEY_ID), kid.get(db.KEY_KINDERGAN_NAME), kid.get(db.KEY_CLASS));
        Log.d(TAG, "Nannies List: " + FULLNAME_LIST);
    }

    /**
     * Show alert dialog with the names of Nannies of the kid
     */
    public void ShowAlertDialogNamesWithListview()
    {
        //Create sequence of items
        final CharSequence[] Options = FULLNAME_LIST.toArray(new String[FULLNAME_LIST.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Names");
        dialogBuilder.setItems(Options, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                // Selected item in alert dialog
                String selectedText = Options[item].toString();
                // Open alert dialog of contacts
                ShowAlertDialogWithListview(selectedText);
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    /**
     * Show alert dialog with options to contact: Call, SMS, Email
     * @param TeacherFullName - The selected teacher name
     */
    public void ShowAlertDialogWithListview(String TeacherFullName)
    {
        List<String> mOptions = new ArrayList<String>();
        mOptions.add("Call");
        mOptions.add("Send SMS");
        mOptions.add("Send email");

        // Split the full name to first name and last name by - sign
        String[] parts = TeacherFullName.split(" - ");
        final String FirstName = parts[0]; // First name
        final String LastName = parts[1]; // Last name

        ArrayList<String> Contact = new ArrayList<>();
        Contact = db.getTeacherContact(FirstName, LastName, kid.get(db.KEY_KINDERGAN_NAME), kid.get(db.KEY_CLASS));
        phone_number = Contact.get(0);
        final String email = Contact.get(1);

        //Create sequence of items
        final CharSequence[] Options = mOptions.toArray(new String[mOptions.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Contact");
        dialogBuilder.setItems(Options, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                switch (item)
                {
                    // Phone call
                    case 0:
                    {
                        callPhoneNumber(phone_number);
                        break;
                    }
                    // Send sms
                    case 1:
                    {
                        sendSMS(phone_number);
                        break;
                    }
                    // Send email
                    case 2:
                    {
                        sendEmail(email);
                        break;
                    }
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
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
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , number);

        try
        {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(ParentActivity.this,
                           "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendEmail(String recipient)
    {
        Log.i("Send email", "");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipient });

        try
        {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(ParentActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
