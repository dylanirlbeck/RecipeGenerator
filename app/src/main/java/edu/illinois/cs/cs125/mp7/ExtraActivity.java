package edu.illinois.cs.cs125.mp7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;


public class ExtraActivity extends AppCompatActivity  {
    private static RequestQueue requestQueue;
    private static final String TAG = "MP7: ProcessRecipe";
    private static final String API_KEY = "027425cf9b8ec6677d75eb132622e862";
    public static String query = null;
    public static JSONObject responseJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        TextView textView = findViewById(R.id.showinfo);
        //make API call and then get the information
        apiCall(query);
//        textView.setText(ProcessRecipe.main(firstIngredients));
    }

    public void apiCall(final String query) {
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://food2fork.com/api/get?key=" + API_KEY + "&rId=" + query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(TAG, "This is a THE response " + response.toString());
                        responseJSON = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "this is the error " + error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}
