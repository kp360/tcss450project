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
import java.util.HashMap;
import java.util.Map;
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

    private Map<Integer, Joke> mFavorites;
    private Set<Integer> mUpvotes;
    private Set<Integer> mDownvotes;
    private String mUsername;

    private ImageView mFavoriteButton;
    private ImageView mUpvoteButton;
    private ImageView mDownvoteButton;
    private TextView mUpvoteTextView;
    private TextView mDownvoteTextView;

    private boolean mCurrentlyBusy;
    private String mAction;

    private Joke mCurrentJoke;
    private int mCurrentJokeID;

    private int mVotingStatus;
    private boolean isFavorite;

    /** Auto-generated variable, used to detect user interaction. */
    private OnFragmentInteractionListener mListener;

    public CustomJokeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * Auto-generated method, cleaned up by us.
     *
     * @return A new instance of fragment CustomAdminJokeDialogFragment.
     */
    public static CustomJokeDialogFragment newInstance() {
        return new CustomJokeDialogFragment();
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
        mFavorites = (HashMap<Integer, Joke>) args.getSerializable("favorites");
        mUpvotes = (Set<Integer>) args.getSerializable("upvotes");
        mDownvotes = (Set<Integer>) args.getSerializable("downvotes");
        mUsername = args.getString("username");
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
            updateView();
        }
        final TextView titleTextView = (TextView) view.findViewById(R.id.titleText);
        titleTextView.setText(mCurrentJoke.getJokeTitle());

        mUpvoteTextView = (TextView) view.findViewById(R.id.upvoteCount);
        mDownvoteTextView = (TextView) view.findViewById(R.id.downvoteCount);

        updateCountTextViews();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        mShowButton = (Button) view.findViewById(R.id.showButton);

        //This inner method provides the functionality for the show/hide punchline button.
        mShowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mJokePunchlineTextView.getVisibility() == View.INVISIBLE) {
                    mJokePunchlineTextView.setVisibility(View.VISIBLE);
                    mShowButton.setText("Hide Punchline");
                } else {
                    mJokePunchlineTextView.setVisibility(View.INVISIBLE);
                    mShowButton.setText("Show Punchline");
                }
            }
        });

        mFavoriteButton = (ImageView) view.findViewById(R.id.favoriteButton);
        mUpvoteButton = (ImageView) view.findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageView) view.findViewById(R.id.downvoteButton);
        checkInitialStatus();

        mUpvoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mCurrentlyBusy)
                    return;
                else
                    mCurrentlyBusy = true;

                mAction = "Upvote";

                if (mVotingStatus == NO_VOTE) {
                    //add one to upvote score. mark as upvoted.
                    mUpvoteButton.setImageResource(R.drawable.upvote);
                    mUpvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumUpvotes();
                } else if (mVotingStatus == UPVOTED) {
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mUpvotes.remove(mCurrentJokeID);
                    mCurrentJoke.decrementNumUpvotes();
                } else { //Downvoted.
                    mUpvoteButton.setImageResource(R.drawable.upvote);
                    mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    mDownvotes.remove(mCurrentJokeID);
                    mUpvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumUpvotes();
                    mCurrentJoke.decrementNumDownvotes();
                }

                updateCountTextViews();
                new EditVote().execute(buildURL());
            }
        });

        mDownvoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mCurrentlyBusy)
                    return;
                else
                    mCurrentlyBusy = true;

                mAction = "Downvote";

                if (mVotingStatus == NO_VOTE) {
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mDownvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumDownvotes();
                } else if (mVotingStatus == UPVOTED) {
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mUpvotes.remove(mCurrentJokeID);
                    mDownvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumDownvotes();
                    mCurrentJoke.decrementNumUpvotes();
                } else { //Downvoted.
                    mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    mDownvotes.remove(mCurrentJokeID);
                    mCurrentJoke.decrementNumDownvotes();
                }

                updateCountTextViews();
                String url = buildURL();
                new EditVote().execute(new String[]{url});
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mCurrentlyBusy)
                    return;
                else
                    mCurrentlyBusy = true;

                if (isFavorite) {
                    mFavoriteButton.setMaxWidth(56);
                    mFavoriteButton.setImageResource(R.drawable.favoritebutton);
                    mFavorites.remove(mCurrentJokeID);
                } else {
                    mFavoriteButton.setMaxWidth(64);
                    mFavoriteButton.setImageResource(R.drawable.unfavoritebutton);
                    mFavorites.put(mCurrentJokeID, mCurrentJoke);
                }

                updateCountTextViews();
                String url = buildFavoritesURL();
                Log.e("", url);
                new EditFavorite().execute(new String[]{url});
            }
        });

        return builder.create();
    }

    /**
     * This method updates the proper text views to display the joke's setup and punchline.
     *
     */
    public void updateView() {
        if (mCurrentJoke != null) {
            mJokeSetupTextView.setText(mCurrentJoke.getJokeSetup());
            mJokePunchlineTextView.setText(mCurrentJoke.getJokePunchline());
        }
    }

    private void updateCountTextViews() {
        mUpvoteTextView.setText(Integer.toString(mCurrentJoke.getmNumUpvotes()));
        mDownvoteTextView.setText(Integer.toString(mCurrentJoke.getmNumDownvotes()));
    }

    private String buildFavoritesURL() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("Favorites.php?user=");

        sb.append(mUsername);
        sb.append("&jokeID=");
        sb.append(mCurrentJokeID);
        sb.append("&favorite=");
        if (isFavorite)
            sb.append(0);
        else
            sb.append(1);

        return sb.toString();
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

    private void checkInitialStatus() {
        if (mUpvotes.contains(mCurrentJokeID)) {
            mVotingStatus = UPVOTED;
            mUpvoteButton.setImageResource(R.drawable.upvote);
        } else if (mDownvotes.contains(mCurrentJokeID)) {
            mVotingStatus = DOWNVOTED;
            mDownvoteButton.setImageResource(R.drawable.downvote);
        } else {
            mVotingStatus = NO_VOTE;
        }

        if (mFavorites.keySet().contains(mCurrentJokeID)) {
            isFavorite = true;
            mFavoriteButton.setImageResource(R.drawable.unfavoritebutton);
        } else
            isFavorite = false;
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
                    response = "Unable to update vote, Reason: "
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

                    /**Toast.makeText(getActivity().getApplicationContext(), "Successfully updated: " + mCurrentJoke.getJokeTitle()
                            , Toast.LENGTH_LONG)
                            .show(); **/
                    ((JokesPage) getActivity()).refreshPage();
                } else {
                    resetVote();

                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update: " + mCurrentJoke.getJokeTitle()
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                resetVote();

                Toast.makeText(getActivity().getApplicationContext(), "Please check your internet connection."
                        , Toast.LENGTH_LONG).show();
            }
            mCurrentlyBusy = false;
        }
    }

    private void resetVote() {
        if (mAction.equals("Upvote")) {
            if (mVotingStatus == NO_VOTE) {
                mUpvoteButton.setImageResource(R.drawable.neutralup);
                mUpvotes.remove(mCurrentJokeID);
                mCurrentJoke.decrementNumUpvotes();
            } else if (mVotingStatus == UPVOTED) {
                mUpvoteButton.setImageResource(R.drawable.upvote);
                mUpvotes.add(mCurrentJokeID);
                mCurrentJoke.incrementNumUpvotes();
            } else { //Downvoted.
                mUpvoteButton.setImageResource(R.drawable.neutralup);
                mDownvoteButton.setImageResource(R.drawable.downvote);
                mDownvotes.add(mCurrentJokeID);
                mUpvotes.remove(mCurrentJokeID);
                mCurrentJoke.decrementNumUpvotes();
                mCurrentJoke.incrementNumDownvotes();
            }
        } else {
            if (mVotingStatus == NO_VOTE) {
                mDownvoteButton.setImageResource(R.drawable.neutraldown);
                mDownvotes.remove(mCurrentJokeID);
                mCurrentJoke.decrementNumDownvotes();
            } else if (mVotingStatus == UPVOTED) {
                mUpvoteButton.setImageResource(R.drawable.upvote);
                mDownvoteButton.setImageResource(R.drawable.neutraldown);
                mDownvotes.remove(mCurrentJokeID);
                mUpvotes.add(mCurrentJokeID);
                mCurrentJoke.decrementNumDownvotes();
                mCurrentJoke.incrementNumUpvotes();
            } else { //Downvoted.
                mDownvoteButton.setImageResource(R.drawable.downvote);
                mDownvotes.add(mCurrentJokeID);
                mCurrentJoke.incrementNumDownvotes();
            }
        }
    }

    private class EditFavorite extends AsyncTask<String, Void, String> {

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
                    response = "Unable to update favorites, Reason: "
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
                    /**Toast.makeText(getActivity().getApplicationContext(), "Successfully updated: " + mCurrentJoke.getJokeTitle()
                            , Toast.LENGTH_LONG)
                            .show();**/

                    if (isFavorite)
                        isFavorite = false;
                    else
                        isFavorite = true;

                    ((JokesPage) getActivity()).refreshPage();
                } else {
                    if (isFavorite) {
                        mFavoriteButton.setMaxWidth(64);
                        mFavoriteButton.setImageResource(R.drawable.unfavoritebutton);
                        mFavorites.put(mCurrentJokeID, mCurrentJoke);
                    } else {
                        mFavoriteButton.setMaxWidth(56);
                        mFavoriteButton.setImageResource(R.drawable.favoritebutton);
                        mFavorites.remove(mCurrentJokeID);
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update: " + mCurrentJoke.getJokeTitle()
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Please check your internet connection."
                        , Toast.LENGTH_LONG).show();
            }
            mCurrentlyBusy = false;
        }
    }
}
