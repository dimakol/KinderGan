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
import android.widget.*;
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
import com.devsoul.dima.kindergarten.helper.jobs.ShowNotificationJob;
import com.devsoul.dima.kindergarten.model.Kid;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Parent;
import com.devsoul.dima.kindergarten.model.Teacher;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = UserActivity.class.getSimpleName();

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

    private Kid Child;
    private Parent Prnt;
    private KinderGan Gan;
    private Teacher Nanny;

    private HashMap<String, String> kid;
    private HashMap<String, String> user;

    //An ArrayLists for Dialog Items
    private ArrayList<String> FULLNAME_LIST;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        imageView = (CircleImageView) findViewById(R.id.circle_profile);
        btnEnter = (ImageButton) findViewById(R.id.btnEnter);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnContacts = (ImageButton) findViewById(R.id.btnContacts);
        btnPick = (ImageButton) findViewById(R.id.btnPick);
        btnSpecial = (ImageButton) findViewById(R.id.btnSpecial);

        Child = new Kid();
        Prnt = new Parent();
        Gan = new KinderGan();
        Nanny = new Teacher();

        FULLNAME_LIST = new ArrayList<String>();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        String path;

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
            case 2:
            {
                // Fetching user details from SQLite parents table
                user = db.getParentDetails();

                // Fetching kid details from SQLite kids table
                kid = db.getKidDetails();
                path = kid.get(db.KEY_PHOTO);

                if (db.getRowCount(SQLiteHandler.TABLE_TEACHERS) == 0)
                {
                    LoadTeachers(user.get(db.KEY_ID), kid.get(db.KEY_KINDERGAN_NAME), kid.get(db.KEY_CLASS));
                }

                btnEnter.setVisibility(View.GONE);
                btnContacts.setVisibility(View.VISIBLE);
                btnPick.setVisibility(View.VISIBLE);
                btnSpecial.setVisibility(View.VISIBLE);
                break;
            }
            default:
            {
                return;
            }
        }

        String name = user.get(db.KEY_FIRST_NAME) + " " + user.get(db.KEY_LAST_NAME);
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
                if ((db.getRowCount(SQLiteHandler.TABLE_KIDS) == 0) && (db.getRowCount(SQLiteHandler.TABLE_PARENTS) == 0))
                {
                    // Load kids that belong to specific teacher from MySQL
                    LoadKids(user.get(db.KEY_KINDERGAN_NAME), user.get(db.KEY_KINDERGAN_CLASS));
                }
                else
                {
                    // Go to teacher activity
                    Intent intent = new Intent(UserActivity.this, TeacherActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_pick,null);
                final EditText mPerson1 = (EditText) mView.findViewById(R.id.person1);
                final EditText mPerson2 = (EditText) mView.findViewById(R.id.person2);
                final EditText mPerson3 = (EditText) mView.findViewById(R.id.person3);
                Button mEdit = (Button) mView.findViewById(R.id.EditBtn);

                // Have already contact1
                if (!kid.get(db.KEY_CONTACT1).contentEquals("null"))
                {
                    // Set contact1 from SQLite to edit text
                    mPerson1.setText(kid.get(db.KEY_CONTACT1));
                }

                // Have already contact2
                if (!kid.get(db.KEY_CONTACT2).contentEquals("null"))
                {
                    // Set contact2 from SQLite to edit text
                    mPerson2.setText(kid.get(db.KEY_CONTACT2));
                }

                // Have already contact3
                if (!kid.get(db.KEY_CONTACT3).contentEquals("null"))
                {
                    // Set contact3 from SQLite to edit text
                    mPerson3.setText(kid.get(db.KEY_CONTACT3));
                }

                mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update kid contact1
                        kid.put(db.KEY_CONTACT1, mPerson1.getText().toString());
                        // Update kid contact2
                        kid.put(db.KEY_CONTACT2, mPerson2.getText().toString());
                        // Update kid contact3
                        kid.put(db.KEY_CONTACT3, mPerson3.getText().toString());
                        // Update in SQLite contacts columns in kid table
                        db.UpdateKidContacts(user.get(db.KEY_ID), kid.get(db.KEY_CONTACT1), kid.get(db.KEY_CONTACT2), kid.get(db.KEY_CONTACT3));
                        // Update contacts columns in MySQL
                        UpdateKidContacts();
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

                // Have already a special request
                if (!kid.get(db.KEY_SPECIAL).contentEquals("null"))
                {
                    // Set special request from SQLite to edit text
                    sTxt.setText(kid.get(db.KEY_SPECIAL));
                }

                mEdit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update kid special request
                        kid.put(db.KEY_SPECIAL, sTxt.getText().toString());
                        // Update in SQLite special column in kid table
                        db.UpdateKidSpecialRequest(user.get(db.KEY_ID), kid.get(db.KEY_SPECIAL));
                        // Update column in MySQL
                        UpdateSpecialRequest();
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
     * Function to load Kids and Parents that belong to specific Teacher from MySQL database to SQLite,
     * will post all params to login url
     * @param TeacherKinderGanName
     * @param TeacherKinderGanClass
     */
    private void LoadKids(final String TeacherKinderGanName, final String TeacherKinderGanClass)
    {
        // Tag used to cancel the request
        String tag_string_req = "kids_request";

        pDialog.setMessage("Loading kids data ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "kids Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        // Kids successfully loaded from MySQL
                        // Now store the Kids in SQLite
                        JSONArray kids = jObj.getJSONArray("Kids");
                        for (int i = 0; i < kids.length(); i++)
                        {
                            JSONObject kid = kids.getJSONObject(i);
                            Child.SetName(kid.getString("name"));
                            Child.SetBirthDate(kid.getString("birth_date"));
                            Child.SetPicture(kid.getString("photo"));
                            Gan.SetName(kid.getString("kindergan_name"));
                            Child.SetClass(kid.getString("class"));
                            Child.SetParentID(kid.getString("parent_id"));

                            Child.SetPresence(kid.getString("presence"));
                            Child.SetSpecial(kid.getString("special"));
                            Child.SetContact1(kid.getString("contact1"));
                            Child.SetContact2(kid.getString("contact2"));
                            Child.SetContact3(kid.getString("contact3"));

                            // Inserting row in kids table
                            db.addKid(Child, Gan);
                        }

                        // Parents successfully loaded from MySQL
                        // Now store the Parents in SQLite
                        JSONArray parents = jObj.getJSONArray("Parents");
                        for (int i = 0; i < parents.length(); i++)
                        {
                            JSONObject parent = parents.getJSONObject(i);
                            Prnt.SetID(parent.getString("ID"));
                            Prnt.SetFirstName(parent.getString("first_name"));
                            Prnt.SetLastName(parent.getString("last_name"));
                            Prnt.SetAddress(parent.getString("address"));
                            Prnt.SetPhone(parent.getString("phone"));
                            Prnt.SetEmail(parent.getString("email"));

                            // Inserting row in parents table
                            db.addParent(Prnt);
                        }

                        // Go to teacher activity
                        Intent intent = new Intent(UserActivity.this,TeacherActivity.class);
                        startActivity(intent);
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
                params.put("tag", "get_kids");
                params.put("KinderGan_Name", TeacherKinderGanName);
                params.put("KinderGan_Class", TeacherKinderGanClass);
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
     * Function to load Teachers of specific kid from MySQL database to SQLite,
     * will post all params to login url
     * @param ParentID
     * @param KidKinderGanName
     * @param KidKinderGanClass
     */
    private void LoadTeachers(final String ParentID, final String KidKinderGanName, final String KidKinderGanClass)
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
                            Nanny.SetNotificationTime(teacher.getString("notification_time"));

                            // Inserting row in teachers table
                            db.addTeacher(Nanny, Gan);
                        }

                        // The push up notification if kid isn't arrived to kindergarten
                        Notification();
                    }
                    else
                    {
                        // Error occurred in loading. Get the error message
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
                params.put("ID", ParentID);
                params.put("KinderGan_Name", KidKinderGanName);
                params.put("KinderGan_Class", KidKinderGanClass);
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
     * Function to update kid special request in MySQL,
     * will post all params to login url
     */
    private void UpdateSpecialRequest()
    {
        // Tag used to cancel the request
        String tag_string_req = "special_request";

        pDialog.setMessage("Updating kid special request ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "special request Response: " + response.toString());
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
                params.put("tag", "update_special_request");
                params.put("parent_id", user.get(db.KEY_ID));
                params.put("special", kid.get(db.KEY_SPECIAL));
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
     * Function to update kid contacts in MySQL,
     * will post all params to login url
     */
    private void UpdateKidContacts()
    {
        // Tag used to cancel the request
        String tag_string_req = "kidcontacts_request";

        pDialog.setMessage("Updating kid pickup list ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "kid contact list Response: " + response.toString());
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
                params.put("tag", "update_pickup_list");
                params.put("parent_id", user.get(db.KEY_ID));
                params.put("contact1", kid.get(db.KEY_CONTACT1));
                params.put("contact2", kid.get(db.KEY_CONTACT2));
                params.put("contact3", kid.get(db.KEY_CONTACT3));
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
        FULLNAME_LIST = db.getTeacherNames();
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

        ArrayList<String> Contact;
        Contact = db.getTeacherContact(FirstName, LastName);
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
            Toast.makeText(UserActivity.this,
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
            Toast.makeText(UserActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Performs the entire alert process
     */
    public void Notification()
    {
        // Kid doesn't presence in kindergarten
        if (Integer.parseInt(kid.get(db.KEY_PRESENCE)) == 0)
        {
            // get notification time from teacher
            String notification_time = db.getNotificationTime();
            if (notification_time != null)
            {
                // Split time to hours and minutes
                String[] parts = notification_time.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                // Set notification alarm
                ShowNotificationJob.scheduleExact(hours, minutes);
            }
        }
    }
}
