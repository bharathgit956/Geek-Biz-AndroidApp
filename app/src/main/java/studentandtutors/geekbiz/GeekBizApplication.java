package studentandtutors.geekbiz;

import android.app.Application;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jakewharton.threetenabp.AndroidThreeTen;
import util.SharedPreferencesUtil;

public class GeekBizApplication extends MultiDexApplication {

    private RequestQueue requestQueue;
    private static GeekBizApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        System.setProperty("http.keepAlive", "false");
        SharedPreferencesUtil.initializeSharedPreferences(this);
        AndroidThreeTen.init(this);
    }

    public static synchronized GeekBizApplication getInstance() {
        return mInstance;
    }


    /**
     * Create a getRequestQueue() method to return the instance of
     * RequestQueue.This kind of implementation ensures that
     * the variable is instantiated only once and the same
     * instance is used throughout the application
     *
     * @return : RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    /**
     * Public method to add the Request to the the single
     * instance of RequestQueue created before.
     * @param request : Volley request
     * @param tag : Setting a tag to every request helps in grouping them. Tags act as identifier
     * for requests and can be used while cancelling them
     */
    public void addToRequestQueue(Request request, String tag) {
        Log.v("request queue", "added");
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * Cancel all the requests matching with the given tag
     * @param tag : Tags act as identifier for requests and can be used while cancelling them
     */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
