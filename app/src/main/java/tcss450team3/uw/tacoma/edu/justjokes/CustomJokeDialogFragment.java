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
 * @author Vlad 3/5/2017
 */
public class CustomJokeDialogFragment extends DialogFragment {
    /** String identifier to retrieve the Joke from the Bundle object */
    public final static String JOKE_SELECTED = "joke_selected";

    /** URL that is concatenated with other strings to access different php files on our server. */
    private static final String BASE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/handle";

    /** An integer that signifies that the user has not voted on the current joke yet. */
    public final static int NO_VOTE = 0;

    /** An integer that signifies that the user has upvoted the current joke. */
    public final static int UPVOTED = 1;

    /** An integer that signifies that the user has downvoted the current joke. */
    public final static int DOWNVOTED = 2;

    /** A TextView variable that allows us to modify the setup TextView seen by the user. */
    private TextView mJokeSetupTextView;

    /** A TextView variable that allows us to modify the punchline TextView seen by the user. */
    private TextView mJokePunchlineTextView;

    /** The Button that hides/shows a joke's punchline. */
    private Button mShowButton;

    /** A Map of the user's favorite jokes, jokeIds are mapped to the Joke objects, this was done
     * to ensure quick searches. */
    private Map<Integer, Joke> mFavorites;

    /** A Set of JokeIds, of the Jokes that the user has upvoted. */
    private Set<Integer> mUpvotes;

    /** A Set of JokeIds, of the Jokes that the user has downvoted. */
    private Set<Integer> mDownvotes;

    /** The current user's username. */
    private String mUsername;

    /** The Favorite button that appears on the UI. */
    private ImageView mFavoriteButton;

    /** The Upvote button that appears on the UI. */
    private ImageView mUpvoteButton;

    /** The Downvote button that appears on the UI. */
    private ImageView mDownvoteButton;

    /** The onscreen TextView that displays how many upvotes a Joke currently has. */
    private TextView mUpvoteTextView;

    /** The onscreen TextView that displays how many downvotes a Joke currently has. */
    private TextView mDownvoteTextView;

    /** A boolean value that keeps user's from abusing our buttons. */
    private boolean mCurrentlyBusy;

    /** A String that we use to differentiate the user's up/downvote button clicks. */
    private String mAction;

    /** The Joke object that we are currently working with. */
    private Joke mCurrentJoke;

    /** The Joke object that we're currently working on's id number. */
    private int mCurrentJokeID;

    /** An integer that represents whether the Joke has been up/downvoted, or not voted on yet. */
    private int mVotingStatus;

    /** A boolean to let us know if the current Joke object is on of the user's favorites. */
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
            mCurrentJoke = (Joke) args.getSerializable(JOKE_SELECTED);
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
                    mShowButton.setText(R.string.hidePunchline);
                } else {
                    mJokePunchlineTextView.setVisibility(View.INVISIBLE);
                    mShowButton.setText(R.string.showPunchline);
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

                if (mVotingStatus == NO_VOTE) { //The Jokes has not been voted on yet.
                    //Upvote the joke.
                    mUpvoteButton.setImageResource(R.drawable.upvote);
                    mUpvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumUpvotes();
                } else if (mVotingStatus == UPVOTED) { //The Joke is already upvoted.
                    //Set the user's vote to neutral/unvoted.
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mUpvotes.remove(mCurrentJokeID);
                    mCurrentJoke.decrementNumUpvotes();
                } else { //The Joke is currently downvoted.
                    //Remove the downvote, upvote the Joke.
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

                if (mVotingStatus == NO_VOTE) { //The Jokes has not been voted on yet.
                    //Downvote the joke.
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mDownvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumDownvotes();
                } else if (mVotingStatus == UPVOTED) { //The Joke is upvoted.
                    //Remove the upvote, downvote the Joke.
                    mUpvoteButton.setImageResource(R.drawable.neutralup);
                    mDownvoteButton.setImageResource(R.drawable.downvote);
                    mUpvotes.remove(mCurrentJokeID);
                    mDownvotes.add(mCurrentJokeID);
                    mCurrentJoke.incrementNumDownvotes();
                    mCurrentJoke.decrementNumUpvotes();
                } else {  //The Joke is already downvoted.
                    //Set the user's vote to neutral/unvoted.
                    mDownvoteButton.setImageResource(R.drawable.neutraldown);
                    mDownvotes.remove(mCurrentJokeID);
                    mCurrentJoke.decrementNumDownvotes();
                }

                updateCountTextViews();
                new EditVote().execute(buildURL());
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mCurrentlyBusy)
                    return;
                else
                    mCurrentlyBusy = true;

                if (isFavorite) { //If already favorited, unfavorite the Joke.
                    mFavoriteButton.setMaxWidth(56);
                    mFavoriteButton.setImageResource(R.drawable.favoritebutton);
                    mFavorites.remove(mCurrentJokeID);
                } else { //Not favorited yet, so favorite the Joke.
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
     * This method updates the proper TextViews to display the joke's setup and punchline.
     */
    public void updateView() {
        if (mCurrentJoke != null) {
            mJokeSetupTextView.setText(mCurrentJoke.getJokeSetup());
            mJokePunchlineTextView.setText(mCurrentJoke.getJokePunchline());
        }
    }

    /**
     * This method updates the up/downvote TextViews to display the joke's current vote amounts.
     */
    private void updateCountTextViews() {
        mUpvoteTextView.setText(Integer.toString(mCurrentJoke.getmNumUpvotes()));
        mDownvoteTextView.setText(Integer.toString(mCurrentJoke.getmNumDownvotes()));
    }

    /**
     * Builds the URL to modify the favorites field in our database for the current joke.
     * @return Returns the URL, with all of the GET fields included.
     */
    private String buildFavoritesURL() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("Favorites.php?user=");

        sb.append(mUsername);
        sb.append("&jokeID=");
        sb.append(mCurrentJokeID);
        sb.append("&favorite=");
        if (isFavorite)
            sb.append(0); //We want to unfavorite this joke.
        else
            sb.append(1); //We want to favorite this joke.

        return sb.toString();
    }

    /**
     * Builds the URL to modify the up/downvote field in our database for the current joke.
     * @return
     */
    private String buildURL() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append(mAction); //Determines if we access the handleUpvotes or handleDownvotes php file.

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

    /**
     * Checks if the current joke has been up/downvoted or favorited, and adjusts the button images
     * to reflect this initial state.
     */
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

    /**
     * Class that handles modifying the up/downvote fields in our database.
     */
    private class EditVote extends AsyncTask<String, Void, String> {

        /**
         * This methods handles accessing our php file.
         * @param urls The URL of our php file.
         * @return Returns the web server's response.
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
         * This method takes the web server's response, and figures out what to do if there was an
         * error of some kind, or if the response was a successful one.
         *
         * @param result The web server's response.
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

    /**
     * Resets all the up/downvote images and the Sets that hold JokeIds. This method gets called
     * when there was an issue updating our online database with the user's vote selection.
     */
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
        updateCountTextViews();
    }

    /**
     * Resets the favorite buttons image and the Map that managers the user's favorites.
     * This method gets called when there was an issue updating our online database with the user's
     * favorite/unfavorite selection.
     */
    private void resetFavorite() {
        if (isFavorite) {
            mFavoriteButton.setMaxWidth(64);
            mFavoriteButton.setImageResource(R.drawable.unfavoritebutton);
            mFavorites.put(mCurrentJokeID, mCurrentJoke);
        } else {
            mFavoriteButton.setMaxWidth(56);
            mFavoriteButton.setImageResource(R.drawable.favoritebutton);
            mFavorites.remove(mCurrentJokeID);
        }
    }

    /**
     * This class handles accessing the web server to update the user's favorite.
     */
    private class EditFavorite extends AsyncTask<String, Void, String> {
        /**
         * This methods handles accessing our php file.
         * @param urls The URL of our php file.
         * @return Returns the web server's response.
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
         * This method takes the web server's response, and figures out what to do if there was an
         * error of some kind, or if the response was a successful one.
         *
         * @param result The web server's response.
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    if (isFavorite)
                        isFavorite = false;
                    else
                        isFavorite = true;

                    ((JokesPage) getActivity()).refreshPage(); //Refreshes the list fragment to
                                                               //reflect the changes made by the
                                                               //user.
                } else {
                    resetFavorite();
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update: " + mCurrentJoke.getJokeTitle()
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                resetFavorite();

                Toast.makeText(getActivity().getApplicationContext(), "Please check your internet connection."
                        , Toast.LENGTH_LONG).show();
            }
            mCurrentlyBusy = false;
        }
    }
}
