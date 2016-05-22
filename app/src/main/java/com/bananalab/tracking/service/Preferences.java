package com.bananalab.tracking.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.bananalab.tracking.model.Account;

import java.text.SimpleDateFormat;

/**
 * Created by batmaster on 5/2/16 AD.
 */
public class Preferences {

    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final String PREF = "TRACKING_PREF";

    public static final String TRACKING_ID_TEMP = "TRACKING_ID_TEMP";

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void removeString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getInt(key, -1);
    }

    public static void removeInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setAccount(Context context, Account account) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("account.email", account.getEmail());
        editor.putString("account.name", account.getName());
        editor.putString("account.imageUrl", account.getImageUrl());
        editor.commit();
    }

    public static void removeAccount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("account.email");
        editor.remove("account.name");
        editor.remove("account.imageUrl");
        editor.commit();
    }

    public static Account getAccount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if (!sp.contains("account.email"))
            return null;

        return new Account(sp.getString("account.email", ""), sp.getString("account.name", ""), sp.getString("account.imageUrl", ""));
    }
}
