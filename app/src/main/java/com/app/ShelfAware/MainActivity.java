package com.app.ShelfAware;

//imports

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

//The main activity which will be loaded when the app begins. Navigation to the search activity and settings activity, as well as most recent viewed BookView.


public class MainActivity extends AppCompatActivity {
    //setting up variables
    public static final String BOOK_DETAIL_KEY = "book";
    private final static String SHARED_PREF_FILE_NAME = "com.app.shelfaware";
    private SharedPreferences sharedPreferences;
    FrameLayout currentLayout;
    Book book1;
    Book book2;
    Book book3;


    @Override
    //when main activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get shared preferences file
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        //find current main layout by id
        currentLayout = findViewById(R.id.main_layout);

        //get light mode preference from sharedpreferences
        boolean prefBGLight = sharedPreferences.getBoolean("BGLight", true);
        //if prefBGLight is not true
        if (!prefBGLight){
            //background colour set to dark gray
            currentLayout.setBackgroundColor(Color.DKGRAY);
        }
        //otherwise if prefBGLight is true
        else {
            //background colour set to white
            currentLayout.setBackgroundColor(Color.WHITE);
        }

        //configure buttons methods
        configureSearchButton();
        configureSettingsButton();
        configureBookButtons();
    }

    //a method to set the configuration for search button
    private void configureSearchButton(){
        //find the search button by it's ID
        ImageButton searchButton = findViewById(R.id.IBsearch);
        //set a listener to the search button for when it is clicked
        searchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            //when the search button is clicked
            public void onClick(View view){
                //start a new SearchActivity
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    //a method to set the configuration for the settings button
    private void configureSettingsButton(){
        //find the settings button by it's ID
        ImageButton settingsButton = findViewById(R.id.IBsettings);
        //set a listener to the settings button for when it is clicked
        settingsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            //when the settings button is clicked
            public void onClick(View view){
                //start a new SettingsActivity
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    //a method to set the configuration for the book cover buttons, with most recently viewed titles
    private void configureBookButtons(){
        //find the book images by their ID
        final ImageView cover1 = findViewById(R.id.IVcover1);
        final ImageView cover2 = findViewById(R.id.IVcover2);
        final ImageView cover3 = findViewById(R.id.IVcover3);

        //if book1 exists in sharedpreferences
        if (sharedPreferences.contains("book1")){
            //create a new URL with a path to book1 from the openLibrary
            String book1url = ("https://openlibrary.org/books/" + sharedPreferences.getString("book1", "") + ".json");
            //Create a new jsonObjectRequest, to get a JSONObject from the url
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, book1url, null, new Response.Listener<JSONObject>() {
                        @Override
                        //if expected response
                        public void onResponse(JSONObject response) {
                                //set book1 to be a new book object, with book1 data
                                book1 = Book.fromJsonBooks(response);
                        }
                    }, new Response.ErrorListener() {
                        //error handling
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            // Access the RequestQueue through singleton class, passing the JsonObjectRequest
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

            //creating a new URL for the "book1" cover from the openlibrary API
            String coverUrl1 = ("https://covers.openlibrary.org/b/olid/"+sharedPreferences.getString("book1", "")+"-M.jpg");
            //create a new ImageRequest, to get a Bitmap from the url
            ImageRequest request = new ImageRequest(coverUrl1,
                    new Response.Listener<Bitmap>() {
                        @Override
                        //on expected response
                        public void onResponse(Bitmap bitmap) {
                            //set cover1 to the url image
                            cover1.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        //if unexpected response
                        public void onErrorResponse(VolleyError error) {
                            //set cover1 to default book cover
                            cover1.setImageResource(R.drawable.bookcover);
                        }
                    });
            //Access the RequestQueue through singleton class, passing the ImageRequest
            MySingleton.getInstance(this).addToRequestQueue(request);
            //create a new onclicklistener for cover1
            cover1.setOnClickListener(new View.OnClickListener() {
                @Override
                //when cover1 is clicked
                public void onClick(View view) {
                    //create a new intent to the BookView activity
                    Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                    //pass book1 as extra to BookView
                    intent.putExtra(BOOK_DETAIL_KEY, book1);
                    //start new BookView Activity
                    startActivity(intent);
                }
            });
        }



        //book2 same as book1, but second cover image
        if (sharedPreferences.contains("book2")){
            String book2url = ("https://openlibrary.org/books/" + sharedPreferences.getString("book2", "") + ".json");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, book2url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            book2 = Book.fromJsonBooks(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            String coverUrl2 = ("https://covers.openlibrary.org/b/olid/"+sharedPreferences.getString("book2", "")+"-M.jpg");
            ImageRequest request = new ImageRequest(coverUrl2,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            cover2.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            cover2.setImageResource(R.drawable.bookcover);
                        }
                    });
            MySingleton.getInstance(this).addToRequestQueue(request);
            cover2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                    intent.putExtra(BOOK_DETAIL_KEY, book2);
                    startActivity(intent);
                }
            });
        }


        //book3 same as book1 and 2, but third cover image
        if (sharedPreferences.contains("book3")){
            String book3url = ("https://openlibrary.org/books/" + sharedPreferences.getString("book3", "") + ".json");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, book3url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            book3 = Book.fromJsonBooks(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            String coverUrl3 = ("https://covers.openlibrary.org/b/olid/"+sharedPreferences.getString("book3", "")+"-M.jpg");
            ImageRequest request = new ImageRequest(coverUrl3,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            cover3.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            cover3.setImageResource(R.drawable.bookcover);
                        }
                    });
            MySingleton.getInstance(this).addToRequestQueue(request);
            cover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                    intent.putExtra(BOOK_DETAIL_KEY, book3);
                    startActivity(intent);
                }
            });
        }
    }
}