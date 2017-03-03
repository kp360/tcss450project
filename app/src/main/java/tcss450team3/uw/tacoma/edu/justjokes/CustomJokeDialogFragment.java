package tcss450team3.uw.tacoma.edu.justjokes;

import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;


/**
 * A Custom Dialog Fragment that is used to display a joke's setup and punchline, it allows the user
 * show/hide the joke's punchline.
 *
 * @author Vlad (2.15.17)
 */
public class CustomJokeDialogFragment extends DialogFragment {
    public final static String COURSE_ITEM_SELECTED = "course_selected";
    private static final String BASE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/handle";

    public final static int NO_VOTE = 0;
    public final static int UPVOTED = 1;
    public final static int DOWNVOTED = 2;

    /** A TextView variable that allows us to modify the setup TextView seen by the user. */
    private TextView mJokeSetupTextView;

    /** A TextView variable that allows us to modify the punchline TextView seen by the user. */
    private TextView mJokePunchlineTextView;

    /** The Button that hides/shows a joke's punchline. */
    private Button mShowButton;

    private List<Joke> mFavorites;
    private Set<Integer> mUpvotes;
    private Set<Integer> mDownvotes;
    private String mUsername;

    private ImageView mFavoriteButton;
    private ImageView mUpvoteButton;
    private ImageView mDownvoteButton;

    private int mAmountAdd;
    private String mAction;

    private Joke mCurrentJoke;
    private int mCurrentJokeID;

    private int mVotingStatus;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /** Auto-generated variable, used to detect user interaction. */
    private OnFragmentInteractionListener mListener;

    public CustomJokeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * Auto-generated method, not modified by us.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomJokeDialogFragment.
     */
    public static CustomJokeDialogFragment newInstance(String param1, String param2) {
        CustomJokeDialogFragment fragment = new CustomJokeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Auto-generated method, not modified by us.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mFavorites = (List) args.getSerializable("favorites");
        mUpvotes = (Set<Integer>) args.getSerializable("upvotes");
        mDownvotes = (Set<Integer>) args.getSerializable("downvotes");
        mUsername = args.getString("username");

        Log.e("tag", mFavorites.get(0).getJokeTitle());
    }

    /**
     * This method is used to setup how the dialog popup will look and behave.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     * @return The Dialog to display.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =  inflater.inflate(R.layout.fragment_custom_joke_dialog, null);
        mJokeSetupTextView = (TextView) view.findViewById(R.id.setupText);
        mJokePunchlineTextView = (TextView) view.findViewById(R.id.punchlineText);

        Bundle args = getArguments();
        if (args != null) {
            mCurrentJoke = (Joke) args.getSerializable(COURSE_ITEM_SELECTED);
            mCurrentJokeID = mCurrentJoke.getJokeID();
            // Set article based on argument passed in
            updateView(mCurrentJoke);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        mShowButton = (Button) view.findViewById(R.id.showButton);

        //This inner method provides the functionality for the show/hide punchline button.
        mShowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mJokePunchlineTextView.getVisibility() == View.INVISIBLE) {
                    mJokePunchlineTextView.setVisibility(view.VISIBLE);
                    mShowButton.setText("Hide Punchline");
                } else {
                    mJokePunchlineTextView.setVisibility(view.INVISIBLE);
                    mShowButton.setText("Show Punchline");
                }
            }
        });

        mUpvoteButton = (ImageView) view.findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageView) view.findViewById(R.id.downvoteButton);
        checkVote();

        mUpvoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mAction = "Upvote";

                if (mVotingStatus == NO_VOTE) {
                    //add one to upvote score. mark as upvoted.
                    mUpvoteButton.setImageResource(R.drawable.upvote);
                    mUpvotes.add(mCurrentJokeID);
                } else if (mVotingStatus == UPVOTED) {
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mUpvotes.remove(mCurrentJokeID);
                } else { //Downvoted.
                    mUpvoteButton.setImageResource(R.drawable.upvote);
                    mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    mDownvotes.remove(mCurrentJokeID);
                    mUpvotes.add(mCurrentJokeID);
                }

                String url = buildURL();
                new EditVote().execute(new String[]{url});
            }
        });

        mDownvoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mAction = "Downvote";

                if (mVotingStatus == NO_VOTE) {
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mDownvotes.add(mCurrentJokeID);
                } else if (mVotingStatus == UPVOTED) {
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mDownvotes.remove(mCurrentJokeID);
                } else { //Downvoted.
                    mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    mUpvotes.remove(mCurrentJokeID);
                    mDownvotes.add(mCurrentJokeID);
                }

                String url = buildURL();
                new EditVote().execute(new String[]{url});
            }
        });

        return builder.create();
    }

    /**
     * This method updates the proper text views to display the joke's setup and punchline.
     *
     * @param joke A Joke object to retrieve data from.
     */
    public void updateView(Joke joke) {
        if (joke != null) {
            mJokeSetupTextView.setText(joke.getJokeSetup());
            mJokePunchlineTextView.setText(joke.getJokePunchline());
        }
    }

    private String buildURL() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append(mAction);

            sb.append("s.php?user=");
            sb.append(mUsername);

            sb.append("&jokeID=");
            sb.append(mCurrentJokeID);

            sb.append("&currState=");
            sb.append(mVotingStatus);

            Log.i("buildURL", sb.toString());

        return sb.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
     * Auto-generated method, not modified by us.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void checkVote() {
        if (mUpvotes.contains(mCurrentJokeID)) {
            mVotingStatus = UPVOTED;
            mUpvoteButton.setImageResource(R.drawable.upvote);
        } else if (mDownvotes.contains(mCurrentJokeID)) {
            mVotingStatus = DOWNVOTED;
            mDownvoteButton.setImageResource(R.drawable.downvote);
        } else {
            mVotingStatus = NO_VOTE;
        }
    }

    private class EditVote extends AsyncTask<String, Void, String> {

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
                //enable buttons.
                if (status.equals("success")) {
                    if (mAction.equals("Upvote")) {
                        if (mVotingStatus == NO_VOTE)
                            mVotingStatus = UPVOTED;
                        else if (mVotingStatus == UPVOTED)
                            mVotingStatus = NO_VOTE;
                        else //Downvoted.
                            mVotingStatus = UPVOTED;
                    } else {
                        if (mVotingStatus == NO_VOTE)
                            mVotingStatus = DOWNVOTED;
                        else if (mVotingStatus == UPVOTED)
                            mVotingStatus = DOWNVOTED;
                        else //Downvoted.
                            mVotingStatus = NO_VOTE;
                    }

                    Toast.makeText(getActivity().getApplicationContext(), mAction + "d: " + mCurrentJoke.getJokeTitle()
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    if (mVotingStatus == NO_VOTE) {
                        mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    } else if (mVotingStatus == UPVOTED) {
                        mUpvoteButton.setImageResource(R.drawable.neutralup);
                        mDownvoteButton.setImageResource(R.drawable.downvote);
                    } else { //Downvoted.
                        mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to " + mAction + ": " + mCurrentJoke.getJokeTitle()
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
