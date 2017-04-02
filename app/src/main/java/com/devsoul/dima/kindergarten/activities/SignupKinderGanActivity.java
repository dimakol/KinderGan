package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.app.AppConfig;
import com.devsoul.dima.kindergarten.app.AppController;
import com.devsoul.dima.kindergarten.helper.BitmapHandler;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.helper.SessionManager;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Teacher;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Signup KinderGan Activity enables the user that is a teacher\parent to create an account in the application.
 */
public class SignupKinderGanActivity extends Activity
{
    private static final String TAG = SignupKinderGanActivity.class.getSimpleName();
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)" +        // must contains one digit from 0-9
                    "(?=.*[a-z])" +       // must contains one lowercase characters
                    "(?=.*[A-Z])" +       // must contains one uppercase characters
                    "(?=.*[!@#$%])" +     // must contains one special symbols in the list "!@#$%"
                    "(?!.*\\s)" +         // disallow spaces
                    ".{8,15})";           // length at least 8 characters and maximum of 15

    @InjectView(R.id.input_kinderganname) EditText inputKinderGanName;
    @InjectView(R.id.input_kinderganaddress) EditText inputKinderGanAddress;
    @InjectView(R.id.input_kinderganclass) EditText inputKinderGanClass;
    @InjectView(R.id.input_email) EditText inputEmail;
    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.link_login) TextView btnLinkToLogin;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private BitmapHandler bmpHandler;

    private Teacher Nanny;
    private KinderGan Gan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_kinder_gan);

        // Back button
        ImageButton img_btnBack = (ImageButton) findViewById(R.id.img_btn_back);
        // Register button
        ImageButton img_btnRegister = (ImageButton) findViewById(R.id.img_btn_signup);

        // Inject the ButterKnife design
        ButterKnife.inject(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {
            // User is already logged in. Take him to user activity
            Intent intent = new Intent(SignupKinderGanActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        // To retrieve objects in current activity
        Nanny = (Teacher) getIntent().getSerializableExtra("teacher");
        Gan = (KinderGan) getIntent().getSerializableExtra("kindergan");
        if (Gan != null)
        // Has object of Gan from previous activity (SignUpTeacher Activity)
        {
            // Load fields of KinderGan
            LoadFields();
        }

        // Previous page Button Click event
        img_btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Save all the fields in current page
                String KinderGan_name = inputKinderGanName.getText().toString().trim();
                String KinderGan_address = inputKinderGanAddress.getText().toString().trim();
                String teacher_class = inputKinderGanClass.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                Gan = new KinderGan(KinderGan_name, KinderGan_address);
                Nanny.SetClass(teacher_class);
                Nanny.SetEmail(email);
                Nanny.SetPassword(password);

                // Go to previous page of registration (teacher Info)
                Intent i = new Intent(SignupKinderGanActivity.this, SignupTeacherActivity.class);
                //To pass object of teacher to previous activity
                i.putExtra("teacher", Nanny);
                //To pass object of gan to previous activity
                i.putExtra("kindergan", Gan);
                startActivity(i);
                finish();
            }
        });

        // Sign up Button Click event
        img_btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signup();
            }
        });

        // Link to login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SignupKinderGanActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * This function load the fields of KinderGan in current activity
     */
    public void LoadFields()
    {
        inputKinderGanName.setText(Gan.GetName());
        inputKinderGanAddress.setText(Gan.GetAddress());
        inputKinderGanClass.setText(Nanny.GetClass());
        inputEmail.setText(Nanny.GetEmail());
        inputPassword.setText(Nanny.GetPassword());
    }

    /**
     * This function performs the sign up operation.
     */
    public void signup()
    {
        Log.d(TAG, "SignupTeacher");

        String KinderGan_name = inputKinderGanName.getText().toString().trim();
        String KinderGan_address = inputKinderGanAddress.getText().toString().trim();
        String teacher_class = inputKinderGanClass.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        Gan = new KinderGan(KinderGan_name, KinderGan_address);
        Nanny.SetClass(teacher_class);
        Nanny.SetEmail(email);
        Nanny.SetPassword(password);

        // One of the fields is invalid
        if (!validate())
        {
            onSignupFailed("Sign up failed");
            return;
        }

        bmpHandler = new BitmapHandler(getApplicationContext());
        registerUser();
    }

    /**
     * This function shows a message to the user that the sign up has failed.
     */
    public void onSignupFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * This is a validation function that checks all the fields.
     * @return boolean - This returns true if all the fields are valid, false if one of the fields is invalid.
     */
    public boolean validate()
    {
        boolean valid = true;

        // KinderGan Name validation
        if (Gan.GetName().isEmpty())
        {
            inputKinderGanName.setError("Enter KinderGan name where you work !");
            valid = false;
        }
        else
        {
            inputKinderGanName.setError(null);
        }

        // KinderGan Address validation
        if (Gan.GetAddress().isEmpty())
        {
            inputKinderGanAddress.setError("Enter KinderGan address where you work !");
            valid = false;
        }
        else
        {
            inputKinderGanAddress.setError(null);
        }

        // Class validation
        if (Nanny.GetClass().isEmpty() || Nanny.GetClass().length() > 1)
        {
            inputKinderGanClass.setError("Enter KinderGan number of class where you work !");
            valid = false;
        }
        else
        {
            inputKinderGanClass.setError(null);
        }

        // Email validation
        if (Nanny.GetEmail().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Nanny.GetEmail()).matches())
        {
            inputEmail.setError("Enter a valid email address !");
            valid = false;
        }
        else
        {
            inputEmail.setError(null);
        }

        // Password validation
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(Nanny.GetPassword()).matches())
        {
            inputPassword.setError("Password must be at least 8 characters.\n" +
                    "Use numbers, symbols and mix of upper and lower case letters !");
            valid = false;
        }
        else
        {
            inputPassword.setError(null);
        }

        return valid;
    }

    /**
     * Function to store user in MySQL database,
     * will post all params to register url
     */
    private void registerUser()
    {
        // Tag used to cancel the request
        String tag_string_req = "register_request";

        pDialog.setMessage("Creating Account ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.REGISTER_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "register Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        /*
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("firstname") + " " + user.getString("lastname");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);
                        */

                        // Teacher user successfully stored in MySQL
                        // Now store the teacher user in sqlite
                        JSONObject user = jObj.getJSONObject("user");
                        Nanny.SetID(user.getString("ID"));
                        Nanny.SetFirstName(user.getString("firstname"));
                        Nanny.SetLastName(user.getString("lastname"));
                        Nanny.SetPhone(user.getString("phone"));
                        Nanny.SetPicture(user.getString("photo"));
                        Gan.SetName(user.getString("kindergan_name"));
                        Gan.SetAddress(user.getString("kindergan_address"));
                        Nanny.SetClass(user.getString("kindergan_class"));
                        Nanny.SetEmail(user.getString("email"));
                        Nanny.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in teachers table
                        db.addTeacher(Nanny, Gan);

                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupKinderGanActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        onSignupFailed(errorMsg);
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                onSignupFailed(error.getMessage());
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Posting parameters to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("ID", Nanny.GetID());
                params.put("First_Name", Nanny.GetFirstName());
                params.put("Last_Name", Nanny.GetLastName());
                params.put("Phone", Nanny.GetPhone());
                //Converting Bitmap to String
                String image = bmpHandler.getStringImage(bmpHandler.decodeSampledBitmapFromStream(Uri.parse(Nanny.GetPicture()), 300, 300));
                params.put("Picture", image);
                params.put("KinderGan_Name", Gan.GetName());
                params.put("KinderGan_Address", Gan.GetAddress());
                params.put("Class", Nanny.GetClass());
                params.put("Email", Nanny.GetEmail());
                params.put("Password", Nanny.GetPassword());

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
}
