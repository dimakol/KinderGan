package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
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
import com.devsoul.dima.kindergarten.model.Kid;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Parent;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Signup ParentGan Activity enables the user that is a parent to create an account in the application.
 */
public class SignupParentGanActivity extends Activity
{
    private static final String TAG = SignupParentGanActivity.class.getSimpleName();

    private static final int PICK_IMAGE_REQUEST = 1; // To get Image from gallery
    private static final int DIALOG_ID = 0;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final String PASSWORD_PATTERN =
                    "((?=.*\\d)" +        // must contains one digit from 0-9
                    "(?=.*[a-z])" +       // must contains one lowercase characters
                    "(?=.*[A-Z])" +       // must contains one uppercase characters
                    "(?=.*[!@#$%])" +     // must contains one special symbols in the list "!@#$%"
                    "(?!.*\\s)" +         // disallow spaces
                    ".{8,15})";           // length at least 8 characters and maximum of 15

    String [] GAN_LIST={"Stars Gan","Flowers Gan","Rainbow Gan"};
    String [] CLASS_LIST={"1","2"};

    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.input_FName) EditText inputKidFirstName;
    @InjectView(R.id.input_KidBirthDate) EditText inputKidBirthDate;
    @InjectView(R.id.link_login) TextView btnLinkToLogin;

    private MaterialBetterSpinner dropdownKinderGanName;
    private MaterialBetterSpinner dropdownKidClass;

    private ImageButton btnImgChoose;    // Choose the image
    private ImageView imageView;         // To show the selected image
    private Uri image_path;              // Path of the image

    private ImageButton img_btnBack, img_btnRegister;

    // For kid birth date
    private Calendar calendar;
    private int year, month, day;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private BitmapHandler bmpHandler;

    private Parent parent;
    private Kid child;
    private KinderGan Gan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_parent_gan);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, GAN_LIST);
        dropdownKinderGanName =(MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner);
        dropdownKinderGanName.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CLASS_LIST);
        dropdownKidClass =(MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner2);
        dropdownKidClass.setAdapter(arrayAdapter1);

        // Set kid birth date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Image choose button
        btnImgChoose = (ImageButton) findViewById(R.id.btn_KidPic);
        // View of the image
        imageView = (ImageView) findViewById(R.id.imageView);

        // Back button
        img_btnBack = (ImageButton) findViewById(R.id.img_btn_back);
        // Register button
        img_btnRegister = (ImageButton) findViewById(R.id.img_btn_SignUp);

        // Inject the ButterKnife design
        ButterKnife.inject(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Bitmap handler
        bmpHandler = new BitmapHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {
            // User is already logged in. Take him to user activity
            Intent intent = new Intent(SignupParentGanActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        // To retrieve objects in current activity
        parent = (Parent) getIntent().getSerializableExtra("parent");
        Gan = (KinderGan) getIntent().getSerializableExtra("kindergan");
        child = (Kid) getIntent().getSerializableExtra("kid");
        if ((Gan != null) && (child != null))
        // Has object of gan and child from previous activity (SignUpParent Activity)
        {
            // Load fields of KinderGan and Kid
            LoadFields();
        }
        else
        {
            // Create new objects
            child = new Kid();
            Gan = new KinderGan();
        }
/*
        // Select Gan Button click event
        dropdownKinderGanName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // SQLite Gans table is empty
                if (db.getRowCount(db.TABLE_GANS) == 0)
                {

                }
            }
        });
*/
        // Kid birth date Button Click event
        inputKidBirthDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDate();
            }
        });

        // Image choose Button Click event
        btnImgChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Show images in gallery
                showFileChooser();
            }
        });

        // Previous page Button Click event
        img_btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Save all the fields in current page
                String KinderGan_name = dropdownKinderGanName.getText().toString();
                String Kid_Class = dropdownKidClass.getText().toString();
                String password = inputPassword.getText().toString().trim();
                String Kid_FName = inputKidFirstName.getText().toString().trim();
                String Kid_BirthDate = inputKidBirthDate.getText().toString().trim();

                Gan = new KinderGan(KinderGan_name);

                if (child != null)
                {
                    child.SetName(Kid_FName);
                    child.SetClass(Kid_Class);
                    child.SetBirthDate(Kid_BirthDate);
                }
                else
                // First visit in activity
                {
                    // With picture
                    if (image_path != null)
                        child = new Kid(Kid_FName, Kid_Class, Kid_BirthDate, image_path.toString());
                    // Without picture
                    else
                        child = new Kid(Kid_FName, Kid_Class, Kid_BirthDate);
                }

                parent.SetPassword(password);

                // Go to previous page of registration (parent Info)
                Intent i = new Intent(SignupParentGanActivity.this, SignupParentActivity.class);
                //To pass object of parent to previous activity
                i.putExtra("parent", parent);
                //To pass object of gan to previous activity
                i.putExtra("kindergan", Gan);
                //To pass object of kid to previous activity
                i.putExtra("kid", child);
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
                Intent i = new Intent(SignupParentGanActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate()
    {
        // onCreateDialog method called
        showDialog(DIALOG_ID);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DIALOG_ID)
        {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener()
    {
        /**
         * Set the date that was chosen
         * @param arg0 - The object
         * @param arg1 - Year
         * @param arg2 - Month
         * @param arg3 - Day
         */
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3)
        {
            showDate(arg1, arg2+1, arg3);
        }
    };

    /**
     * Show the date that was chosen in the kid birth date input text
     * @param year
     * @param month
     * @param day
     */
    private void showDate(int year, int month, int day)
    {
        inputKidBirthDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    /**
     * This function load the fields of KinderGan and Kid in current activity
     */
    public void LoadFields()
    {
        dropdownKinderGanName.setText(Gan.GetName());
        dropdownKidClass.setText(child.GetClass());
        inputPassword.setText(parent.GetPassword());
        inputKidFirstName.setText(child.GetName());

        // Birth date selected
        if (child.GetBirthDate() != null)
        {
            // Load the birth date
            inputKidBirthDate.setText(child.GetBirthDate());
        }

        // Picture selected
        if (child.GetPicture() != null)
        {
            // Load the image
            bmpHandler.loadBitmap(Uri.parse(child.GetPicture()), imageView);
        }
    }

    /**
     * This method for choosing image from gallery
     */
    private void showFileChooser()
    {
        Intent intent;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        // The current version is Kitkat or higher
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        else
        // The current version is lower than Kitkat
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    /**
     * Get the Uri of bitmap from gallery
     * Decode the image and set it on the image view
     */
    //@TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            image_path = data.getData();
            try
            {
                //final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                //getContentResolver().takePersistableUriPermission(image_path, takeFlags);
                // Decode the image and set on the image view
                bmpHandler.loadBitmap(image_path, imageView);

                // Save the picture path in child object
                child.SetPicture(image_path.toString());
            }
            catch (Exception e)
            {
                //handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * This function performs the sign up operation.
     */
    public void signup()
    {
        Log.d(TAG, "SignupParentGan");

        String KinderGanName = dropdownKinderGanName.getText().toString().trim();
        String KidClass = dropdownKidClass.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String KidName = inputKidFirstName.getText().toString().trim();
        String KidBirthDate = inputKidBirthDate.getText().toString().trim();

        Gan.SetName(KinderGanName);
        child.SetName(KidName);
        child.SetClass(KidClass);
        child.SetBirthDate(KidBirthDate);

        parent.SetPassword(password);

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
            dropdownKinderGanName.setError("Choose your child's kindergan name !");
            valid = false;
        }
        else
        {
            dropdownKinderGanName.setError(null);
        }

        // Kid's class validation
        if (child.GetClass().isEmpty())
        {
            dropdownKidClass.setError("Choose your child's kindergan class !");
            valid = false;
        }
        else
        {
            dropdownKidClass.setError(null);
        }

        // Password validation
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(parent.GetPassword()).matches())
        {
            inputPassword.setError("Password must be at least 8 characters.\n" +
                    "Use numbers, symbols and mix of upper and lower case letters !");
            valid = false;
        }
        else
        {
            inputPassword.setError(null);
        }

        // Kid First Name validation
        if (child.GetName().isEmpty())
        {
            inputKidFirstName.setError("Enter your child's first name !");
            valid = false;
        }
        else
        {
            inputKidFirstName.setError(null);
        }

        // Kid Birth Date validation
        try
        {
            if (inputKidBirthDate.getText().toString().isEmpty() ||
                    (formatter.parse(inputKidBirthDate.getText().toString()).after(new Date())))
            {
                inputKidBirthDate.setError("Enter your child's birth date !");
                valid = false;
            }
            else
            {
                inputKidBirthDate.setError(null);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Kid Picture validation
        if (bmpHandler.GetBitmap() == null)
        {
            onSignupFailed("Enter your child's picture !");
            valid = false;
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
                        // Parent user successfully stored in MySQL
                        // Now store the parent user in SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        parent.SetID(user.getString("ID"));
                        parent.SetFirstName(user.getString("firstname"));
                        parent.SetLastName(user.getString("lastname"));
                        parent.SetAddress(user.getString("address"));
                        parent.SetPhone(user.getString("phone"));
                        parent.SetEmail(user.getString("email"));
                        parent.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in parents table
                        db.addParent(parent);

                        // Now store the child user in SQLite
                        child.SetName(user.getString("kid_name"));
                        child.SetBirthDate(user.getString("kid_birthdate"));
                        child.SetPicture(user.getString("kid_photo"));
                        child.SetClass(user.getString("kid_class"));
                        Gan.SetName(user.getString("kindergan_name"));
                        child.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in kids table
                        db.addKid(child, Gan);

                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupParentGanActivity.this, UserActivity.class);
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
                params.put("tag", "register_parent");
                params.put("ID", parent.GetID());
                params.put("First_Name", parent.GetFirstName());
                params.put("Last_Name", parent.GetLastName());
                params.put("Address", parent.GetAddress());
                params.put("Phone", parent.GetPhone());

                params.put("Kid_Name", child.GetName());
                params.put("Kid_BirthDate", child.GetBirthDate());
                //Converting Bitmap to String
                String image = bmpHandler.getStringImage(bmpHandler.decodeSampledBitmapFromStream(Uri.parse(child.GetPicture()), 300, 300));
                params.put("Kid_Picture", image);
                params.put("KinderGan_Name", Gan.GetName());
                params.put("Kid_Class", child.GetClass());
                params.put("Email", parent.GetEmail());
                params.put("Password", parent.GetPassword());

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

    /**
     * Function to load Kindergans from MySQL database to SQLite,
     * will post all params to register url
     */
    /*
    private void LoadNamesofGan()
    {
        // Tag used to cancel the request
        String tag_string_req = "kindergans_request";

        pDialog.setMessage("Loading names ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.REGISTER_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "kindergans Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        // Kindergans successfully loaded from MySQL
                        // Now store the Kindergans in SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        parent.SetID(user.getString("ID"));
                        parent.SetFirstName(user.getString("firstname"));
                        parent.SetLastName(user.getString("lastname"));
                        parent.SetAddress(user.getString("address"));
                        parent.SetPhone(user.getString("phone"));
                        parent.SetEmail(user.getString("email"));
                        parent.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in parents table
                        db.addParent(parent);

                        // Now store the child user in SQLite
                        child.SetName(user.getString("kid_name"));
                        child.SetBirthDate(user.getString("kid_birthdate"));
                        child.SetPicture(user.getString("kid_photo"));
                        child.SetClass(user.getString("kid_class"));
                        Gan.SetName(user.getString("kindergan_name"));
                        child.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in kids table
                        db.addKid(child, Gan);

                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupParentGanActivity.this, UserActivity.class);
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
                Log.e(TAG, "Load Error: " + error.getMessage());
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
                params.put("tag", "get_kindergans");
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
    */
}

