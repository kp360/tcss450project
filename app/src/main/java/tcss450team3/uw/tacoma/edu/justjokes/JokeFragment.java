package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A fragment that lists 20 jokes, displaying only their title.
 *
 * @author Vlad (2.15.17)
 */
public class JokeFragment extends Fragment {

    /** The URL of the php file that handles joke retrieval. */
    private static final String BASE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/";

    private static final String JOKES_URL = "list.php?page=";

    private static final String HIGH_SCORES_URL
            = "getHighScores.php";

    /** This text is used to convey to the user what page they're currently on. */
    private static final String PAGE_TEXT = " Page: ";

    /** A RecylerView object that handles smooth list scrolling. */
    private RecyclerView mRecyclerView;

    /** The TextView that tells the user what page of jokes they are currently browsing. */
    private TextView mPageNumTextView;

    /** This variable holds the amount of pages of jokes our database currently has. It is used to
     * disable the next button, so the user doesn't encounter any pages without jokes. */
    private int mNumPages = 1;

    /** This variable holds the current page that the user is viewing, it is used to ensure that the
     * previous (prev) and next buttons are enabled/disabled at the proper times. */
    private int mCurrentPageNum;

    private String mPurpose;

    private Map<Integer, Joke> mFavorites;

    private Set<Integer> mUpvoted;

    private Set<Integer> mDownvoted;

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
     * contain jokes, which we use to enable/disable the page navigation buttons.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPageNum = 1;
        Log.e("tag", "oncreate");
        Bundle args = getArguments();
        if (args != null) {
            mNumPages = args.getInt("numPages");
            mPurpose = args.getString("purpose");
            mFavorites = (Map<Integer, Joke>) args.getSerializable("favoritesMap");
            mUpvoted = (Set<Integer>) args.getSerializable("upvotes");
            mDownvoted = (Set<Integer>) args.getSerializable("downvotes");
        }
        Log.e("str", mUpvoted.toString());
    }

    public void updateRecyclerView() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * This method downloads the page of jokes, and also provides functionality for the
     * 'Next'/'Prev' buttons.
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

        if (mPurpose.equals("jokeViewer")) {
            DownloadJokesTask task = new DownloadJokesTask();

            task.execute(new String[]{BASE_URL + JOKES_URL + mCurrentPageNum});

            final Button prevButton = (Button) getActivity().findViewById(R.id.prevButton);
            final Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);

            //Decrements the current page number variable and loads the jokes from that page.
            //Enables/disables buttons accordingly.
            prevButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    mCurrentPageNum--;
                    if (mCurrentPageNum == 1)
                        prevButton.setEnabled(false);

                    if (!nextButton.isEnabled())
                        nextButton.setEnabled(true);

                    new DownloadJokesTask().execute(new String[]{BASE_URL + JOKES_URL + mCurrentPageNum});
                    updatePageNumTextView();
                }
            });

            //Increments the current page number variable and loads the jokes from that page.
            //Enables/disables buttons accordingly.
            nextButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    mCurrentPageNum++;
                    if (mCurrentPageNum == mNumPages)
                        nextButton.setEnabled(false);

                    if (!prevButton.isEnabled())
                        prevButton.setEnabled(true);

                    new DownloadJokesTask().execute(new String[]{BASE_URL + JOKES_URL + mCurrentPageNum});
                    updatePageNumTextView();
                }
            });

            if (mCurrentPageNum == 1)
                prevButton.setEnabled(false);

            if (mCurrentPageNum == mNumPages)
                nextButton.setEnabled(false);

            mPageNumTextView = (TextView) getActivity().findViewById(R.id.pageNum);
            updatePageNumTextView();
        } else if (mPurpose.equals("highScores")) {
            new DownloadJokesTask().execute(new String[]{BASE_URL + HIGH_SCORES_URL});
        } else { //purpose = favorites
            Bundle args = new Bundle();
            args.putSerializable("favorites", (Serializable) mFavorites);
            args.putSerializable("upvotes", (Serializable) mUpvoted);
            args.putSerializable("downvotes", (Serializable) mDownvoted);
            mRecyclerView.setAdapter(new MyJokeRecyclerViewAdapter(new ArrayList<Joke>(mFavorites.values()), mListener, false, args));
        }

        return view;
    }

    /**
     * This method is used to update the TextView that shows the current page number.
     */
    private void updatePageNumTextView() {
        mPageNumTextView.setText(PAGE_TEXT + mCurrentPageNum + " ");
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
                    response = "Unable to download the list of courses, Reason: "
                            + e.getMessage();
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

            List<Joke> courseList = new ArrayList<Joke>();
            result = Joke.parseCourseJSON(result, courseList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!courseList.isEmpty()) {
                Bundle args = new Bundle();
                args.putSerializable("favorites", (Serializable) mFavorites);
                args.putSerializable("upvotes", (Serializable) mUpvoted);
                args.putSerializable("downvotes", (Serializable) mDownvoted);
                if (mPurpose.equals("highScores"))
                    mRecyclerView.setAdapter(new MyJokeRecyclerViewAdapter(courseList, mListener, true, args));
                else
                    mRecyclerView.setAdapter(new MyJokeRecyclerViewAdapter(courseList, mListener, false, args));
            }
        }

    }

}
