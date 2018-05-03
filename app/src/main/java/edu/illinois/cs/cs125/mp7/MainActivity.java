package edu.illinois.cs.cs125.mp7;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


//http://food2fork.com/api/search URL
//http://food2fork.com/api/search?key={API_KEY}&q=shredded%20chicken REQUEST
public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "027425cf9b8ec6677d75eb132622e862";
    //save the recipe names and ID's that we are working with
    private static String firstChoiceRecipeName;
    private static String firstChoiceRecipeID;
    private static String secondChoiceRecipeName;
    private static String secondChoiceRecipeID;
    private static String thirdChoiceRecipeName;
    private static String thirdChoiceRecipeID;
    private static String ingredients = null;
    //save the counts of each choice and the page of search results, changed on generate or generate new click
    private static int firstChoicecount = 0;
    private static int secondChoicecount = 1;
    private static int thirdChoicecount = 2;
    private static int pagecount = 1;
    private static int arraySize;
    //Default logging tag for messages from the main activity
    private static final String TAG = "MP7:Main";

    private static RequestQueue requestQueue;

    /**
     * method for setting the recipe choices to display in the textbox
     */
    public void setRecipeChoices() {
        TextView firsttextview = findViewById(R.id.firstChoiceText);
        firsttextview.setText(firstChoiceRecipeName);
        TextView secondtextview = findViewById(R.id.secondChoiceText);
        secondtextview.setText(secondChoiceRecipeName);
        TextView thirdtextview = findViewById(R.id.thirdChoiceText);
        thirdtextview.setText(thirdChoiceRecipeName);
    }

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //code needed for fade transition after splash screen
        overridePendingTransition(R.xml.fadein, R.xml.fadeout);
        requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "onCreate ran");
        super.onCreate(savedInstanceState);

        //Load the main layout for our activity
        setContentView(R.layout.activity_main);
        //Text field for user ingredients
        final EditText userIngredients = (EditText) findViewById(R.id.ingredientInput);
        //Buttons for generating recipes, loading options
        final Button generateButton = (Button) findViewById(R.id.generateRecipes);

        final Button firstChoice = (Button) findViewById(R.id.firstChoice);

        final Button secondChoice = (Button) findViewById(R.id.secondChoice);

        final Button thirdChoice = (Button) findViewById(R.id.thirdChoice);

        final Button generateNew = (Button) findViewById(R.id.generateNewRecipes);

        final Button informationButton = (Button) findViewById(R.id.informationButton);

        //Initially disable buttons until generate button clicked
        firstChoice.setEnabled(false);
        secondChoice.setEnabled(false);
        thirdChoice.setEnabled(false);
        generateButton.setEnabled(false);
        generateNew.setEnabled(false);
        //Text changed Listener to enable or disable button
        userIngredients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                generateButton.setEnabled(false);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ingredients = s.toString();
                if (s.toString().equals("")) {
                    generateButton.setEnabled(false);
                } else {
                    generateButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        //Action for text field
        userIngredients.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView text, int actionId, KeyEvent event) {
                //save ingredients as a instance variable
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "Text field entered");
                    //close keyboard
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    handled = true;
                }
                return handled;
            }
        });

        //on generate new button click, increment json "recipe" array counters and get new info.
        generateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Generate New button click");
                // generate a new set of search results
                firstChoicecount += 3;
                secondChoicecount += 3;
                thirdChoicecount += 3;
                // check if the count has gotten bigger than the displayed results, max is 30
                if (firstChoicecount == 30) {
                    pagecount += 1;
                    firstChoicecount = 1;
                    secondChoicecount = 2;
                    thirdChoicecount = 3;
                }
                //make the request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://food2fork.com/api/search?key=" + API_KEY + "&q=" + ingredients + "&page=" + pagecount, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Log.d(TAG, "This is a THE response " + response.toString());
                                JsonParser parser = new JsonParser();
                                JsonObject result = parser.parse(response.toString()).getAsJsonObject();
                                JsonArray recipes = result.get("recipes").getAsJsonArray();
                                try {
                                    JsonObject option1 = recipes.get(firstChoicecount).getAsJsonObject();
                                    firstChoiceRecipeName = option1.get("title").getAsString();
                                    firstChoiceRecipeID = option1.get("recipe_id").getAsString();
                                } catch (IndexOutOfBoundsException e) {
                                    firstChoiceRecipeName = "No option";
                                    firstChoice.setEnabled(false);
                                    generateNew.setEnabled(false);

                                }
                                try {
                                    JsonObject option2 = recipes.get(secondChoicecount).getAsJsonObject();
                                    secondChoiceRecipeName = option2.get("title").getAsString();
                                    secondChoiceRecipeID = option2.get("recipe_id").getAsString();
                                } catch (IndexOutOfBoundsException e) {
                                    secondChoiceRecipeName = "No option";
                                    secondChoice.setEnabled(false);
                                    generateNew.setEnabled(false);
                                }
                                try {
                                    JsonObject option3 = recipes.get(thirdChoicecount).getAsJsonObject();
                                    thirdChoiceRecipeName = option3.get("title").getAsString();
                                    thirdChoiceRecipeID = option3.get("recipe_id").getAsString();
                                } catch (IndexOutOfBoundsException e) {
                                    thirdChoiceRecipeName = "No option";
                                    thirdChoice.setEnabled(false);
                                    generateNew.setEnabled(false);
                                }
                                setRecipeChoices();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(final VolleyError error) {
                                Log.d(TAG, "this is the error " + error.toString());
                            }
                        });
                requestQueue.add(jsonObjectRequest);
                generateNew.setEnabled(true);
            }
        });
        //Action for generate button, will call a method that generates three recipe names
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Generate button click");
                firstChoicecount = 0;
                secondChoicecount = 1;
                thirdChoicecount = 2;
                pagecount = 1;
                if (ingredients == null) {
                    Toast.makeText(MainActivity.this, "Please enter ingredients.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    //Let's make the initial request, and parse for the first three recipes and store their Recipe ID's
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://food2fork.com/api/search?key=" + API_KEY + "&q=" + ingredients + "&page=" + pagecount, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(final JSONObject response) {
                                    Log.d(TAG, "This is a THE response " + response.toString());
                                    JsonParser parser = new JsonParser();
                                    JsonObject result = parser.parse(response.toString()).getAsJsonObject();
                                    JsonArray recipes = result.get("recipes").getAsJsonArray();
                                    arraySize = recipes.size();


                                    try {
                                        JsonObject option1 = recipes.get(firstChoicecount).getAsJsonObject();
                                        firstChoiceRecipeName = option1.get("title").getAsString();
                                        firstChoiceRecipeID = option1.get("recipe_id").getAsString();
                                    } catch (IndexOutOfBoundsException e) {
                                        firstChoiceRecipeName = "No option";
                                        firstChoice.setEnabled(false);
                                        generateNew.setEnabled(false);
                                    }
                                    try {
                                        JsonObject option2 = recipes.get(secondChoicecount).getAsJsonObject();
                                        secondChoiceRecipeName = option2.get("title").getAsString();
                                        secondChoiceRecipeID = option2.get("recipe_id").getAsString();
                                    } catch (IndexOutOfBoundsException e) {
                                        secondChoiceRecipeName = "No option";
                                        secondChoice.setEnabled(false);
                                        generateNew.setEnabled(false);
                                    }
                                    try {
                                        JsonObject option3 = recipes.get(thirdChoicecount).getAsJsonObject();
                                        thirdChoiceRecipeName = option3.get("title").getAsString();
                                        thirdChoiceRecipeID = option3.get("recipe_id").getAsString();
                                    } catch (IndexOutOfBoundsException e) {
                                        thirdChoiceRecipeName = "No option";
                                        thirdChoice.setEnabled(false);
                                        generateNew.setEnabled(false);
                                    }
                                    setRecipeChoices();

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(final VolleyError error) {
                                    Log.d(TAG, "this is the error " + error.toString());
                                }
                            });
                    requestQueue.add(jsonObjectRequest);
                    //set buttons to extra activity and generate new equal to true, will disable them in response if errors
                    firstChoice.setEnabled(true);
                    secondChoice.setEnabled(true);
                    thirdChoice.setEnabled(true);
                    generateNew.setEnabled(true);
                    //show toast for entered ingredients
                    Toast.makeText(MainActivity.this, "Your ingredients are: "
                            + ingredients, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //stuff happens on first choice button click
        firstChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 1 Click");
                ExtraActivity.query = firstChoiceRecipeID;
                ExtraActivity.recipeTitleString = firstChoiceRecipeName;
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.xml.enter, R.xml.exit);

            }
        });
        //stuff happens on second choice click
        secondChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 2 Click");
                ExtraActivity.query = secondChoiceRecipeID;
                ExtraActivity.recipeTitleString = secondChoiceRecipeName;
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.xml.enter, R.xml.exit);
            }
        });
        //stuff happens on third choice click
        thirdChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 3 Click");
                ExtraActivity.query = thirdChoiceRecipeID;
                ExtraActivity.recipeTitleString = thirdChoiceRecipeName;
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.xml.enter, R.xml.exit);
            }
        });

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Information Button Click");
               Intent myIntent = new Intent(MainActivity.this, InformationActivity.class);
               startActivity(myIntent);
               overridePendingTransition(R.xml.enter, R.xml.exit);
           }

       });
    }
}