package edu.illinois.cs.cs125.mp7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;


public class ExtraActivity extends AppCompatActivity  {
    private static RequestQueue requestQueue;
    private static final String TAG = "MP7: ProcessRecipe";
    private static final String API_KEY = "027425cf9b8ec6677d75eb132622e862";
    public static String query = null;
    public static JSONObject responseJSON = null;
    public static String urlString;
    public static String[] ingredientsArray;
    public static double socialRank;
    public static String recipeTitleString;


    //transition in case of back button click
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        //make API call and then get the information
        apiCall(query);





}

    public void apiCall(final String query) {
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://food2fork.com/api/get?key=" + API_KEY + "&rId=" + query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(TAG, "This is a THE response " + response.toString());
                        JsonParser parser = new JsonParser();
                        JsonObject result = parser.parse(response.toString()).getAsJsonObject();
                        JsonObject recipe = result.getAsJsonObject("recipe");
                        urlString = recipe.get("source_url").getAsString();
                        JsonArray jsonIngredientsArr = recipe.getAsJsonArray("ingredients");
                        ingredientsArray = new String[jsonIngredientsArr.size()];
                        for (int i = 0; i < jsonIngredientsArr.size(); i++) {
                            ingredientsArray[i] = jsonIngredientsArr.get(i).getAsString() + "\n";
                        }
                        socialRank = recipe.get("social_rank").getAsDouble();
                        setView();
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

    public void setView() {
        TextView title = findViewById(R.id.recipeTitle);
        title.setText(recipeTitleString);

        TextView url = findViewById(R.id.url);
        url.setText(urlString);

        EditText editText = findViewById(R.id.ingredientList);
        String allIngredients = String.join("\n", ingredientsArray);
        editText.setText(allIngredients);

        TextView socR = findViewById(R.id.socialRank);
        String socialRankString = "Social Rank: " + socialRank;
        socR.setText(socialRankString);


    }



}
