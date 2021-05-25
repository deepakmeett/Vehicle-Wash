package com.dexter.flex.utility;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.dexter.flex.utility.Constants.FILE_DATA;
import static com.dexter.flex.utility.Constants.HOW_TO_USE;
import static com.dexter.flex.utility.Constants.RUNNING_NUMBER;
import static com.dexter.flex.utility.Constants.UID;
import static com.dexter.flex.utility.Constants.USER_EXIST;
import static com.dexter.flex.utility.Constants.WASHER_KEY;
public class SharePreference {

    public static void setWasherKey(Context context, String washerKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( WASHER_KEY, washerKey );
        editor.apply();
    }

    public static String getWasherKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, Context.MODE_PRIVATE );
        String userKey = "";
        if (sharedPreferences.contains( WASHER_KEY )) {
            userKey = sharedPreferences.getString( WASHER_KEY, "" );
        }
        return userKey;
    }

    public static void setUserExit(Context context, String userExist) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( USER_EXIST, userExist );
        editor.apply();
    }

    public static String getUserExit(Context context) {
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
    
    public static void setHowTo(Context context, String howTo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( HOW_TO_USE, howTo );
        editor.apply();
    }

    public static String getHowTo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, Context.MODE_PRIVATE );
        String howTo = "";
        if (sharedPreferences.contains( HOW_TO_USE )) {
            howTo = sharedPreferences.getString( HOW_TO_USE, "" );
        }
        return howTo;
    }

    public static void removeUidKeyRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove( UID );
        editor.remove( RUNNING_NUMBER );
        editor.apply();
    }

    public static void removeWasherKeyUserExit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_DATA, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove( WASHER_KEY );
        editor.remove( USER_EXIST );
        editor.apply();
    }
}
