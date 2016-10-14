package roman.com.booklisterapp.networking;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import roman.com.booklisterapp.dataobjects.Book;

/**
 * an asyntaskloader to get the books api data into our app
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private static final String REQUEST_URL_BASE = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String REQUEST_URL_SUFFIX = "&maxResults=25";

    //query parameters for the loader
    private String mQueryParams;

    public BookLoader(Context context, String searchQuery) {
        super(context);
        mQueryParams = searchQuery;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        List<Book> bookList = null;
        URL url = BookApiHelper.createUrl(REQUEST_URL_BASE + mQueryParams + REQUEST_URL_SUFFIX);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = BookApiHelper.makeHttpRequest(url);
        } catch (IOException e) {
            System.out.println("Problem making the HTTP request.");
            e.printStackTrace();
        }
        if (jsonResponse != null) {
            bookList = BookApiHelper.parseJsonToBooks(jsonResponse);
        } else {
            System.out.println("jsonResponse is null");
        }
        return bookList;
    }
}