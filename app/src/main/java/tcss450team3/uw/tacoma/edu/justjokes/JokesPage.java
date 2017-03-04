package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

/**
 * This class provides the backend for the Activity where a list of jokes is displayed.
 *
 * @author Vlad (2.16.17)
 */

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    private Map<Integer, Joke> mFavoriteJokes;
    private Set<Integer> mUpvoted;
    private Set<Integer> mDownvoted;
    private String mUsername;
    private Spinner mDropDownPages;
    private String[] mDropDownValues;
    private int mNumPages;

    /**
     * This method handles opening a CustomAdminJokeDialogFragment when a joke in the list is tapped on.
     *
     * @param joke The Joke whose values (setup, punchline) will be displayed in the
     *             CustomAdminJokeDialogFragment.
     */
    @Override
    public void onListFragmentInteraction(Joke joke) {
        CustomJokeDialogFragment jokeDetailFragment = new CustomJokeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("favorites", (Serializable) mFavoriteJokes);
        args.putSerializable("upvotes", (Serializable) mUpvoted);
        args.putSerializable("downvotes", (Serializable) mDownvoted);
        args.putString("username", mUsername);
        args.putSerializable(jokeDetailFragment.COURSE_ITEM_SELECTED, joke);
        jokeDetailFragment.setArguments(args);
        jokeDetailFragment.show(getSupportFragmentManager(), "launch");
    }

    /**
     * This method is called when the JokesPage Activity is created. It opens a JokeFragment object,
     * and passes it the number of pages of jokes that are currently in our database.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jokes_page);

        mUpvoted = new HashSet<Integer>();
        String upvotesStringVersion = (getIntent().getStringExtra("upvotes"));
        if (!upvotesStringVersion.equals("")) {
            String[] upvoted = upvotesStringVersion.split(",");
            for (int i = 0; i < upvoted.length; i++) {
                mUpvoted.add(Integer.parseInt(upvoted[i]));
            }
        }

        mDownvoted = new HashSet<Integer>();
        String downvotesStringVersion = (getIntent().getStringExtra("downvotes"));
        if (!downvotesStringVersion.equals("")) {
            String[] downvoted = downvotesStringVersion.split(",");
            mDownvoted = new HashSet<Integer>();
            for (int i = 0; i < downvoted.length; i++) {
                mDownvoted.add(Integer.parseInt(downvoted[i]));
            }
        }

        mFavoriteJokes = new HashMap<Integer, Joke>();
        Joke.parseCourseJSON(getIntent().getStringExtra("favorites"), mFavoriteJokes);

        mUsername = getIntent().getStringExtra("username");

        List<Fragment> pages = new ArrayList<>();

        final JokeFragment jokeFragment = new JokeFragment();
        Bundle args = new Bundle();
        mNumPages = getIntent().getIntExtra("numPages", 0);
        args.putInt("numPages", mNumPages);
        args.putString("purpose", "jokeViewer");
        args.putSerializable("favoritesMap", (Serializable) mFavoriteJokes);
        args.putSerializable("upvotes", (Serializable) mUpvoted);
        args.putSerializable("downvotes", (Serializable) mDownvoted);
        jokeFragment.setArguments(args);
        pages.add(jokeFragment);

        JokeFragment highScoresFragment = new JokeFragment();
        args = new Bundle();
        args.putString("purpose", "highScores");
        args.putSerializable("favoritesMap", (Serializable) mFavoriteJokes);
        args.putSerializable("upvotes", (Serializable) mUpvoted);
        args.putSerializable("downvotes", (Serializable) mDownvoted);
        highScoresFragment.setArguments(args);
        pages.add(highScoresFragment);

        JokeFragment favoritesFragment = new JokeFragment();
        args = new Bundle();
        args.putString("purpose", "favorites");
        args.putSerializable("favoritesMap", (Serializable) mFavoriteJokes);
        args.putSerializable("upvotes", (Serializable) mUpvoted);
        args.putSerializable("downvotes", (Serializable) mDownvoted);
        favoritesFragment.setArguments(args);
        pages.add(favoritesFragment);

        SubmitJokeFragment submitJoke = new SubmitJokeFragment();
        args = new Bundle();
        args.putString("username", mUsername);
        submitJoke.setArguments(args);
        pages.add(submitJoke);

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(getSupportFragmentManager(), pages);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TableLayout navigation = (TableLayout) findViewById(R.id.navigationTable);
                if (position == 0) {
                    navigation.setVisibility(View.VISIBLE);
                } else{
                    navigation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDropDownValues = new String[mNumPages];
        for (int i = 0; i < mDropDownValues.length; i++) {
            mDropDownValues[i] = Integer.toString(i + 1);
        }

        mDropDownPages = (Spinner) findViewById(R.id.dropDownPages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JokesPage.this,
                android.R.layout.simple_spinner_item, mDropDownValues);
        mDropDownPages.setAdapter(adapter);
        mDropDownPages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jokeFragment.changePage(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               //Do nothing.
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.yoTabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * This method creates the settings menu.
     *
     * @param menu Our settings/logout menu
     * @return boolean when menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    public void refreshPage() {
        JokeFragment currentFragment = (JokeFragment) mDemoCollectionPagerAdapter.getItem(mViewPager.getCurrentItem());
        currentFragment.updateRecyclerView();
    }

    /**
     * This method handles whatever button was clicked in the menu by launching a certain action.
     *
     * @param item the button that was pressed inside the menu i.e.(logout, preferences)
     * @return boolean after action was performed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method overrides the back button, that way you can't use the back button
     * to close out of the app. Pressing the back button pops the joke list off the stack
     * so when you reopen the app you have to log in again.
     *
     */
    @Override
    public void onBackPressed() {
        // Leaving blank to disable back button functionality.
    }

    private class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> myFragments;

        public DemoCollectionPagerAdapter(FragmentManager fm, List<Fragment> theFragments) {
            super(fm);
            this.myFragments = theFragments;
        }

        @Override
        public Fragment getItem(int i) {
            return myFragments.get(i);
        }

        @Override
        public int getCount() {
            return this.myFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Jokes";
                case 1:
                    return "High Scores";
                case 2:
                    return "Favorites";
                case 3:
                    return "Submit Joke";
            }
            return null;
        }
    }
}
