package studentandtutors.geekbiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ArrayList<String> tutors = new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        final EditText zip_view = (EditText) view.findViewById(R.id.city_zip);
        final ImageView search_img = (ImageView) view.findViewById(R.id.search_button);
        ListView listView = (ListView) view.findViewById(R.id.search_list_view);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_zip = zip_view.getText().toString();
                getTutorsFromZip(search_zip);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,tutors);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getTutorsFromZip(String zip) {
        String url = "https://geekbiz.azurewebsites.net/search_tutors.php?zip="+ "'"+zip+"'";
        Log.v("user_login",url);

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
                                tutors.add(tutor.getFname() + "  " + String.valueOf(tutor.getWage())+ " "+ tutor.getCity());
                            }

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

    private void showToast(String s) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this.getContext(), s, duration);toast.show();

    }


}
