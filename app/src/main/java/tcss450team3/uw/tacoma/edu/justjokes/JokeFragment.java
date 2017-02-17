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
import android.widget.Toast;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that lists 20 jokes, displaying only their title.
 *
 * @author Vlad (2.15.17)
 */
public class JokeFragment extends Fragment {
    private static final String COURSE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/list.php?cmd=";
    private RecyclerView mRecyclerView;
    private int mNumPages = 1;
    private int mCurrentPageNum = 1;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JokeFragment() {
    }


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

        Bundle args = getArguments();
        if (args != null) {
            mNumPages = args.getInt("numPages");
        }

    }

    /**
     * This method downloads the list of jokes, and also provides functionality for the
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
            DownloadCoursesTask task = new DownloadCoursesTask();
            task.execute(new String[]{COURSE_URL + mCurrentPageNum});
        }

        final Button prevButton = (Button) getActivity().findViewById(R.id.prevButton);
        final Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);

        //Decrements the current page number variable and loads the jokes from that page.
        // Enables/disables buttons accordingly.
        prevButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCurrentPageNum--;
                if (mCurrentPageNum == 1) {
                    prevButton.setEnabled(false);
                }
                nextButton.setEnabled(true);
                new DownloadCoursesTask().execute(new String[]{COURSE_URL + mCurrentPageNum});
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

                new DownloadCoursesTask().execute(new String[]{COURSE_URL + mCurrentPageNum});
            }
        });

        if (mCurrentPageNum == 1)
            prevButton.setEnabled(false);

        if (mCurrentPageNum == mNumPages)
            nextButton.setEnabled(false);

        return view;
    }

    /**
     * Auto-generated method, not modified by us.
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
    private class DownloadCoursesTask extends AsyncTask<String, Void, String> {

        /**
         * Accesses the server, and retrieves the page's source.
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
         * @param result The server's response/webpage's source.
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
                mRecyclerView.setAdapter(new MyJokeRecyclerViewAdapter(courseList, mListener));
            }
        }

    }

}
