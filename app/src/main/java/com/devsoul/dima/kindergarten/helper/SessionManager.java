package com.devsoul.dima.kindergarten.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 *  This class maintains session data across the app using the SharedPreferences.
 *  We store a boolean flag isLoggedIn in shared preferences to check the login status.
 */
public class SessionManager
{
    public static final String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;
    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "LoginApi";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    // Constructor
    public SessionManager(Context context)
    {
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setLogin(boolean isLoggedIn)
    {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG,"user login modified in pref");
    }

    public boolean isLoggedIn()
    {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
