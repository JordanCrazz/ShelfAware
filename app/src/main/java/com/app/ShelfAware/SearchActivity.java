package com.app.ShelfAware;
//imports

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//a class to search for book results, and navigate to detailed BookViewActivity

public class SearchActivity extends AppCompatActivity {
    //setting up variables
    RadioButton RBTitle;
    RadioButton RBAuthor;
    private final static String SHARED_PREF_FILE_NAME = "com.app.shelfaware";
    SharedPreferences sharedPreferences;
    ConstraintLayout currentLayout;
    private BookAdapter bookAdapter;
    private ListView lvBooks;
    ArrayList<Book> abooks;
    private EditText searchBox;
    public static final String BOOK_DETAIL_KEY = "book";
    String url = "";

    @Override
    //when SearchActivity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //get shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        //get current main layout from id
        currentLayout = findViewById(R.id.main_layout);

        //get radioButtons from id
        RBTitle = findViewById(R.id.RBtitle);
        RBAuthor = findViewById(R.id.RBauthor);

        //get listview LVBooks from id
        lvBooks = findViewById(R.id.LVBooks);
        //create a new arrayList of Book objects
        ArrayList<Book> aBooks = new ArrayList<>();

        //create a bookAdapter object to convert book objects into the listView
        bookAdapter = new BookAdapter(this, aBooks);
        //set the bookAdapter to the listView
        lvBooks.setAdapter(bookAdapter);

        //get editText box from id
        searchBox = findViewById(R.id.ETsearch);

        //configure buttons methods
        configureBackButton();
        configureRadioButtons();
        configureSearchBox();

        //configure listview selected listener
        configureBookSelectedListener();

        //light mode preference setup
        boolean prefBGLight = sharedPreferences.getBoolean("BGLight", false);
        if (!prefBGLight){
            currentLayout.setBackgroundColor(Color.DKGRAY);
        }
        else {
            currentLayout.setBackgroundColor(Color.WHITE);
        }

        //get intent extras
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //if there are intent extras
        if(extras != null){
            //create a new string to store author
            String author = (String) getIntent().getSerializableExtra(BookViewActivity.AUTHOR_DETAIL_KEY);
            //set searchbox contents to new author string
            searchBox.setText(author, TextView.BufferType.EDITABLE);
            //set radiobutton RBAuthor to true
            RBAuthor.setChecked(true);
            //set radiobutton RBTitle to false
            RBTitle.setChecked(false);
            //send a search query
            searchSent();


        }
    }

    //a function to configure the listView for when a book is selected
    public void configureBookSelectedListener() {
        //set a listener to the listview
        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //when an item is clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create a new intent to BookViewActivity
                Intent intent = new Intent(SearchActivity.this, BookViewActivity.class);
                //add extra to intent containing information on selected Item
                intent.putExtra(BOOK_DETAIL_KEY, bookAdapter.getItem(position));
                //start new Activity
                startActivity(intent);
            }
        });
    }

    //a function to configure the search box
    private void configureSearchBox() {
        //create a new action listener on the searchBox
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            //on EditorAction (keyboard prompt is sent through searchBox)
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if action is being sent
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    //send a search query
                    searchSent();
                }
                return false;
            }
        });
    }

    //a function to send a search query to the OpenLibrary API
    private void searchSent() {
        //create a new string to store the search query
        String query = searchBox.getText().toString();
        //parse query to replace spaces with "+"
        query = query.replace(" ", "+");
        //if RBTitle radiobutton is checked
        if (RBTitle.isChecked()){
            try {
                //create a new url searching by title, encoding the query
                url = ("https://openlibrary.org/search.json?title=" + URLEncoder.encode(query, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                //error handling
                e.printStackTrace();
            }
            //populate the listView with the specified search
            populateList(url);
        }else{

            try {
                //create a new url searching by author, encoding the query
                url = ("https://openlibrary.org/search.json?author=" + URLEncoder.encode(query, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                //error handling
                e.printStackTrace();
            }
            //populate the listView with the specified search
            populateList(url);
        }
    }



    //a function to populate the listView with book results from the search query
    private void populateList(String url) {
        //set the abooks ArrayList to null
        abooks = null;
        //create a new JsonObjectRequest to get a JSONObject
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    //on expected response
                    public void onResponse(JSONObject response) {
                        try {
                            //set abooks arraylist to JSONArray "docs" from the API response
                            abooks = Book.fromJson(response.getJSONArray("docs"));
                            //clear the listview
                            bookAdapter.clear();
                            //for each book in the abooks ArrayList
                            for (Book book : abooks) {
                                // add book through the adapter
                                bookAdapter.add(book);
                            }
                            //notify that the data set has been changed
                            bookAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            //error handling
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    //on unexpected response
                    public void onErrorResponse(VolleyError error) {
                        //clear the listView
                        bookAdapter.clear();
                    }
                });


        // Access the RequestQueue through singleton class
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    //function to configure the back button
    private void configureBackButton(){
        //find the back button by it's ID
        ImageView backButton = findViewById(R.id.IVbackButton);
        //set a listener to the back button for when it is clicked
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            //when the back button is clicked
            public void onClick(View view){
                //start a new MainActivity activity
                //startActivity used instead of finish for updating purposes
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
            }
        });
    }

    //function to configure the radiobuttons
    private void configureRadioButtons(){
        //set RBTitle to be checked by default
        RBTitle.setChecked(true);
        //on RBTitle being clicked
        RBTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uncheck the RBAuthor radiobutton
                RBAuthor.setChecked(false);
            }
        });
        //on RBAuthor being clicked
        RBAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uncheck the RBTitle radiobutton
                RBTitle.setChecked(false);
            }
        });
    }

}