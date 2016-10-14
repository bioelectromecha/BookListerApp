package roman.com.booklisterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import roman.com.booklisterapp.dataobjects.Book;
import roman.com.booklisterapp.R;

/**
 * a fragment with some instructions for the user and a welcome message
 */
public class HelloFragment extends Fragment{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView mRecyclerView;
    private List<Book> mBookList;
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HelloFragment() {
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
        View view = inflater.inflate(R.layout.fragment_hello, container, false);

        return view;
    }
}
