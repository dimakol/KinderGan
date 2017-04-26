package com.devsoul.dima.kindergarten.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.devsoul.dima.kindergarten.model.Kid;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Parent;
import com.devsoul.dima.kindergarten.model.Teacher;

import java.util.HashMap;

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
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "login_db";

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TEACHERS = "teachers";
    public static final String TABLE_PARENTS = "parents";
    public static final String TABLE_KIDS = "kids";
    public static final String TABLE_GANS = "kindergans";

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
    public static final String KEY_BIRTHDATE = "birth_date";
    public static final String KEY_CLASS = "class";
    public static final String KEY_KINDERGAN_NAME = "kname";
    public static final String KEY_KINDERGAN_ADDRESS = "kaddress";
    public static final String KEY_KINDERGAN_CLASS = "kclass";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CLASSES = "classes";
    public static final String KEY_CITY = "city";

    // Table Create Statements
    // Teachers table create statement
    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + "("
            + KEY_ID + " TEXT UNIQUE," + KEY_FIRST_NAME + " TEXT," + KEY_lAST_NAME + " TEXT,"
            + KEY_PHONE + " TEXT," + KEY_PHOTO + " TEXT,"
            + KEY_KINDERGAN_NAME + " TEXT," + KEY_KINDERGAN_ADDRESS + " TEXT," + KEY_KINDERGAN_CLASS + " INTEGER,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Parent table create statement
    private static final String CREATE_TABLE_PARENTS = "CREATE TABLE " + TABLE_PARENTS + "("
            + KEY_ID + " TEXT UNIQUE," + KEY_FIRST_NAME + " TEXT," + KEY_lAST_NAME + " TEXT,"
            + KEY_ADDRESS + " TEXT," + KEY_PHONE + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Kids table create statement
    private static final String CREATE_TABLE_KIDS = "CREATE TABLE " + TABLE_KIDS + "("
            + KEY_UID + " TEXT UNIQUE," + KEY_NAME + " TEXT,"
            + KEY_BIRTHDATE + " TEXT," + KEY_PHOTO + " TEXT,"
            + KEY_KINDERGAN_NAME + " TEXT," + KEY_CLASS + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // Gan table create statement
    private static final String CREATE_TABLE_GANS = "CREATE TABLE " + TABLE_GANS + "("
            + KEY_UID + " TEXT UNIQUE," + KEY_NAME + " TEXT,"
            + KEY_CLASSES + " INTEGER," + KEY_ADDRESS + " TEXT,"
            + KEY_CITY + " TEXT," + KEY_PHONE + " TEXT" + ")";

    // Constructor
    public SQLiteHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // creating required tables
        db.execSQL(CREATE_TABLE_TEACHERS);
        db.execSQL(CREATE_TABLE_PARENTS);
        db.execSQL(CREATE_TABLE_KIDS);
        db.execSQL(CREATE_TABLE_GANS);
        Log.d(TAG, "Database Tables Created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KIDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GANS);
        // Create new tables
        onCreate(db);
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

        Log.d(TAG, "New teacher was added to sqlite: " + teacher_id);
    }

    /**
     * Storing parent details in database
     */
    public void addParent(Parent parent)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, parent.GetID());
        values.put(KEY_FIRST_NAME, parent.GetFirstName());
        values.put(KEY_lAST_NAME, parent.GetLastName());
        values.put(KEY_ADDRESS, parent.GetAddress());
        values.put(KEY_PHONE, parent.GetPhone());
        values.put(KEY_EMAIL, parent.GetEmail());
        values.put(KEY_CREATED_AT, parent.GetCreatedAt());

        // Inserting Row
        long parent_id = db.insert(TABLE_PARENTS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New parent was added to sqlite: " + parent_id);
    }

    /**
     * Storing kid details in database
     */
    public void addKid(Kid child, KinderGan gan)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, child.GetName());
        values.put(KEY_BIRTHDATE, child.GetBirthDate());
        values.put(KEY_PHOTO, child.GetPicture());
        values.put(KEY_CLASS, child.GetClass());
        values.put(KEY_KINDERGAN_NAME, gan.GetName());
        values.put(KEY_CREATED_AT, child.GetCreatedAt());

        // Inserting Row
        long kid_id = db.insert(TABLE_KIDS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New kid was added to sqlite: " + kid_id);
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
        Log.d(TAG, "Fetching teacher info from SQLite");

        return teacher;
    }

    /**
     * Getting parent data from database
     **/
    public HashMap<String, String> getParentDetails()
    {
        HashMap<String, String> parent = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_PARENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            parent.put(KEY_ID, cursor.getString(0));
            parent.put(KEY_FIRST_NAME, cursor.getString(1));
            parent.put(KEY_lAST_NAME, cursor.getString(2));
            parent.put(KEY_ADDRESS, cursor.getString(3));
            parent.put(KEY_PHONE, cursor.getString(4));
            parent.put(KEY_EMAIL, cursor.getString(5));
            parent.put(KEY_CREATED_AT, cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching parent info from SQLite");

        return parent;
    }

    /**
     * Getting kid data from database
     **/
    public HashMap<String, String> getKidDetails()
    {
        HashMap<String, String> kid = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_KIDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            kid.put(KEY_UID, cursor.getString(0));
            kid.put(KEY_NAME, cursor.getString(1));
            kid.put(KEY_BIRTHDATE, cursor.getString(2));
            kid.put(KEY_PHOTO, cursor.getString(3));
            kid.put(KEY_KINDERGAN_NAME, cursor.getString(4));
            kid.put(KEY_CLASS, cursor.getString(5));
            kid.put(KEY_CREATED_AT, cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching kid info from SQLite");

        return kid;
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
     * Delete table of teachers
     */
    public void deleteTeachers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_TEACHERS, null, null);
        db.close();

        Log.d(TAG, "Teachers has been deleted from SQLite");
    }

    /**
     * Delete table of parents
     */
    public void deleteParents()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PARENTS, null, null);
        db.close();

        Log.d(TAG, "Parents has been deleted from SQLite");
    }

    /**
     * Delete table of kids
     */
    public void deleteKids()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_KIDS, null, null);
        db.close();

        Log.d(TAG, "Kids has been deleted from SQLite");
    }

    /**
     * Delete table of gans
     */
    public void deleteGans()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_GANS, null, null);
        db.close();

        Log.d(TAG, "Gans has been deleted from SQLite");
    }
}
