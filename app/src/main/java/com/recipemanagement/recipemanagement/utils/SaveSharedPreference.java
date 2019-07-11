package com.recipemanagement.recipemanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.recipemanagement.recipemanagement.utils.PreferencesUtility.LOGGED_IN_PREF;
import static com.recipemanagement.recipemanagement.utils.PreferencesUtility.MY_TOKEN;

public class SaveSharedPreference {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF , loggedIn);
        editor.apply();
    }

    public static void setToken(Context context, String token){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(MY_TOKEN , token);
        editor.apply();
    }
    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }
    public static String getToken(Context context) {
        return getPreferences(context).getString(MY_TOKEN, "asd");
    }
}
