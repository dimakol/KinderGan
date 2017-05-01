package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.helper.SQLiteHandler;
import com.devsoul.dima.kindergarten.helper.SessionManager;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.HashMap;

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

    private SQLiteHandler db;
    private SessionManager session;

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

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

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
}
