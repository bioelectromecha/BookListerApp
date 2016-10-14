package roman.com.booklisterapp.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import roman.com.booklisterapp.R;
import roman.com.booklisterapp.fragments.BookFragment;
import roman.com.booklisterapp.fragments.HelloFragment;
import roman.com.booklisterapp.fragments.NoConnectionFragment;

/**
 * the main acitivty for the
 */
public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {


    // a key to get the fragment id from savedInstanceState
    private static final String KEY_FRAGMENT = "FRAGMENT";
    //holds the current fragment
    private Fragment mForegroundFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // if a fragment does not exist
            mForegroundFragment = new HelloFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, mForegroundFragment);
            fragmentTransaction.commit();
        } else {
            //get the fragment if it already exists
            mForegroundFragment = getSupportFragmentManager().findFragmentById(savedInstanceState.getInt(KEY_FRAGMENT));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the current fragment id - so it can be reused when activity is recreated
        outState.putInt(KEY_FRAGMENT, mForegroundFragment.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!isConnectedToIntenet()) {
            if(! (mForegroundFragment instanceof NoConnectionFragment)) {
                mForegroundFragment = new NoConnectionFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mForegroundFragment);
                fragmentTransaction.commit();
            }
        }else if(mForegroundFragment instanceof BookFragment){
            BookFragment bookFragment =  (BookFragment) mForegroundFragment;
            bookFragment.onSearchSubmitted(query);
        }else{
            BookFragment bookFragment = new BookFragment();
            mForegroundFragment = bookFragment;
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mForegroundFragment);
            fragmentTransaction.commit();
            bookFragment.onSearchSubmitted(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }

    private boolean isConnectedToIntenet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
