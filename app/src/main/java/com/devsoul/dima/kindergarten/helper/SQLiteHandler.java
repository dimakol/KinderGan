package com.devsoul.dima.kindergarten.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Teacher;

import java.util.HashMap;

import static android.R.attr.id;

/**
 *  This class takes care of storing the user data in SQLite database.
 *  Whenever we needs to get the logged in user information,
 *  we fetch from SQLite instead of making request to server.
 */
public class SQLiteHandler extends SQLiteOpenHelper
{
    // Logcat tag
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "login_db";

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TEACHERS = "teachers";
    private static final String TABLE_PARENTS = "parents";

    // Common column names
    public static final String KEY_UID = "uid";
    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "fname";
    public static final String KEY_lAST_NAME = "lname";
    public static final String KEY_CREATED_AT = "created_at";

    // Columns names
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_KINDERGAN_NAME = "kname";
    public static final String KEY_KINDERGAN_ADDRESS = "kaddress";
    public static final String KEY_KINDERGAN_CLASS = "kclass";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_ADDRESS = "address";

    // Table Create Statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    //  Teachers table create statement
    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + "("
            + KEY_ID + " TEXT UNIQUE," + KEY_FIRST_NAME + " TEXT," + KEY_lAST_NAME + " TEXT,"
            + KEY_PHONE + " TEXT," + KEY_PHOTO + " TEXT,"
            + KEY_KINDERGAN_NAME + " TEXT," + KEY_KINDERGAN_ADDRESS + " TEXT," + KEY_KINDERGAN_CLASS + " INTEGER,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Constructor
    public SQLiteHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // creating required tables
        //db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TEACHERS);
        Log.d(TAG, "Database Tables Created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);

        // Create new tables
        onCreate(db);
    }

    /**
     * Storing user details in database
     **/
    public void addUser(String name, String email, String uid, String created_at)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // user ID
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user was added to sqlite: " + id);
    }

    /**
     * Storing teacher details in database
     */
    public void addTeacher(Teacher nanny, KinderGan gan)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, nanny.GetID());
        values.put(KEY_FIRST_NAME, nanny.GetFirstName());
        values.put(KEY_lAST_NAME, nanny.GetLastName());
        values.put(KEY_PHONE, nanny.GetPhone());
        values.put(KEY_PHOTO, nanny.GetPicture());
        values.put(KEY_KINDERGAN_NAME, gan.GetName());
        values.put(KEY_KINDERGAN_ADDRESS, gan.GetAddress());
        values.put(KEY_KINDERGAN_CLASS, nanny.GetClass());
        values.put(KEY_EMAIL, nanny.GetEmail());
        values.put(KEY_CREATED_AT, nanny.GetCreatedAt());

        // Inserting Row
        long teacher_id = db.insert(TABLE_TEACHERS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New teacher was added to sqlite: " + id);
    }

    /**
     * Getting user data from database
     **/
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user info from sqlite");

        return user;
    }

    /**
     * Getting teacher data from database
     **/
    public HashMap<String, String> getTeacherDetails()
    {
        HashMap<String, String> teacher = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_TEACHERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            teacher.put(KEY_ID, cursor.getString(0));
            teacher.put(KEY_FIRST_NAME, cursor.getString(1));
            teacher.put(KEY_lAST_NAME, cursor.getString(2));
            teacher.put(KEY_PHONE, cursor.getString(3));
            teacher.put(KEY_PHOTO, cursor.getString(4));
            teacher.put(KEY_KINDERGAN_NAME, cursor.getString(5));
            teacher.put(KEY_KINDERGAN_ADDRESS, cursor.getString(6));
            teacher.put(KEY_KINDERGAN_CLASS, cursor.getString(7));
            teacher.put(KEY_EMAIL, cursor.getString(8));
            teacher.put(KEY_CREATED_AT, cursor.getString(9));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching teacher info from sqlite");

        return teacher;
    }

    /**
     * Will return total number of users in SQLite database table.
     * @param table_name - Name of the table to get the number of rows.
     * @return number of rows
     */
    public int getRowCount(String table_name)
    {
        String countQuery = "SELECT * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(countQuery, null);
        int row_count = c.getCount();
        db.close();
        c.close();
        return row_count;
    }

    /**
     * Delete table of users
     * */
    public void deleteUsers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USERS, null, null);
        db.close();

        Log.d(TAG, "User has been deleted from sqlite");
    }

    /**
     * Delete table of teachers
     * */
    public void deleteTeachers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_TEACHERS, null, null);
        db.close();

        Log.d(TAG, "Teachers has been deleted from sqlite");
    }
}
