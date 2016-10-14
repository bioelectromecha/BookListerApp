package roman.com.booklisterapp.networking;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.apkfuns.logutils.LogUtils;

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
        LogUtils.d("BookLoader Constructor");
        mQueryParams = searchQuery;
    }

    @Override
    protected void onStartLoading() {
        LogUtils.d("onStartLoading");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        LogUtils.d("loadInBackground");
        List<Book> bookList = null;
        URL url = BookApiHelper.createUrl(REQUEST_URL_BASE + mQueryParams + REQUEST_URL_SUFFIX);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = BookApiHelper.makeHttpRequest(url);
        } catch (IOException e) {
            LogUtils.d("Problem making the HTTP request.", e);
        }
        if (jsonResponse != null) {
            bookList = BookApiHelper.parseJsonToBooks(jsonResponse);
        } else {
            LogUtils.d("jsonResponse is null");
        }
        return bookList;
    }
}