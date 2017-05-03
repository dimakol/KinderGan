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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.devsoul.dima.kindergarten.helper.SessionManager;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Teacher;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The User Activity fetching the logged user information from SQLite and displaying it on the screen.
 * The logout button will logout the user by clearing the session and deleting the user from SQLite table.
 */
public class UserActivity extends Activity
{
    private TextView txtName;
    private TextView txtEmail;
    private CircleImageView imageView;
    private ImageButton btnEnter;
    private ImageButton btnLogout;
    private ImageButton btnContacts;
    private ImageButton btnPick;
    private ImageButton btnSpecial;
    private SQLiteHandler db;
    private SessionManager session;

    private ProgressDialog pDialog;
    private SQLiteHandler db2;
    private Teacher Nanny;
    private KinderGan Gan;
    private HashMap<String, String> parent;
    private HashMap<String, String> kid;
    //An ArrayLists for Dialog Items
    private ArrayList<String> FULLNAME_LIST;
    private String phone_number;
    private static final String TAG = ParentActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        btnContacts = (ImageButton) findViewById(R.id.btnContacts);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        imageView = (CircleImageView) findViewById(R.id.circle_profile);
        btnEnter = (ImageButton) findViewById(R.id.btnEnter);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnPick = (ImageButton) findViewById(R.id.btnPick);
        btnSpecial = (ImageButton) findViewById(R.id.btnSpecial);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        FULLNAME_LIST = new ArrayList<String>();

        Nanny = new Teacher();
        Gan = new KinderGan();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db2 = new SQLiteHandler(getApplicationContext());

        // Fetching parent details from SQLite parents table
        parent = db2.getParentDetails();
        // Fetching kid details from SQLite kids table
        kid = db2.getKidDetails();

        if (db2.getRowCount(SQLiteHandler.TABLE_TEACHERS)==0){
            LoadTeachers();

        }
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_pick,null);
                final EditText mPerson1 = (EditText) mView.findViewById(R.id.person1);
                final EditText mPerson2 = (EditText) mView.findViewById(R.id.person2);
                final EditText mPerson3 = (EditText) mView.findViewById(R.id.person3);
                Button mEdit = (Button) mView.findViewById(R.id.EditBtn);
                mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UserActivity.this,"Person added to list successfully",Toast.LENGTH_SHORT).show();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialogz = mBuilder.create();
                dialogz.show();
            }
        });
        btnSpecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_special,null);
                final EditText sTxt = (EditText) mView.findViewById(R.id.sTxt);
                Button mEdit2 = (Button) mView.findViewById(R.id.EditBtn2);
                mEdit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UserActivity.this,"Special requests were added successfully",Toast.LENGTH_SHORT).show();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialogz = mBuilder.create();
                dialogz.show();
            }
        });
        btnContacts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Load the child's nannies from SQLite
                getTeacherNames();
                ShowAlertDialogNamesWithListview();
            }
        });
        // session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        HashMap<String, String> user = null;
        String path = null;

        // Get the user type from session
        switch (session.getType())
        {
            // Teacher type
            case 1:
            {
                // Fetching user details from SQLite teachers table
                user = db.getTeacherDetails();
                path = user.get(db.KEY_PHOTO);
                break;
            }
            // Parent type
            case  2:
            {
                // Fetching user details from SQLite parents table
                user = db.getParentDetails();
                // Fetching kid details from SQLite kids table
                HashMap<String, String> kid;
                kid = db.getKidDetails();
                btnEnter.setVisibility(View.GONE);
                btnContacts.setVisibility(View.VISIBLE);
                btnPick.setVisibility(View.VISIBLE);
                btnSpecial.setVisibility(View.VISIBLE);
                path = kid.get(db.KEY_PHOTO);
                break;
            }
            default:
            {
                return;
            }
        }

        String name = user.get(db.KEY_FIRST_NAME) + " " + user.get(db.KEY_lAST_NAME);
        String email = user.get(db.KEY_EMAIL);

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Show user profile image in circle image view
        Picasso.with(getApplicationContext()).load(path).placeholder(R.drawable.profile).error(R.drawable.profile)
                .into(imageView);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logoutUser();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Teacher
                if (session.getType() == 1)
                {
                    // Go to teacher activity
                    Intent intent = new Intent(UserActivity.this,TeacherActivity.class);
                    startActivity(intent);
                }
                // Parent
                else if (session.getType() == 2)
                {
                    // Go to parent activity
                    Intent intent = new Intent(UserActivity.this,ParentActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Logging out the user.
     * Will set isLoggedIn flag to false in shared preferences, type flag to 0
     * and clears the user data from SQLite users table
     */
    private void logoutUser()
    {
        session.setLogin(false);
        session.setType(0);
        db.deleteTable(db.TABLE_TEACHERS);
        db.deleteTable(db.TABLE_PARENTS);
        db.deleteTable(db.TABLE_KIDS);
        db.deleteTable(db.TABLE_GANS);

        // Launching the login activity
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                            db2.addTeacher(Nanny, Gan);
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
        FULLNAME_LIST = db2.getTeacherNames(parent.get(db2.KEY_ID), kid.get(db2.KEY_KINDERGAN_NAME), kid.get(db2.KEY_CLASS));
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
        Contact = db2.getTeacherContact(FirstName, LastName, kid.get(db2.KEY_KINDERGAN_NAME), kid.get(db2.KEY_CLASS));
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
        smsIntent.setData(Uri.parse("sms:"));
        smsIntent.putExtra("address" , number);

        try
        {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this,
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
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
