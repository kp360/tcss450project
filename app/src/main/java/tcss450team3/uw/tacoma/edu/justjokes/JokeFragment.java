package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import tcss450team3.uw.tacoma.edu.justjokes.data.PageDB;
import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Fragment that lists 20 jokes, displaying only their title.
 *
 * @author Vlad 3/6/2017
 */
public class JokeFragment extends Fragment {

    /** The URL of the php file that handles joke retrieval. */
    private static final String BASE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/";

    /** Part of the url for the php file that retrieves a list of 20 jokes. */
    private static final String JOKES_URL = "list.php?page=";

    /** A String that denotes that the purpose of a particular instance is joke viewing. */
    private static final String JOKE_VIEWER_PURPOSE = "jokeViewer";

    /** A String that denotes that the purpose of a particular instance is viewing the high scores. */
    private static final String HIGH_SCORES_PURPOSE = "highScores";

    /** A String that denotes that the purpose of a particular instance is seeing the user's favorites. */
    private static final String FAVORITES_PURPOSE = "favorites";

    /** A RecylerView object that handles smooth list scrolling. */
    private RecyclerView mRecyclerView;

    /** This variable holds the amount of pages of jokes our database currently has. It is used to
     * disable the next button, so the user doesn't encounter any pages without jokes. */
    private int mNumPages = 1;

    /** This variable holds the current page that the user is viewing, it is used to ensure that the
     * previous (prev) and next buttons are enabled/disabled at the proper times. */
    private int mCurrentPageNum;

    /** The purpose of this JokeFragment (i.e. displaying the high scores or favorites). */
    private String mPurpose;

    /** A Map of the user's favorite jokes, jokeIds are mapped to the Joke objects, this was done
     * to ensure quick searches. */
    private Map<Integer, Joke> mFavorites;

    /** A Set of JokeIds, of the Jokes that the user has upvoted. */
    private Set<Integer> mUpvoted;

    /** A Set of JokeIds, of the Jokes that the user has downvoted. */
    private Set<Integer> mDownvoted;

    /** The current user's username. */
    private String mUsername;

    /** The PageDB that we will use to store and retrieve page numbers. */
    private PageDB mPageDB;

    /** The RecyclerViewAdapter that will handle displaying Jokes in this fragment. */
    private MyJokeRecyclerViewAdapter mAdapter;

    /** Auto-generated variable. */
    private static final String ARG_COLUMN_COUNT = "column-count";

    /** Auto-generated variable. */
    private int mColumnCount = 1;

    /** Auto-generated variable, used to detect user interaction. */
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JokeFragment() {
    }

    /**
     * Auto-generated constructor, not modified by us.
     *
     * @param columnCount Auto-generated variable.
     * @return Returns a JokeFragment object.
     */
    @SuppressWarnings("unused")
    public static JokeFragment newInstance(int columnCount) {
        JokeFragment fragment = new JokeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This is called when the Fragment is created, we use it to retrieve the amount of pages that
     * contain jokes, and the user's favorites and up/downvotes. We use the number of pages value
     * to enable/disable the page navigation buttons.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mNumPages = args.getInt("numPages");
            mPurpose = args.getString("purpose");
            mUsername = args.getString("username");
            mFavorites = (Map<Integer, Joke>) args.getSerializable("favoritesMap");
            mUpvoted = (Set<Integer>) args.getSerializable("upvotes");
            mDownvoted = (Set<Integer>) args.getSerializable("downvotes");
        }
    }

    /**
     * This methods is called when we access a given tab. It makes sure that the elements in the
     * clicked tab is updated/holds current values.
     */
    public void updateElements() {
        if (mPurpose.equals(JOKE_VIEWER_PURPOSE)) {
            mRecyclerView.setAdapter(mAdapter);
        } else if (mPurpose.equals(HIGH_SCORES_PURPOSE))
            new DownloadJokesTask().execute(BASE_URL + "getHighScores.php");
        else { //mPurpose.equals(FAVORITES_PURPOSE)
            updateRecyclerView();
        }
    }

    /**
     * This method calls methods in our RecyclerViewAdapter to ensure the favorites and high scores
     * lists are up-to-date.
     * */
    public void updateRecyclerView() {
        MyJokeRecyclerViewAdapter currAdapter = (MyJokeRecyclerViewAdapter) mRecyclerView.getAdapter();
        if (mPurpose.equals(FAVORITES_PURPOSE))
            currAdapter.checkFavorites();
        else if (mPurpose.equals(HIGH_SCORES_PURPOSE))
            currAdapter.checkHighScores();
        currAdapter.notifyDataSetChanged();
    }

    /**
     * This method does many different things, depending on the purpose of the current JokeFragment
     * object. If it's for jokeViewing, we initialize all the buttons and the dropdown list. If it's
     * for displaying the high scores tab, we access the high scores php file. If it's for the favorites
     * tab, we simply fill our list with the user's favorite jokes, which we retrieved when the user
     * initially logged in.
     *
     * @param inflater A LayoutInflater object, that is used to get a View.
     * @param container A ViewGroup object that is also used to get a View.
     * @param savedInstanceState Stores data that was sent from the caller.
     * @return Returns the View object that was generated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joke_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        switch (mPurpose) {
            case JOKE_VIEWER_PURPOSE:
                final Button prevButton = (Button) getActivity().findViewById(R.id.prevButton);
                final Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);

                final Spinner dropDownList = (Spinner) getActivity().findViewById(R.id.dropDownPages);

                mPageDB = new PageDB(getActivity());
                mCurrentPageNum = mPageDB.getPage(mUsername);
                if (mCurrentPageNum == -1) {
                    mCurrentPageNum = 1;
                    mPageDB.insertRow(mUsername, mCurrentPageNum);
                }

                dropDownList.setSelection(mCurrentPageNum - 1);

                //Sets the dropDownList to the proper page, which then causes the fragment to update.
                prevButton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        dropDownList.setSelection(mCurrentPageNum - 2); //(mCurrentPageNum - 1) - 1
                    }
                });

                //Sets the dropDownList to the proper page, which then causes the fragment to update.
                nextButton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        dropDownList.setSelection(mCurrentPageNum); //(mCurrentPageNum + 1) - 1
                    }
                });
                break;
            case HIGH_SCORES_PURPOSE:
                new DownloadJokesTask().execute(BASE_URL + "getHighScores.php");
                break;
            case FAVORITES_PURPOSE:
                Bundle args = new Bundle();
                args.putSerializable(FAVORITES_PURPOSE, (Serializable) mFavorites);
                args.putSerializable("upvotes", (Serializable) mUpvoted);
                args.putSerializable("downvotes", (Serializable) mDownvoted);
                mRecyclerView.setAdapter(new MyJokeRecyclerViewAdapter(new ArrayList<Joke>(mFavorites.values()), mListener, false, args));
            break;
            default:
                break;
        }
        return view;
    }

    /**
     * This method stores the user's last browsed page's page number in our SQLite database (only
     * applicable when the JokeFragment is used for jokeViewing/jokeBrowsing).
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPurpose.equals(JOKE_VIEWER_PURPOSE)) {
            mPageDB.updatePageNum(mUsername, mCurrentPageNum);
        }
    }

    /**
     * This method downloads page <newPage> of jokes from our database, and ensures the proper
     * navigation buttons are enabled/disabled.
     * @param newPage The page of jokes to download and display.
     */
    public void changePage(int newPage) {
        final Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);
        final Button prevButton = (Button) getActivity().findViewById(R.id.prevButton);
        mCurrentPageNum = newPage;

        if (mCurrentPageNum == mNumPages)
            nextButton.setEnabled(false);
        else
            nextButton.setEnabled(true);
        if (mCurrentPageNum == 1)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        new DownloadJokesTask().execute(BASE_URL + JOKES_URL + mCurrentPageNum);
    }

    /**
     * Auto-generated method, not modified by us.
     *
     * This method is critical because it initializes mListener, which detects when the user
     * interacts with our joke list.
     *
     * @param context A Context object.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * Auto-generated method, not modified by us.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
     * Auto-generated method, not modified by us.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Joke item);
    }

    /**
     * This class is used to retrieve the current page of jokes, and list them in the fragment.
     */
    private class DownloadJokesTask extends AsyncTask<String, Void, String> {

        /**
         * Accesses the server, and retrieves the page's data.
         *
         * @param urls URLs to access.
         * @return Returns the webpage's source.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of jokes. " +
                            "Please check your internet connection.";
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Notifies the user of what happened via Toast, and updates the list if no issues occurred.
         *
         * @param result An error message, or the server's response/webpage's data.
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Joke> jokeList = new ArrayList<Joke>();
            result = Joke.parseJokeJSON(result, jokeList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of jokes.
            if (!jokeList.isEmpty()) {
                Bundle args = new Bundle();
                args.putSerializable(FAVORITES_PURPOSE, (Serializable) mFavorites);
                args.putSerializable("upvotes", (Serializable) mUpvoted);
                args.putSerializable("downvotes", (Serializable) mDownvoted);

                //If this JokeFragment object is used from showing the high scores jokes, pass a
                //parameter so that the jokes are numbered, 1-20.
                if (mPurpose.equals(HIGH_SCORES_PURPOSE))
                   mAdapter = new MyJokeRecyclerViewAdapter(jokeList, mListener, true, args);
                else {
                   mAdapter = new MyJokeRecyclerViewAdapter(jokeList, mListener, false, args);
                }
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
