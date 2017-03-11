package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;

import android.view.Menu;
import android.view.MenuItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

/**
 * This class provides the backend for the logged-in user environment activity, where users can
 * browse our 4 different pages/tabs, and rate or favorite jokes, or submit their own jokes.
 *
 * @author Vlad 3/6/2017
 */

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener {
    /** The PagerAdapter object that handles the 4 Fragments that are displayed in mViewPager. */
    private JustJokesPagerAdapter mJustJokesPagerAdapter;

    /** This ViewPager object displays and manages our 4 tabs. */
    private ViewPager mViewPager;

    /** A Map of the user's favorite jokes, jokeIds are mapped to the Joke objects, this was done
     * to ensure quick searches. */
    private Map<Integer, Joke> mFavoriteJokes;

    /** A Set of JokeIds, of the Jokes that the user has upvoted. */
    private Set<Integer> mUpvoted;

    /** A Set of JokeIds, of the Jokes that the user has downvoted. */
    private Set<Integer> mDownvoted;

    /** The current user's username. */
    private String mUsername;

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
        args.putSerializable(CustomJokeDialogFragment.JOKE_SELECTED, joke);
        jokeDetailFragment.setArguments(args);
        jokeDetailFragment.show(getSupportFragmentManager(), "launch");
    }

    /**
     * This method is called when the JokesPage Activity is created. It initializes our ViewPager
     * object, and all the Fragments that will inhabit it. It also parses the values we retrieved
     * when we logged in (Favorites, Upvotes, and Downvotes list for the current user). Lastly it
     * handles sets up the dropDownItem that we have in one of our tabs.
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
            for (String anUpvoted : upvoted) {
                mUpvoted.add(Integer.parseInt(anUpvoted));
            }
        }

        mDownvoted = new HashSet<Integer>();
        String downvotesStringVersion = (getIntent().getStringExtra("downvotes"));
        if (!downvotesStringVersion.equals("")) {
            String[] downvoted = downvotesStringVersion.split(",");
            mDownvoted = new HashSet<Integer>();
            for (String aDownvoted : downvoted) {
                mDownvoted.add(Integer.parseInt(aDownvoted));
            }
        }

        mFavoriteJokes = new HashMap<Integer, Joke>();
        Joke.parseJokeJSON(getIntent().getStringExtra("favorites"), mFavoriteJokes);

        mUsername = getIntent().getStringExtra("username");

        List<Fragment> pages = new ArrayList<>();

        final JokeFragment jokeFragment = new JokeFragment();
        Bundle args = setupArgs();
        int numPages = getIntent().getIntExtra("numPages", 0);
        args.putInt("numPages", numPages);
        args.putString("purpose", "jokeViewer");
        args.putString("username", mUsername);
        jokeFragment.setArguments(args);
        pages.add(jokeFragment);

        JokeFragment highScoresFragment = new JokeFragment();
        args = setupArgs();
        args.putString("purpose", "highScores");
        highScoresFragment.setArguments(args);
        pages.add(highScoresFragment);

        JokeFragment favoritesFragment = new JokeFragment();
        args = setupArgs();
        args.putString("purpose", "favorites");
        favoritesFragment.setArguments(args);
        pages.add(favoritesFragment);

        SubmitJokeFragment submitJoke = new SubmitJokeFragment();
        args = new Bundle();
        args.putString("username", mUsername);
        submitJoke.setArguments(args);
        pages.add(submitJoke);

        mJustJokesPagerAdapter =
                new JustJokesPagerAdapter(getSupportFragmentManager(), pages);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mJustJokesPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //We do not need any specific action when the page is scrolled.
            }

            //When a tab is selected, modify the tabs a bit.
            @Override
            public void onPageSelected(int position) {
                TableLayout navigation = (TableLayout) findViewById(R.id.navigationTable);
                //Removes the navigation buttons if we're not in the "Jokes" tab.
                if (position == 0) {
                    navigation.setVisibility(View.VISIBLE);
                } else{
                    navigation.setVisibility(View.GONE);
                }
                if (position != 3) { //When page != the submit joke page, update the elements to make
                                     //sure they're up to date.
                    ((JokeFragment)mJustJokesPagerAdapter.getItem(position)).updateElements();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //We do not need any specific action when the page is scrolled.
            }
        });

        String[] theDropDownValues = new String[numPages];
        for (int i = 0; i < theDropDownValues.length; i++) {
            theDropDownValues[i] = Integer.toString(i + 1);
        }

        Spinner theDropDownPages = (Spinner) findViewById(R.id.dropDownPages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JokesPage.this,
                android.R.layout.simple_spinner_item, theDropDownValues);
        theDropDownPages.setAdapter(adapter);
        theDropDownPages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * This method ensures that a different page of jokes is displayed when a user chooses a
             * page number from the dropDownList.
             * When a page i
             * @param parent The parent object.
             * @param view The View that this dropDown object lives in.
             * @param position The position of the item selected, in the dropDownList.
             * @param id Unknown parameter.
             */
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
     * This method is simply used to remove redundant code, it initializes and returns a Bundle object
     * with parameters that all of our Fragments need.
     * @return Returns a Bundle with common arguments.
     */
    private Bundle setupArgs() {
        Bundle args = new Bundle();
        args.putSerializable("favoritesMap", (Serializable) mFavoriteJokes);
        args.putSerializable("upvotes", (Serializable) mUpvoted);
        args.putSerializable("downvotes", (Serializable) mDownvoted);
        return args;
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

    /**
     * Updates the RecyclerView object to ensure the elements are all displaying correctly.
     */
    public void refreshPage() {
        JokeFragment currentFragment = (JokeFragment) mJustJokesPagerAdapter.getItem(mViewPager.getCurrentItem());
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
     */
    @Override
    public void onBackPressed() {
        // Leaving blank to disable back button functionality.
    }

    /**
     * This class is used to populate pages/tabs in our ViewPager object.
     */
    private class JustJokesPagerAdapter extends FragmentPagerAdapter {
        /** The list of Fragments to include in our tabbed display. */
        private List<Fragment> myFragments;

        /**
         * Constructor to initalize the fields.
         * @param fm The FragmentManager to use.
         * @param theFragments The list of Fragments to include in our tabs.
         */
        public JustJokesPagerAdapter(FragmentManager fm, List<Fragment> theFragments) {
            super(fm);
            this.myFragments = theFragments;
        }

        /**
         * This method returns a Fragment object that is at the ith index in our myFragments list.
         * @param i The index of Fragment to return;
         * @return A Fragment at the index i, in our list of Fragments.
         */
        @Override
        public Fragment getItem(int i) {
            return myFragments.get(i);
        }

        /**
         * Returns the amount of Fragments in our list.
         * @return The size of our Fragment list.
         */
        @Override
        public int getCount() {
            return this.myFragments.size();
        }

        /**
         * Returns the page/tab title to display.
         * @param position The fragment's position in the list of fragments.
         * @return The corresponding title String.
         */
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
