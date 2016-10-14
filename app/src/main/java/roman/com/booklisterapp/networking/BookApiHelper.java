package roman.com.booklisterapp.networking;

import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import roman.com.booklisterapp.dataobjects.Book;

/**
 * a helper class used by our loader to get stuff from the books api and parse the result
 */
public class BookApiHelper {
    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            LogUtils.d("Error with creating URL", exception);
            return null;
        }
        return url;
    }




    /**
     * connect to the server and get the response as a string
     * @param url a Url object from where to GET the json
     * @return
     * @throws IOException
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                LogUtils.d("Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            LogUtils.d("Problem retrieving the JSON results.", e);
            // no matter what - do this
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                //noinspection ThrowFromFinallyBlock
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static List<Book> parseJsonToBooks(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            List<Book> bookList = new ArrayList<>();
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < booksArray.length(); i++) {
                //if parsing one book fails, continue to parse others
                try {
                    String title;
                    String authors;
                    String publishedDate;
                        /*
                         instantiated to empty string in case there is no description for the book
                         if any other string is missing - we'll just skip adding that specific book to the list
                          */
                    String description = "";

                    //get info specific to the book
                    JSONObject volumeInfo =  booksArray.getJSONObject(i).getJSONObject("volumeInfo");
                    title =volumeInfo.getString("title");
                    publishedDate = volumeInfo.getString("publishedDate");

                    //get the authors from the array (there might be several)
                    StringBuilder authorsBuilder = new StringBuilder();
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authorsBuilder.append(authorsArray.get(j));
                        authorsBuilder.append(" ");
                    }
                    authors = authorsBuilder.toString();

                    // if parsing the description fails, still add the book to the list, and set an empty description
                    try {
                        description = volumeInfo.getString("description");
                    } catch (JSONException e) {
                        LogUtils.d("JSONException thrown - there's no description");
                    }
                    bookList.add(new Book(title, authors, publishedDate, description));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //if all went well - return the books list
            return bookList;
        } catch (JSONException e) {
            LogUtils.d("Problem parsing the JSON results", e);
        }
        //return null if something went wrong
        return null;
    }
}
