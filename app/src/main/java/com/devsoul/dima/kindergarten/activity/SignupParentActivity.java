package com.devsoul.dima.kindergarten.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.app.AppConfig;
import com.devsoul.dima.kindergarten.app.AppController;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.helper.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Signup parent Activity enables the user that is a parent to create an account in the application,
 * and is generally displayed via the link on the Registration Activity.
 */
public class SignupParentActivity extends Activity
{
    private static final String TAG = SignupParentActivity.class.getSimpleName();
    private static final String PASSWORD_PATTERN =
                    "((?=.*\\d)" +        // must contains one digit from 0-9
                    "(?=.*[a-z])" +       // must contains one lowercase characters
                    "(?=.*[A-Z])" +       // must contains one uppercase characters
                    "(?=.*[!@#$%])" +     // must contains one special symbols in the list "!@#$%"
                    "(?!.*\\s)" +         // disallow spaces
                    ".{8,15})";           // length at least 8 characters and maximum of 15

/*    @InjectView(R.id.input_id) EditText inputID;
    @InjectView(R.id.input_fname) EditText inputFirstName;
    @InjectView(R.id.input_lname) EditText inputLastName;
    @InjectView(R.id.input_address) EditText inputAddress;
    @InjectView(R.id.input_phone) EditText inputPhone;
    @InjectView(R.id.input_kidfname) EditText inputKidFirstName;
    @InjectView(R.id.input_kidlname) EditText inputKidLastName;
    @InjectView(R.id.input_kidbirthdate) EditText inputKidBirthDate;
    @InjectView(R.id.input_kidpic) EditText inputKidPic;
    @InjectView(R.id.input_kinderganname) EditText inputKinderGanName;
    @InjectView(R.id.input_kinderganaddress) EditText inputKinderGanAddress;
    @InjectView(R.id.input_kinderganclass) EditText inputKinderGanClass;
    @InjectView(R.id.input_email) EditText inputEmail;
    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.btn_signup) Button btnRegister;
    @InjectView(R.id.link_login) TextView btnLinkToLogin;*/

    // For kid birth date
    private Calendar calendar;
    private int year, month, day;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_parent);
        btnNext = (ImageButton) findViewById(R.id.img_btn_next);


        // Inject the ButterKnife design
        ButterKnife.inject(this);


                // Set kid birth date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nexxt = new Intent(SignupParentActivity.this, SignupParentGanActivity.class);
                startActivity(nexxt);
            }
        });
        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {
            // User is already logged in. Take him to user activity
            Intent intent = new Intent(SignupParentActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }}

        // Kid birth date Button Click event
 /*       inputKidBirthDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDate();
            }
        });

        // Sign up Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener()
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
                Intent i = new Intent(SignupParentActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate()
    {
        // onCreateDialog method called
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == 999)
        {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener()
    {
        *//**
         * Set the date that was chosen
         * @param arg0 - The object
         * @param arg1 - Year
         * @param arg2 - Month
         * @param arg3 - Day
         *//*
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3)
        {
            showDate(arg1, arg2+1, arg3);
        }
    };

    *//**
     * Show the date that was chosen in the kid birth date input text
     * @param year
     * @param month
     * @param day
     *//*
    private void showDate(int year, int month, int day)
    {
        inputKidBirthDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    *//**
     * This function performs the sign up operation.
     *//*
    public void signup()
    {
        Log.d(TAG, "SignupParent");

        String id = inputID.getText().toString().trim();
        String fname = inputFirstName.getText().toString().trim();
        String lname = inputLastName.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String kidfname = inputKidFirstName.getText().toString().trim();
        String kidlname = inputKidLastName.getText().toString().trim();
        String kidbirthdate = inputKidBirthDate.getText().toString().trim();
        String kidpic = inputKidPic.getText().toString().trim();
        String kinderganname = inputKinderGanName.getText().toString().trim();
        String kinderganaddress = inputKinderGanAddress.getText().toString().trim();
        String kinderganclass = inputKinderGanClass.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // One of the fields is invalid
        if (!validate(id, fname, lname, address, phone,
                      kidfname, kidlname, kidbirthdate, kidpic,
                      kinderganname, kinderganaddress, kinderganclass, email, password))
        {
            onSignupFailed("Sign up failed");
            return;
        }

        registerUser(id, fname, lname, address, phone,
                     kidfname, kidlname, kidbirthdate, kidpic,
                     kinderganname, kinderganaddress, kinderganclass, email, password);
    }

    *//**
     * This function shows a message to the user that the sign up has failed.
     *//*
    public void onSignupFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    *//**
     * This is a validation function that checks all the fields.
     * @return boolean - This returns true if all the fields are valid, false if one of the fields is invalid.
     *//*
    public boolean validate(final String id, final String fname, final String lname, final String address, final String phone,
                            final String kidfname, final String kidlname, final String kidbirthdate, final String kidpic,
                            final String kinderganname, final String kinderganaddress, final String kinderganclass,
                            final String email, final String password)
    {
        boolean valid = true;

        // ID validation
        if (id.isEmpty())
        {
            inputID.setError("Enter your ID !");
            valid = false;
        }
        else
        {
            inputID.setError(null);
        }

        // First name validation
        if (fname.isEmpty())
        {
            inputFirstName.setError("Enter your first name !");
            valid = false;
        }
        else
        {
            inputFirstName.setError(null);
        }

        // Last name validation
        if (lname.isEmpty())
        {
            inputLastName.setError("Enter your last name !");
            valid = false;
        }
        else
        {
            inputLastName.setError(null);
        }

        // Address validation
        if (address.isEmpty())
        {
            inputAddress.setError("Enter your address !");
            valid = false;
        }
        else
        {
            inputAddress.setError(null);
        }

        // Phone validation
        if (phone.isEmpty())
        {
            inputPhone.setError("Enter your phone !");
            valid = false;
        }
        else
        {
            inputPhone.setError(null);
        }

        // Kid First Name validation
        if (kidfname.isEmpty())
        {
            inputKidFirstName.setError("Enter your kid's first name !");
            valid = false;
        }
        else
        {
            inputKidFirstName.setError(null);
        }

        // Kid Last Name validation
        if (kidlname.isEmpty())
        {
            inputKidLastName.setError("Enter your kid's last name !");
            valid = false;
        }
        else
        {
            inputKidLastName.setError(null);
        }

        // Kid Birth Date validation
        if (kidbirthdate.isEmpty() ||
                (Integer.parseInt(kidbirthdate.split("/")[2])) > Integer.valueOf(year) ||
                (Integer.parseInt(kidbirthdate.split("/")[2])) == Integer.valueOf(year) &&
                        (Integer.parseInt(kidbirthdate.split("/")[1])) >= Integer.valueOf(month) &&
                        (Integer.parseInt(kidbirthdate.split("/")[0])) > Integer.valueOf(day))
        {
            inputKidBirthDate.setError("Enter your kid's birth date !");
            valid = false;
        }
        else
        {
            inputKidBirthDate.setError(null);
        }

        // Kid Picture validation
        if (kidpic.isEmpty())
        {
            inputKidPic.setError("Enter your kid's picture !");
            valid = false;
        }
        else
        {
            inputKidPic.setError(null);
        }

        // KinderGan Name validation
        if (kinderganname.isEmpty())
        {
            inputKinderGanName.setError("Enter your kid's kindergan name !");
            valid = false;
        }
        else
        {
            inputKinderGanName.setError(null);
        }

        // KinderGan Address validation
        if (kinderganaddress.isEmpty())
        {
            inputKinderGanAddress.setError("Enter your kid's kindergan address !");
            valid = false;
        }
        else
        {
            inputKinderGanAddress.setError(null);
        }

        // KinderGan Class validation
        if (kinderganclass.isEmpty() || kinderganclass.length() > 1)
        {
            inputKinderGanClass.setError("Enter your kid's valid kindergan class !");
            valid = false;
        }
        else
        {
            inputKinderGanClass.setError(null);
        }

        // Email validation
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError("Enter a valid email address !");
            valid = false;
        }
        else
        {
            inputEmail.setError(null);
        }

        // Password validation
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches())
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

    *//**
     * Function to store user in MySQL database,
     * will post all params to register url
     *//*
    private void registerUser(final String id, final String fname, final String lname, final String address, final String phone,
                              final String kidfname, final String kidlname, final String kidbirthdate, final String kidpic,
                              final String kinderganname, final String kinderganaddress, final String kinderganclass,
                              final String email, final String password)
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
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);
                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupParentActivity.this, UserActivity.class);
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
                params.put("name", fname);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    *//**
     * Show the progress dialog
     *//*
    private void showDialog()
    {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    *//**
     * Hide the progress dialog
     *//*
    private void hideDialog()
    {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/
}

