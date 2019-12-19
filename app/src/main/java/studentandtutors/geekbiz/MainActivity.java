package studentandtutors.geekbiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    private ArrayList<String> tutors = new ArrayList<>();
    private ArrayList<Tutor> tutors_list = new ArrayList<>();
    private ArrayList<String> free_slots = new ArrayList<>();

    private ArrayList<String> slotid = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        final EditText zip_view = (EditText) findViewById(R.id.city_zip);
        final EditText wage_view = (EditText) findViewById(R.id.wage);
        final EditText gender_view = (EditText) findViewById(R.id.gender);

        final ImageView search_img = (ImageView) findViewById(R.id.search_button);
        final ListView listView = (ListView) findViewById(R.id.search_list_view);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_zip = zip_view.getText().toString();
                String search_wage = wage_view.getText().toString();
                if (search_wage.isEmpty()) {
                    search_wage = "1000";
                }
                String gender1 = gender_view.getText().toString();
                String gender2 = gender_view.getText().toString();
                if (gender1.isEmpty()) {
                    gender1 = "male";
                    gender2 = "female";
                }
                getTutorsFromZip(search_zip,search_wage,gender1,gender2,listView);
            }
        });


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_dashboard:
                        showBookings(zip_view,wage_view,gender_view,search_img,listView);
                        break;

                    case R.id.navigation_home:
                        setVisibility(zip_view,wage_view,gender_view,search_img,listView);
                        break;

                }

                return true;
            }
        });
    }

    private void setVisibility(EditText zip_view, EditText wage_view, EditText gender_view, ImageView search_img, ListView listView) {
        zip_view.setVisibility(View.VISIBLE);
        wage_view.setVisibility(View.VISIBLE);
        gender_view.setVisibility(View.VISIBLE);
        search_img.setVisibility(View.VISIBLE);
    }

    private void showBookings(EditText zip_view, EditText wage_view, EditText gender_view, ImageView search_img, ListView listView) {
        zip_view.setVisibility(View.GONE);
        wage_view.setVisibility(View.GONE);
        gender_view.setVisibility(View.GONE);
        search_img.setVisibility(View.GONE);
        ArrayList<String> bookings = new ArrayList<>();
        String booking = SharedPreferencesUtil.getBookings();
        bookings.add(booking);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,bookings);

        listView.setAdapter(adapter);


    }

    private void getTutorsFromZip(String search_zip, String search_wage, String gender1,String gender2, final ListView listView) {
        String url = "https://geekbiz.azurewebsites.net/search_tutors.php?zip="+ "'"+search_zip+"'" +"&wage="+search_wage+"&gender1="+"'"+gender1+"'"+"&gender2="+"'"+gender2+"'";
        Log.v("user_login",url);
        tutors = new ArrayList<String>();

        JsonObjectRequest userRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int numberOfEntries = 0;
                        try {
                            numberOfEntries = response.getInt("numberOfEntries");
                            if ( numberOfEntries > 0) {
                                for (int i = 0; i < numberOfEntries; i++) {

                                    JSONObject TutorJSONObject = response.getJSONArray("tutors").getJSONObject(i);

                                    Tutor tutor = new Tutor(
                                            TutorJSONObject.getString("aid"),
                                            TutorJSONObject.getString("fname"),
                                            TutorJSONObject.getString("lname"),
                                            TutorJSONObject.getString("edlevel"),
                                            TutorJSONObject.getInt("wage"),
                                            TutorJSONObject.getString("city"),
                                            i);
                                    System.out.println(tutor.getFname());
                                    tutors_list.add(tutor);
                                    tutors.add(tutor.getFname() + ", " + "wage: "+ String.valueOf(tutor.getWage())+ ", "+ tutor.getCity());
                                }
                                showListview(listView);

                            } else {
                                showToast("No entries found for the zipcode");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.v("user_login", response.toString());
                        System.out.println(tutors);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Network error");

                        Log.v("user_login",error.toString());
                    }
                });
        GeekBizApplication.getInstance().addToRequestQueue(userRequest, "user details");

    }

    private void showListview(final ListView listView) {
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,tutors);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populateTutorView(listView, tutors_list.get(position));
            }
        });
    }

    private void populateTutorView(final ListView listView, final Tutor tutor) {
        String url = "https://geekbiz.azurewebsites.net/get_free_slots.php?tutid="+ "'"+tutor.getAid()+"'";
        Log.v("user_login",url);
        free_slots = new ArrayList<String>();
        free_slots.add(tutor.getFname() + " Free Slots");
        JsonObjectRequest userRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int numberOfEntries = 0;
                        try {
                            numberOfEntries = response.getInt("numberOfEntries");
                            if ( numberOfEntries > 0) {
                                for (int i = 0; i < numberOfEntries; i++) {
                                    JSONObject TutorJSONObject = response.getJSONArray("user").getJSONObject(i);
                                    free_slots.add(TutorJSONObject.getString("weekid")
                                          + " "+  TutorJSONObject.getString("startTime"));
                                    slotid.add(TutorJSONObject.getString("slotid"));
                                }
                                showListview_2(listView,free_slots,tutor.getAid(),tutor.getFname());

                            } else {
                                showToast("No entries found for the zipcode");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.v("user_login", response.toString());
                        System.out.println(tutors);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Network error");

                        Log.v("user_login",error.toString());
                    }
                });
        GeekBizApplication.getInstance().addToRequestQueue(userRequest, "user details");

    }

    private void showListview_2(final ListView listView, ArrayList<String> free_slots, final String aid, final String fname) {
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,free_slots);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeTheBooking(slotid.get(position),aid,fname,position);
            }
        });

    }

    private void makeTheBooking(String slotid, String aid, final String fname, final int position) {
        String studid = SharedPreferencesUtil.getEmail();
        String url = "https://geekbiz.azurewebsites.net/add_booking.php?tutid="+ "'"+aid+"'"+"&slotid=" + "'"+slotid+"'"+"&studid="+"'"+studid+"'";
        http://geekbiz.azurewebsites.net/add_booking.php?tutid=%27abcdef%27&slotid=%2725%27&studid=%27bkk48%27
        Log.v("user_login",url);
        JsonObjectRequest userRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String message = "";
                        try {
                            message = response.getString("message");
                            if ( message.equalsIgnoreCase("Booking Confirmed")) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Your Booking")
                                        .setMessage("Your booking with " + fname + " at "+ free_slots.get(position) + " has been confirmed!" )
                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                saveBookingDetails(fname, free_slots.get(position));
                                                startMainActivity();
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                showToast("Booking Not Confirmed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.v("user_login", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Network error");

                        Log.v("user_login",error.toString());
                    }
                });
        GeekBizApplication.getInstance().addToRequestQueue(userRequest, "user details");

    }

    private void saveBookingDetails(String fname, String s) {
        SharedPreferencesUtil.initializeSharedPreferences(this);
        SharedPreferencesUtil.setBookings("Tutor:"+fname + "   "+s);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String s) {
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(this, s, duration);toast.show();
    }

}
