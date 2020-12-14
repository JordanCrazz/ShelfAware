package com.app.ShelfAware;
//imports

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

//a class to parse Json data as a book object, with functions to set up book objects and related.

public class Book implements Serializable {
    //setting up variables
    private String openLibraryId;
    private String author;
    private String title;

    //method to get id of book object
    public String getOpenLibraryId() {
        return openLibraryId;
    }

    //method to get title of book object
    public String getTitle() {
        return title;
    }

    //method to get author of book object
    public String getAuthor() {
        return author;
    }

    //get URL for thumbnail sized book cover image from covers API
    public String getCoverThumbs(String libraryID){
        return "https://covers.openlibrary.org/b/olid/" + libraryID + "-M.jpg?default=false";
    }

    // Get URL for medium sized book cover from covers API
    public String getCoverUrl() {
        return "https://covers.openlibrary.org/b/olid/" + openLibraryId + "-M.jpg?default=false";
    }

    // Get URL for large sized book cover from covers API
    public String getLargeCoverUrl() {
        return "https://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }

    // Returns a Book object given the expected JSON
    public static Book fromJson(JSONObject jsonObject) {
        //set up new book object
        Book book = new Book();
        try {
            // Deserialize json into individual elements

            //get book openLibraryId
            if (jsonObject.has("cover_edition_key")) {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if(jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }

            //get book title from Json
            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            //get book author from Json
            book.author = getAuthor(jsonObject);

        } catch (JSONException e) {
            //error handling
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    //returns a book object given alternate Json data from single book search
    public static Book fromJsonBooks(JSONObject jsonObject) {
        //create new book object
        Book book = new Book();

        try {
            // Deserialize json into object fields
            //get book openLibraryId
            if (jsonObject.has("key")) {
                //get id from Json
                String id = jsonObject.getString("key");
                //remove anything preceding and including "/" from id (inconsistent Json data solution)
                id = id.substring(id.lastIndexOf("/")+1);
                //set book id
                book.openLibraryId = id;

            }
            //get book author
            if (jsonObject.has("authors")) {
                //get author from json
                String authors = jsonObject.getString("authors");
                //remove anything preceding and including "/" from author (inconsistent Json data solution)
                authors = authors.substring(authors.lastIndexOf("/")+1);
                //set book author  (often returns author ID, which would require another API call to solve)
                book.author = authors;
            }
            //get book title from json
            book.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
        } catch (JSONException e) {
            //error handling
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }


    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject jsonObject) {
        try {
            //get JSONArray of authors
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            //get length of authors JSONArray
            int numAuthors = authors.length();
            //convert JSONArray to String[]
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            //return delimited list of authors
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            //error handling
            return "";
        }
    }

    // Decodes array of book json results into ArrayList of Book objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray) {
        //create new ArrayList of Book objects
        ArrayList<Book> books = new ArrayList<Book>(jsonArray.length());
        // Process each result in json array
        for (int i = 0; i < jsonArray.length(); i++) {
            //create new JSONObject
            JSONObject bookJson = null;
            try {
                //set JSONObject as next in JSONArray
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //error handling
                e.printStackTrace();
                continue;
            }
            //create new book object from parsed JSONObject
            Book book = Book.fromJson(bookJson);
            //if the book object was created successfully
            if (book != null) {
                //add new book object to books list
                books.add(book);
            }
        }
        //return books list
        return books;
    }
}

