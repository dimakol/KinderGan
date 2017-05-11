package com.devsoul.dima.kindergarten.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.devsoul.dima.kindergarten.model.Kid;
import com.devsoul.dima.kindergarten.model.KinderGan;
import com.devsoul.dima.kindergarten.model.Parent;
import com.devsoul.dima.kindergarten.model.Teacher;

import java.util.ArrayList;
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
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "login_db";

    // Table names
    public static final String TABLE_TEACHERS = "teachers";
    public static final String TABLE_PARENTS = "parents";
    public static final String TABLE_KIDS = "kids";
    public static final String TABLE_GANS = "kindergans";

    // Common column names
    public static final String KEY_UID = "uid";
    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "fname";
    public static final String KEY_LAST_NAME = "lname";
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
    public static final String KEY_PARENT_ID = "parent_id";

    // Columns names addons to kids
    public static final String KEY_PRESENCE = "presence";
    public static final String KEY_SPECIAL = "special";
    public static final String KEY_CONTACT1 = "contact1";
    public static final String KEY_CONTACT2 = "contact2";
    public static final String KEY_CONTACT3 = "contact3";

    // Table Create Statements
    // Teachers table create statement
    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + "("
            + KEY_ID + " TEXT UNIQUE," + KEY_FIRST_NAME + " TEXT," + KEY_LAST_NAME + " TEXT,"
            + KEY_PHONE + " TEXT," + KEY_PHOTO + " TEXT,"
            + KEY_KINDERGAN_NAME + " TEXT," + KEY_CITY + " TEXT," + KEY_KINDERGAN_CLASS + " INTEGER,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Parent table create statement
    private static final String CREATE_TABLE_PARENTS = "CREATE TABLE " + TABLE_PARENTS + "("
            + KEY_ID + " TEXT UNIQUE," + KEY_FIRST_NAME + " TEXT," + KEY_LAST_NAME + " TEXT,"
            + KEY_ADDRESS + " TEXT," + KEY_PHONE + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

    // Kids table create statement
    private static final String CREATE_TABLE_KIDS = "CREATE TABLE " + TABLE_KIDS + "("
            + KEY_UID + " TEXT UNIQUE," + KEY_NAME + " TEXT,"
            + KEY_BIRTHDATE + " TEXT," + KEY_PHOTO + " TEXT,"
            + KEY_KINDERGAN_NAME + " TEXT," + KEY_CLASS + " INTEGER,"
            + KEY_PARENT_ID + " TEXT," + KEY_PRESENCE + " INTEGER," + KEY_SPECIAL + " TEXT,"
            + KEY_CONTACT1 + " TEXT," + KEY_CONTACT2 + " TEXT," + KEY_CONTACT3 + " TEXT,"
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
        values.put(KEY_LAST_NAME, nanny.GetLastName());
        values.put(KEY_PHONE, nanny.GetPhone());
        values.put(KEY_PHOTO, nanny.GetPicture());
        values.put(KEY_KINDERGAN_NAME, gan.GetName());
        values.put(KEY_CITY, gan.GetCity());
        values.put(KEY_KINDERGAN_CLASS, nanny.GetClass());
        values.put(KEY_EMAIL, nanny.GetEmail());
        values.put(KEY_CREATED_AT, nanny.GetCreatedAt());

        // Inserting Row
        long teacher_id = db.insert(TABLE_TEACHERS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New teacher was added to SQLite: " + teacher_id);
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
        values.put(KEY_LAST_NAME, parent.GetLastName());
        values.put(KEY_ADDRESS, parent.GetAddress());
        values.put(KEY_PHONE, parent.GetPhone());
        values.put(KEY_EMAIL, parent.GetEmail());
        values.put(KEY_CREATED_AT, parent.GetCreatedAt());

        // Inserting Row
        long parent_id = db.insert(TABLE_PARENTS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New parent was added to SQLite: " + parent_id);
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
        values.put(KEY_PARENT_ID, child.GetParentID());
        values.put(KEY_PRESENCE, child.GetPresence());
        values.put(KEY_SPECIAL, child.GetSpecial());
        values.put(KEY_CONTACT1, child.GetContact1());
        values.put(KEY_CONTACT2, child.GetContact2());
        values.put(KEY_CONTACT3, child.GetContact3());
        values.put(KEY_CREATED_AT, child.GetCreatedAt());

        // Inserting Row
        long kid_id = db.insert(TABLE_KIDS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New kid was added to SQLite: " + kid_id);
    }

    /**
     * Storing kindergan details in database
     */
    public void addKindergan(KinderGan gan)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, gan.GetID());
        values.put(KEY_NAME, gan.GetName());
        values.put(KEY_CLASSES, gan.GetClasses());
        values.put(KEY_ADDRESS, gan.GetAddress());
        values.put(KEY_CITY, gan.GetCity());
        values.put(KEY_PHONE, gan.GetPhone());

        // Inserting Row
        long gan_id = db.insert(TABLE_GANS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New kindergan was added to SQLite: " + gan_id);
    }

    /**
     * Getting teacher data from database
     */
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
            teacher.put(KEY_LAST_NAME, cursor.getString(2));
            teacher.put(KEY_PHONE, cursor.getString(3));
            teacher.put(KEY_PHOTO, cursor.getString(4));
            teacher.put(KEY_KINDERGAN_NAME, cursor.getString(5));
            teacher.put(KEY_CITY, cursor.getString(6));
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
     */
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
            parent.put(KEY_LAST_NAME, cursor.getString(2));
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
     */
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
            kid.put(KEY_PARENT_ID, cursor.getString(6));
            kid.put(KEY_PRESENCE, cursor.getString(7));
            kid.put(KEY_SPECIAL, cursor.getString(8));
            kid.put(KEY_CONTACT1, cursor.getString(9));
            kid.put(KEY_CONTACT2, cursor.getString(10));
            kid.put(KEY_CONTACT3, cursor.getString(11));
            kid.put(KEY_CREATED_AT, cursor.getString(12));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching kid info from SQLite");

        return kid;
    }

    /**
     * Getting kindergans city data from database
     */
    public ArrayList<String> getKinderGanCities()
    {
        ArrayList<String> CITY_LIST = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_CITY + " FROM " + TABLE_GANS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                CITY_LIST.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return gan cities as a list
        Log.d(TAG, "Fetching kindergan cities info from SQLite");

        return CITY_LIST;
    }

    /**
     * Getting KinderGans names data by city as a parameter
     * @param City - KinderGan City
     * @return ArrayList of KinderGans names
     */
    public ArrayList<String> getKinderGanNamesbyCity(String City)
    {
        ArrayList<String> GAN_LIST = new ArrayList<>();
        String[] params = new String[]{ City };
        String selectQuery = "SELECT " + KEY_NAME + " FROM " + TABLE_GANS + " WHERE " + KEY_CITY + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                GAN_LIST.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return gan names as a list
        Log.d(TAG, "Fetching kindergan names info from SQLite");

        return GAN_LIST;
    }

    /**
     * Getting KinderGan number of classes by name
     * @param Name - KinderGan Name
     * @return number of classes in specific KinderGan
     */
    public int getKinderGanClasses(String Name)
    {
        int num_classes = 0;
        String[] params = new String[]{ Name };
        String selectQuery = "SELECT " + KEY_CLASSES + " FROM " + TABLE_GANS + " WHERE " + KEY_NAME + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            // get the data
            num_classes = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        // return number of classes
        Log.d(TAG, "Fetching Num_Classes from SQLite");

        return num_classes;
    }

    /**
     * Getting Teacher\s full name data
     * @return ArrayList of Teachers full names
     */
    public ArrayList<String> getTeacherNames()
    {
        ArrayList<String> NAME_LIST = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_FIRST_NAME + ", " + KEY_LAST_NAME + " FROM " + TABLE_TEACHERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                NAME_LIST.add(cursor.getString(0) + " - " + cursor.getString(1));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return teachers full names as a list
        Log.d(TAG, "Fetching teachers names info from SQLite");

        return NAME_LIST;
    }

    /**
     * Getting teacher's phone number and email by her first and last name as parameters
     * @param FirstName - Teacher first name
     * @param LastName - Teacher last name
     * @return array list that contains as fields: teacher phone number, teacher email.
     */
    public ArrayList<String> getTeacherContact(String FirstName, String LastName)
    {
        ArrayList<String> ContactInfo = new ArrayList<>();
        String phone = null;
        String email = null;
        String[] params = new String[]{ FirstName, LastName};
        String selectQuery = "SELECT " + KEY_PHONE + ", " + KEY_EMAIL + " FROM " + TABLE_TEACHERS + " WHERE " + KEY_FIRST_NAME + " = ? AND "
                            + KEY_LAST_NAME + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            // get the data
            phone = cursor.getString(0);
            email = cursor.getString(1);
            ContactInfo.add(phone);
            ContactInfo.add(email);
        }
        cursor.close();
        db.close();

        // return teacher contact info
        Log.d(TAG, "Fetching contact info from SQLite");

        return ContactInfo;
    }

    /**
     * Getting all kid's pictures
     * @return kid's pictures as array list
     */
    public ArrayList<String> getKidsPictures()
    {
        ArrayList<String> Pictures = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PHOTO + " FROM " + TABLE_KIDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                Pictures.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return kids pictures
        Log.d(TAG, "Fetching kids pictures from SQLite");

        return Pictures;
    }

    /**
     * Getting all kid's presence
     * @return kid's presence as array list
     */
    public ArrayList<Integer> getKidsPresence()
    {
        ArrayList<Integer> Presence = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRESENCE + " FROM " + TABLE_KIDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                Presence.add(cursor.getInt(0));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return kids presence
        Log.d(TAG, "Fetching kids presence from SQLite");

        return Presence;
    }

    /**
     * Getting parent id by image path as parameter
     * @param image_path - image of the kid
     * @return ID of kid's parent
     */
    public String getParentID(String image_path)
    {
        String ID = null;

        String[] params = new String[]{ image_path};
        // Inner Join with 2 tables
        String selectQuery = "SELECT " + "p." + KEY_ID +
                " FROM " + TABLE_PARENTS + " p" +
                " INNER JOIN " + TABLE_KIDS + " k" +
                " ON p." + KEY_ID + " = k." + KEY_PARENT_ID +
                " WHERE k." + KEY_PHOTO + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            // get the id
            ID = cursor.getString(0);
        }
        cursor.close();
        db.close();

        // return parent ID
        Log.d(TAG, "Fetching ID from SQLite");

        return ID;
    }

    /**
     * Getting parent phone number by image path as parameter
     * @param image_path - image of the kid
     * @return phone number of kid's parent
     */
    public String getParentPhone(String image_path)
    {
        String phone = null;

        String[] params = new String[]{ image_path};
        // Inner Join with 2 tables
        String selectQuery = "SELECT " + "p." + KEY_PHONE +
                " FROM " + TABLE_PARENTS + " p" +
                " INNER JOIN " + TABLE_KIDS + " k" +
                " ON p." + KEY_ID + " = k." + KEY_PARENT_ID +
                " WHERE k." + KEY_PHOTO + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            // get the phone number
            phone = cursor.getString(0);
        }
        cursor.close();
        db.close();

        // return parent phone number
        Log.d(TAG, "Fetching phone number from SQLite");

        return phone;
    }

    /**
     * Getting kid and his parent data from database by image path as parameter
     * @param image_path - image of the kid
     * @return hashmap of details of specific kid with his parent
     */
    public HashMap<String, String> getDetails(String image_path)
    {
        HashMap<String, String> kid = new HashMap<String, String>();

        String[] params = new String[]{ image_path};
        // Inner Join with 2 tables
        String selectQuery = "SELECT " + "k." + KEY_NAME + ", k." + KEY_BIRTHDATE + ", k." + KEY_SPECIAL +
                             ", k." + KEY_CONTACT1 + ", k." + KEY_CONTACT2 + ", k." + KEY_CONTACT3 +
                             ", p." + KEY_FIRST_NAME + ", p." + KEY_LAST_NAME + ", p." + KEY_ADDRESS +
                             " FROM " + TABLE_PARENTS + " p" +
                             " INNER JOIN " + TABLE_KIDS + " k" +
                             " ON p." + KEY_ID + " = k." + KEY_PARENT_ID +
                             " WHERE k." + KEY_PHOTO + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, params);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            kid.put(KEY_NAME, cursor.getString(0));
            kid.put(KEY_BIRTHDATE, cursor.getString(1));
            kid.put(KEY_SPECIAL, cursor.getString(2));
            kid.put(KEY_CONTACT1, cursor.getString(3));
            kid.put(KEY_CONTACT2, cursor.getString(4));
            kid.put(KEY_CONTACT3, cursor.getString(5));
            kid.put(KEY_FIRST_NAME, cursor.getString(6));
            kid.put(KEY_LAST_NAME, cursor.getString(7));
            kid.put(KEY_ADDRESS, cursor.getString(8));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching kid and parent info from SQLite");

        return kid;
    }

    /**
     * Get all the emails of the parents of specific kindergarten class
     * @return ArrayList of all parents emails of specific class
     */
    public ArrayList<String> getEmails()
    {
        ArrayList<String> Emails = new ArrayList<>();

        String selectQuery = "SELECT " + KEY_EMAIL + " FROM " + TABLE_PARENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                // get the data into array
                Emails.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return parents emails
        Log.d(TAG, "Fetching parents emails from SQLite");

        return Emails;
    }

    /**
     * Updating special request column in kids table in database
     */
    public void UpdateKidSpecialRequest(String Parent_ID, String Request)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + TABLE_KIDS + " SET " + KEY_SPECIAL + " = ? WHERE "
                                                                + KEY_PARENT_ID + " = ?");
        sqLiteStatement.bindString(1, Request);
        sqLiteStatement.bindString(2, Parent_ID);
        sqLiteStatement.executeUpdateDelete();

        db.close(); // Closing database connection

        Log.d(TAG, "Kid request was updated in SQLite: " + Parent_ID);
    }

    /**
     * Updating contacts columns in kids table in database
     */
    public void UpdateKidContacts(String Parent_ID, String Contact1, String Contact2, String Contact3)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + TABLE_KIDS + " SET " + KEY_CONTACT1 + " = ?, "
                                                               + KEY_CONTACT2 + " = ?, " + KEY_CONTACT3 + " = ? WHERE "
                                                                + KEY_PARENT_ID + " = ?");
        sqLiteStatement.bindString(1, Contact1);
        sqLiteStatement.bindString(2, Contact2);
        sqLiteStatement.bindString(3, Contact3);
        sqLiteStatement.bindString(4, Parent_ID);
        sqLiteStatement.executeUpdateDelete();

        db.close(); // Closing database connection

        Log.d(TAG, "Kid request was updated in SQLite: " + Parent_ID);
    }

    /**
     * Updating presence column in kids table in database
     */
    public void UpdateKidPresence(String Parent_ID, Integer Presence)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + TABLE_KIDS + " SET " + KEY_PRESENCE + " = ? WHERE "
                + KEY_PARENT_ID + " = ?");
        sqLiteStatement.bindString(1, Integer.toString(Presence));
        sqLiteStatement.bindString(2, Parent_ID);
        sqLiteStatement.executeUpdateDelete();

        db.close(); // Closing database connection

        Log.d(TAG, "Kid presence was updated in SQLite: " + Parent_ID);
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
     * Delete table from SQLite that get as parameter
     * @param table_name - Name of the table
     */
    public void deleteTable(String table_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(table_name, null, null);
        db.close();

        Log.d(TAG, table_name + " has been deleted from SQLite");
    }
}
