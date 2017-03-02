package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
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
import android.widget.EditText;
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
import java.util.HashSet;
import java.util.List;
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

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener, SubmitJokeFragment.SubmitJokeListener {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    List<Joke> mFavoriteJokes;
    Set<Integer> mUpvoted;
    Set<Integer> mDownvoted;

    /**
     * This method handles opening a CustomJokeDialogFragment when a joke in the list is tapped on.
     *
     * @param joke The Joke whose values (setup, punchline) will be displayed in the
     *             CustomJokeDialogFragment.
     */
    @Override
    public void onListFragmentInteraction(Joke joke) {
        CustomJokeDialogFragment jokeDetailFragment = new CustomJokeDialogFragment();
        Bundle args = new Bundle();
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

        mFavoriteJokes = new ArrayList<Joke>();
        Joke.parseCourseJSON(getIntent().getStringExtra("favorites"), mFavoriteJokes);

        List<Fragment> pages = new ArrayList<>();

        JokeFragment jokeFragment = new JokeFragment();
        Bundle args = new Bundle();
        int numPagesOfJokes = getIntent().getIntExtra("numPages", 0);
        args.putInt("numPages", numPagesOfJokes);
        args.putString("purpose", "jokeViewer");
        jokeFragment.setArguments(args);
        pages.add(jokeFragment);

        JokeFragment highScoresFragment = new JokeFragment();
        args = new Bundle();
        args.putString("purpose", "highScores");
        highScoresFragment.setArguments(args);
        pages.add(highScoresFragment);

        JokeFragment favoritesFragment = new JokeFragment();
        args = new Bundle();
        args.putString("purpose", "favorites");
        args.putSerializable("favoritesList", (Serializable) mFavoriteJokes);
        favoritesFragment.setArguments(args);
        pages.add(favoritesFragment);

        SubmitJokeFragment submitJoke = new SubmitJokeFragment();
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
     * This method overrides the bac k button, that way you can't use the back button
     * to close out of the app. (I think) Pressing the back button pops the joke list off the stack
     * so when you reopen the app you have to log in again.
     *
     */
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void submitJoke(String url) {
        System.out.println("submit joke is being called");
        SubmitJokeTask task = new SubmitJokeTask();
        task.execute(new String[]{url.toString()});
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

    private class SubmitJokeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    System.out.println("SubmitJokeTask trying");
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    System.out.println("SubmitJokeTask failed");
                    response = "Unable to submit joke, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    EditText jokeTitle = (EditText)findViewById(R.id.sub_joke_title_field);
                    EditText jokeSetup = (EditText)findViewById(R.id.sub_joke_setup_field);
                    EditText jokePunchline = (EditText)findViewById(R.id.sub_joke_punchline_field);

                    jokeTitle.setText("");
                    jokeSetup.setText("");
                    jokePunchline.setText("");

                    Toast.makeText(getApplicationContext(), "Joke successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
