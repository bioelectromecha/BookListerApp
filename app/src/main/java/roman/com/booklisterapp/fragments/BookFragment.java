package roman.com.booklisterapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import roman.com.booklisterapp.R;
import roman.com.booklisterapp.adapters.BookRecyclerViewAdapter;
import roman.com.booklisterapp.dataobjects.Book;
import roman.com.booklisterapp.networking.BookLoader;

/**
 * A fragment representing a list of books
 */
public class BookFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    // id of the book loader
    private static final int LOADER_ID = 2701;
    // id for the string that goes into the bundle
    private static final String KEY_QUERY_STRING = "QUERY_STRING";
    // what do these do?
    private static final String ARG_COLUMN_COUNT = "column-count";
    // the string for the network api query
    private String mSearchQuery = null;
    //the list view
    private RecyclerView mRecyclerView;
    // the circular progress bar view
    private ProgressBar mProgressBar;
    private List<Book> mBookList;
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loading_indicator);

        //in the beginning, we show the circular progress bar and hide the list
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        Context context = view.getContext();
        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setVisibility(View.GONE);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mBookList = new ArrayList<>();
        mRecyclerView.setAdapter(new BookRecyclerViewAdapter(mBookList));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // reload the previous data or get data with the query string submitted by onSearchSubmitted
        initiateBookDataLoad();
    }

    /**
     * submit and execute a query to the network api via our asyntaskloader
     *
     * @param searchQuery
     */
    public void onSearchSubmitted(String searchQuery) {
        if (mSearchQuery == null && !isAdded()) {
            mSearchQuery = searchQuery;
        } else if (mSearchQuery == null && isAdded()) {
            mSearchQuery = searchQuery;
            restartLoader();
        } else if (!mSearchQuery.equals(searchQuery) && isAdded()) {
            mSearchQuery = searchQuery;
            restartLoader();
        } else if (isAdded()) {
            initiateBookDataLoad();
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this.getContext(), args.getString(KEY_QUERY_STRING));
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
        if (bookList == null) {
            Toast.makeText(getActivity(), "Problem retrieving data - try again", Toast.LENGTH_SHORT).show();
            return;
        }

        //hide the circular progress bar and show the list
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        mBookList.clear();
        mBookList.addAll(bookList);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        //do nothing?
    }

    /**
     * make a new loader or get the data from a previous load
     */
    private void initiateBookDataLoad() {
        LoaderManager loaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY_STRING, mSearchQuery);
        loaderManager.initLoader(LOADER_ID, bundle, this);
    }

    /**
     * the query has change - make a new loader with the new data
     */
    private void restartLoader() {

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY_STRING, mSearchQuery);
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

}
