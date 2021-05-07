package com.example.bikewash.utility;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bikewash.utility.Constants.FILE_DATA;
import static com.example.bikewash.utility.Constants.RUNNING_NUMBER;
import static com.example.bikewash.utility.Constants.UID;
import static com.example.bikewash.utility.Constants.USER_EXIST;
public class SharePreference {

    public static void setUserData(Context context, String userExist) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( USER_EXIST, userExist );
        editor.apply();
    }

    public static String getUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, Context.MODE_PRIVATE );
        String userExist = "";
        if (sharedPreferences.contains( USER_EXIST )) {
            userExist = sharedPreferences.getString( USER_EXIST, "" );
        }
        return userExist;
    }

    public static void setRunningNumber(Context context, String childName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( RUNNING_NUMBER, childName );
        editor.apply();
    }

    public static String getRunningNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, Context.MODE_PRIVATE );
        String runningNumber = "";
        if (sharedPreferences.contains( RUNNING_NUMBER )) {
            runningNumber = sharedPreferences.getString( RUNNING_NUMBER, "" );
        }
        return runningNumber;
    }

    public static void setUID(Context context, String uid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( UID, uid );
        editor.apply();
    }

    public static String getUID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, Context.MODE_PRIVATE );
        String uid = "";
        if (sharedPreferences.contains( UID )) {
            uid = sharedPreferences.getString( UID, "" );
        }
        return uid;
    }

    public static void removeUidKeyRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove( UID );
        editor.remove( RUNNING_NUMBER );
        editor.apply();
    }
}
