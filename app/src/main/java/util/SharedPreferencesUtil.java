package util;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SharedPreferencesUtil {

    private static final String MY_PREFS = "my_prefs";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    /**
     * Call this method from class that extends Application to maintain the same SharedPreferences state
     * throughout the app.
     * Do not call this method again from anywhere else
     * @param context : Application context
     */
    public static void initializeSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void saveEmail(String email) {
        editor.putString("email", email).apply();
    }

    public static void saveName(String name) {
        editor.putString("name", name).apply();
    }

    public static String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    public static String getName() {
        return sharedPreferences.getString("name", null);
    }

    public static void saveTutors(String tutors) {
        editor.putString("tutors", tutors).apply();
    }

    public static JSONObject getTutors() {
        String strJson = sharedPreferences.getString("tutors", null);
        JSONObject response = new JSONObject();

        if (strJson != null) {
            try {
                response = new JSONObject(strJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return response;

    }

    public static void setBookings (String bookings) {
        editor.putString("bookings",bookings).apply();

    }

    public static String getBookings () {
        return sharedPreferences.getString("bookings",null);

    }


    public static void clearSharedPrefsData() {
        editor.clear().commit();
    }
}
