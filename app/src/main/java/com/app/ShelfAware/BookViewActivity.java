package com.app.ShelfAware;
//imports

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.app.ShelfAware.MainActivity.BOOK_DETAIL_KEY;

//a class to view more details about a selected book

public class BookViewActivity extends AppCompatActivity {
    //setting up variables
    private final static String SHARED_PREF_FILE_NAME = "com.app.shelfaware";
    SharedPreferences sharedPreferences;
    ConstraintLayout currentLayout;
    SharedPreferences.Editor prefsEditor;
    public static final String AUTHOR_DETAIL_KEY = "author";

    //setting up UI objects
    TextView tvTitle;
    TextView tvAuthor;
    TextView tvDescription;
    ImageView ivCover;
    Button bShare;
    Button bInfo;
    Button bPurchase;
    Button bRecommend;
    String newBookId;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);

        //getting sharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        //creating preference editor
        prefsEditor = sharedPreferences.edit();

        //getting current main layout by id
        currentLayout = findViewById(R.id.main_layout);

        //getting UI elements by id
        tvTitle = findViewById(R.id.TVTitle);
        tvAuthor = findViewById(R.id.TVauthor);
        tvDescription = findViewById(R.id.TVdescription2);
        ivCover = findViewById(R.id.IVbookCoverLarge);
        bRecommend = findViewById(R.id.BtnRecommend);

        //getting book object from extra intent data
        Book book = (Book) getIntent().getSerializableExtra(BOOK_DETAIL_KEY);

        //setting up id from book
        assert book != null;
        newBookId = book.getOpenLibraryId();

        //setting light mode from preferences
        boolean prefBGLight = sharedPreferences.getBoolean("BGLight", true);
        if (!prefBGLight) {
            currentLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            currentLayout.setBackgroundColor(Color.WHITE);
        }

        //configure information from book
        configureInformation(book);

        //configure back button
        configureBackButton();



    }

    //a function to add the most recently viewed book to the "recent books" preferences
    private void reorderBooks() {
        //creating a queue system using 3  Strings from sharedPreferences
        if (sharedPreferences.contains("book2")){
            prefsEditor.putString("book3", sharedPreferences.getString("book2", ""));
            prefsEditor.apply();}
        if (sharedPreferences.contains("book1")){
            prefsEditor.putString("book2", sharedPreferences.getString("book1", ""));
            prefsEditor.apply();}
        prefsEditor.putString("book1", newBookId);
        prefsEditor.apply();
    }

    //a function to set up all book information when recieved
    private void configureInformation(final Book book) {
        //getting buttons by id
        bShare = findViewById(R.id.BtnShare);
        bInfo = findViewById(R.id.BtnInfo);
        bPurchase = findViewById(R.id.BtnPurchase);

        //setting textviews and title
        this.setTitle(book.getTitle());
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());

        //creating a new imagerequest to get a large version of the book cover as a bitmap
        ImageRequest request = new ImageRequest(book.getLargeCoverUrl(),
                    new Response.Listener<Bitmap>(){
                        @Override
                        //on expected result
                        public void onResponse(Bitmap bitmap) {
                            //set cover to bitmap
                            ivCover.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            //set cover to default cover image
                            ivCover.setImageResource(R.drawable.bookcover);
                        }
                    });
            //using singleton to run request
            MySingleton.getInstance(this).addToRequestQueue(request);

            //getting single book query from openlibrary api
            String url = ("https://openlibrary.org/works/" + book.getOpenLibraryId() + ".json");
            //creating new JsonObjectRequest to get a JsonObject (more detailed information than exta intent book
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            //set description from response json
                            tvDescription.setText(response.getString("description"));

                        } catch (JSONException e) {
                            //error handling
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //if not found, set error text
                        tvDescription.setText("no description found");
                    }
                });
        // Access the RequestQueue through singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        //when bShare Button is clicked, go to open library page
        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            //when the back button is clicked
            public void onClick(View view) {
                Uri shareUrl = Uri.parse("https://openlibrary.org/books/" + book.getOpenLibraryId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(shareUrl);
                startActivity(intent);
            }
        });

        //when bInfo Button is clicked, go to open library page
        bInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            //when the back button is clicked
            public void onClick(View view) {
                Uri shareUrl = Uri.parse("https://openlibrary.org/books/" + book.getOpenLibraryId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(shareUrl);
                startActivity(intent);
            }
        });

        //when bPurchase Button is clicked, go to open library page
        bPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            //when the back button is clicked
            public void onClick(View view) {
                Uri shareUrl = Uri.parse("https://openlibrary.org/books/" + book.getOpenLibraryId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(shareUrl);
                startActivity(intent);
            }
        });

        //when bRecommend Button is clicked, go to open new search activity, with extra intent of author name
        bRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            //when the back button is clicked
            public void onClick(View view) {
                Intent intent = new Intent(BookViewActivity.this, SearchActivity.class);
                intent.putExtra(AUTHOR_DETAIL_KEY, book.getAuthor());
                startActivity(intent);
            }
        });
        //set current book to first on list of recent books, dropping last
        reorderBooks();
    }


    //function to configure back button
    private void configureBackButton() {
        //find the back button by it's ID
        ImageView backButton = findViewById(R.id.IVbackButton);
        //set a listener to the back button for when it is clicked
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            //when the back button is clicked
            public void onClick(View view) {
                //finish the activity, returning to the previous activity
                finish();
            }
        });
    }
}