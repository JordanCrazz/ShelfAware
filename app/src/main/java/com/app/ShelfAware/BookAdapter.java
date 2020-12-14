package com.app.ShelfAware;
//imports

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//a class to handle the listView in SearchActivity, and to populate it with book entries

public class BookAdapter extends ArrayAdapter<Book> {
    //setting up variables
    private static class ViewHolder {
        public ImageView ivCover;
        public TextView tvTitle;
        public TextView tvAuthor;
    }
    public BookAdapter(Context context, ArrayList<Book> aBooks) {
        super(context, 0, aBooks);
    }

    //function that translates a particular `Book` given a position into a row within an adapterView
    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        // Get the data item for this position
        final Book book = getItem(position);
        final ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_book, parent, false);

            viewHolder.ivCover = convertView.findViewById(R.id.ivBookCover);
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tvAuthor);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        assert book != null;
        viewHolder.tvTitle.setText(book.getTitle());
        viewHolder.tvAuthor.setText(book.getAuthor());

        //create a new ImageRequest to get a bitmap for the cover url
        ImageRequest request = new ImageRequest(book.getCoverUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    //on expected response
                    public void onResponse(Bitmap bitmap) {
                        //set the current ivCover to the bitmap
                        viewHolder.ivCover.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //on unexpected response, set image to default bookcover image
                        viewHolder.ivCover.setImageResource(R.drawable.bookcover);
                    }
                });
        //send request through singleton
        MySingleton.getInstance(this.getContext()).addToRequestQueue(request);

        // Return the completed view to display on ListView
        return convertView;
    }
}