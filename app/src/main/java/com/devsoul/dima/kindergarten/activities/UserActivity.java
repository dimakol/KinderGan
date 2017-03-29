package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.helper.BitmapHandler;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.helper.SessionManager;

import java.util.HashMap;

/**
 * The User Activity fetching the logged user information from SQLite and displaying it on the screen.
 * The logout button will logout the user by clearing the session and deleting the user from SQLite table.
 */
public class UserActivity extends Activity
{
    private TextView txtName;
    private TextView txtEmail;
    private ImageView imageView;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;
    private BitmapHandler bmpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Bitmap handler
        bmpHandler = new BitmapHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getTeacherDetails();

        String name = user.get(db.KEY_FIRST_NAME) +  user.get(db.KEY_lAST_NAME);
        String email = user.get(db.KEY_EMAIL);
        String path = user.get(db.KEY_PHOTO);

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        //Toast.makeText(getBaseContext(), path, Toast.LENGTH_LONG).show();
        // Decode the image and set on the image view
        //bmpHandler.loadBitmap(Uri.parse(path), imageView);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user.
     * Will set isLoggedIn flag to false in shared preferences
     * and clears the user data from sqlite users table
     */
    private void logoutUser()
    {
        session.setLogin(false);
        db.deleteTeachers();

        // Launching the login activity
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
