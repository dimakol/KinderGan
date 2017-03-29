package com.devsoul.dima.kindergarten.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.Request.Method;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Signup Activity enables the user to create an account in the application,
 * and is generally displayed via the link on the login Activity.
 */
public class SignupActivity extends Activity
{
    private static final String TAG = SignupActivity.class.getSimpleName();
    private static final String PASSWORD_PATTERN =
                    "((?=.*\\d)" +        // must contains one digit from 0-9
                    "(?=.*[a-z])" +       // must contains one lowercase characters
                    "(?=.*[A-Z])" +       // must contains one uppercase characters
                    "(?=.*[!@#$%])" +     // must contains one special symbols in the list "!@#$%"
                    "(?!.*\\s)" +         // disallow spaces
                    ".{8,15})";           // length at least 8 characters and maximum of 15

    @InjectView(R.id.input_id) EditText inputID;
    @InjectView(R.id.input_name) EditText inputName;
    @InjectView(R.id.input_phone) EditText inputPhone;
    @InjectView(R.id.input_email) EditText inputEmail;
    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.btn_signup) Button btnRegister;
    @InjectView(R.id.link_login) TextView btnLinkToLogin;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
            Intent intent = new Intent(SignupActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

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
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * This function performs the sign up operation.
     */
    public void signup()
    {
        Log.d(TAG, "Signup");

        String id = inputID.getText().toString().trim();
        String name = inputName.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // One of the fields is invalid
        if (!validate(id, name, phone, email, password))
        {
            onSignupFailed("Sign up failed");
            return;
        }

        registerUser(id, name, phone, email, password);
    }

    /**
     * This function shows a message to the user that the sign up has failed.
     */
    public void onSignupFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * This is a validation function that checks three fields: Name, Email and Password.
     * @param name - The name that was entered.
     * @param email - The email that was entered.
     * @param password - The password that was entered.
     * @return boolean - This returns true if all the fields are valid, false if one of the fields is invalid.
     */
    public boolean validate(final String id, final String name, final String phone, final String email, final String password)
    {
        boolean valid = true;

        // ID validation
        if (id.isEmpty() || id.length() > 10)
        {
            inputID.setError("Enter your ID !");
            valid = false;
        }
        else
        {
            inputID.setError(null);
        }

        // Name validation
        if (name.isEmpty())
        {
            inputName.setError("Enter your name !");
            valid = false;
        }
        else
        {
            inputName.setError(null);
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

    /**
     * Function to store user in MySQL database will post params(tag, name, email, password) to register url
     */
    private void registerUser(final String id, final String name, final String phone, final String email, final String password)
    {
        // Tag used to cancel the request
        String tag_string_req = "register_request";

        pDialog.setMessage("Creating Account ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.REGISTER_URL, new Response.Listener<String>()
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
                        Intent intent = new Intent(SignupActivity.this, UserActivity.class);
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
                params.put("id", id);
                params.put("name", name);
                params.put("phone", phone);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

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
