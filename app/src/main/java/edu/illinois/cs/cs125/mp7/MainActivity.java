package edu.illinois.cs.cs125.mp7;

import android.content.Context;
import android.content.Intent;
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


//http://food2fork.com/api/search URL
//http://food2fork.com/api/search?key={API_KEY}&q=shredded%20chicken REQUEST
public class MainActivity extends AppCompatActivity {
    private static String ingredients = null;
    //Default logging tag for messages from the main activity
    private static final String TAG = "MP7:Main";
    /**
     * Run when our activity comes into view.
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //Initially disable buttons until generate button clicked
        firstChoice.setEnabled(false);
        secondChoice.setEnabled(false);
        thirdChoice.setEnabled(false);
        //Text changed Listener to enable or disable button
        userIngredients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                generateButton.setEnabled(false);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
            ingredients = text.getText().toString();
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

        //Action for generate button, will call a method that generates recipe(s)
        generateButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Generate button click");
                if (ingredients == null) {
                    Toast.makeText(MainActivity.this, "Please enter ingredients.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    firstChoice.setEnabled(true);
                    secondChoice.setEnabled(true);
                    thirdChoice.setEnabled(true);
                    //show toast for entered ingredients
                    Toast.makeText(MainActivity.this, "Your ingredients are: "
                            + ingredients, Toast.LENGTH_SHORT).show();
                    //startsomemethod, use text field from this.ingredients
                }
            }
        });

        firstChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 1 Click");
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
            }
        });
        secondChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 2 Click");
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
            }
        });
        thirdChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Option 3 Click");
                Intent myIntent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(myIntent);
            }
        });

}
}
