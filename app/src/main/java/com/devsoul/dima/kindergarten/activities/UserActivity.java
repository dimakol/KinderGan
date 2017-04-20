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
    //private BitmapHandler bmpHandler;

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

        // Bitmap handler
        //bmpHandler = new BitmapHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        HashMap<String, String> user = null;
        String path = null;
        // SQLite table of teachers contains records
        if (db.getRowCount(db.TABLE_TEACHERS) > 0)
        {
            // Fetching user details from SQLite teachers table
            user = db.getTeacherDetails();

            path = user.get(db.KEY_PHOTO);
        }
        // SQLite table of parents contains records
        else if (db.getRowCount(db.TABLE_PARENTS) > 0)
        {
            // Fetching user details from SQLite parents table
            user = db.getParentDetails();

            // Fetching kid details from SQLite kids table
            HashMap<String, String> kid;
            kid = db.getKidDetails();

            path = kid.get(db.KEY_PHOTO);
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
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this,TeacherActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Logging out the user.
     * Will set isLoggedIn flag to false in shared preferences
     * and clears the user data from SQLite users table
     */
    private void logoutUser()
    {
        session.setLogin(false);
        db.deleteTeachers();
        db.deleteParents();
        db.deleteKids();
        //db.deleteGans();

        // Launching the login activity
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
