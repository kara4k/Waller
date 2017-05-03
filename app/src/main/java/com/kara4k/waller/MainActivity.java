package com.kara4k.waller;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.rarepebble.colorpicker.ColorPreference;

import java.io.IOException;

public class MainActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private static final String HOME_COLOR = "home";
    private static final String APPLY_HOME = "apply_home";
    private static final String LOCK_COLOR = "lock";
    private static final String APPLY_LOCK = "apply_lock";
    private static final String PREF_SCREEN = "screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        PreferenceScreen prefScreen = (PreferenceScreen) findPreference(PREF_SCREEN);
        ColorPreference homePref = (ColorPreference) findPreference(HOME_COLOR);
        Preference applyHomePref = (Preference) findPreference(APPLY_HOME);
        ColorPreference lockPref = (ColorPreference) findPreference(LOCK_COLOR);
        Preference applyLockPref = (Preference) findPreference(APPLY_LOCK);
        applyHomePref.setOnPreferenceClickListener(this);
        applyLockPref.setOnPreferenceClickListener(this);

        ifHideLockPref(prefScreen, lockPref, applyLockPref);


    }

    private void ifHideLockPref(PreferenceScreen prefScreen, ColorPreference lockPref, Preference applyLockPref) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            prefScreen.removePreference(lockPref);
            prefScreen.removePreference(applyLockPref);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (preference.getKey().equals(APPLY_HOME)) {
            setHomeWallpaper(sp);
        } else if (preference.getKey().equals(APPLY_LOCK)) {
            setLockWallpaper(sp);
        }
        return true;
    }

    private void setLockWallpaper(SharedPreferences sp) {
        Bitmap bitmap = getBitmap(sp, LOCK_COLOR);
        trySetOnNewVersion(bitmap, WallpaperManager.FLAG_LOCK);
    }

    private void setHomeWallpaper(SharedPreferences sp) {
        Bitmap bitmap = getBitmap(sp, HOME_COLOR);
        trySetHomeWallpaper(bitmap);
    }

    private void trySetHomeWallpaper(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            trySetHomeOnOldVersions(bitmap);
        } else {
            trySetOnNewVersion(bitmap, WallpaperManager.FLAG_SYSTEM);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void trySetOnNewVersion(Bitmap bitmap, int flag) {
        try {
            WallpaperManager.getInstance(this).setBitmap(bitmap, null, false, flag);
        } catch (IOException e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void trySetHomeOnOldVersions(Bitmap bitmap) {
        try {
            WallpaperManager.getInstance(this).setBitmap(bitmap);
            Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }



    @NonNull
    private Bitmap getBitmap(SharedPreferences sp, String key) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        int color = sp.getInt(key, 0);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }
}
