package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

/**
 * This class provides the backend for the Activity where a list of jokes is displayed.
 *
 * @author Vlad (2.16.17)
 */

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener  {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

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

        List<Fragment> pages = new ArrayList<>();

        JokeFragment jokeFragment = new JokeFragment();
        Bundle args = new Bundle();
        int numPagesOfJokes = getIntent().getIntExtra("numPages", 0);
        args.putInt("numPages", numPagesOfJokes);
        jokeFragment.setArguments(args);
        pages.add(jokeFragment);

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
                Log.e("tag", "GETINT: " + position);

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

//        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
//            JokeFragment courseFragment = new JokeFragment();
//            Bundle args = new Bundle();
//            int numPagesOfJokes = getIntent().getIntExtra("numPages", 0);
//            args.putInt("numPages", numPagesOfJokes);
//            courseFragment.setArguments(args);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, courseFragment)
//                    .commit();
//        }
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
