package com.example.bedalground;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavedSharedPreference {
    static final String USER_EMAIL = "useremail";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void setUserEmail(Context ctx, String userEmail){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.commit();
    }
    public static String getUserEmail(Context ctx){
        return getSharedPreferences(ctx).getString(USER_EMAIL, "");
    }
}
