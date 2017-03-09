package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * This Fragment exists to enable the user to submit a Joke of their own.
 *
 * @author Vlad 3/5/2017
 * @author Kyle Phan 3/6/2017
 */
public class SubmitJokeFragment extends Fragment {
    /** The URL of the php file that handles Joke submission. */
    private static final String JOKE_SUB_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/submitJoke.php?";

    /** The EditText object that contains the Joke's title. */
    private EditText mJokeTitleEdit;

    /** The EditText object that contains the Joke's setup. */
    private EditText mJokeSetupEdit;

    /** The EditText object that contains the Joke's punchline. */
    private EditText mJokePunchlineEdit;

    /** The current user's username. */
    private String mUsername;

    public SubmitJokeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SubmitJokeFragment.
     */
    public static SubmitJokeFragment newInstance(String param1, String param2) {
        return new SubmitJokeFragment();
    }

    /**
     * This onCreate method simply retrieves the username value from the caller's Bundle file.
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString("username");
        }
    }

    /**
     * This method simply initializes our EditText fields, and adds an onClickListener to the submit
     * and clear buttons that exist on this Fragment.
     * @param inflater A LayoutInflater object, that is used to get a View.
     * @param container A ViewGroup object that is also used to get a View.
     * @param savedInstanceState Stores data that was sent from the caller.
     * @return Returns the View object that was generated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_submit_joke, container, false);

        mJokeTitleEdit = (EditText) v.findViewById(R.id.sub_joke_title_field);
        mJokeSetupEdit = (EditText) v.findViewById(R.id.sub_joke_setup_field);
        mJokePunchlineEdit = (EditText) v.findViewById(R.id.sub_joke_punchline_field);

        Button addCourseButton = (Button) v.findViewById(R.id.sub_joke_button);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidInput(v)) {
                    submitJoke(v);
                }

            }
        });

        Button clearButton = (Button) v.findViewById(R.id.clear_fields_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJokeTitleEdit.setText("");
                mJokeSetupEdit.setText("");
                mJokePunchlineEdit.setText("");
            }
        });

        return v;
    }

    /**
     * Verifies that the user's inputted text is valid.
     * @param v A View object where we can display Toast messages.
     * @return Returns a boolean, true if the user has inputted valid text in all the EditText boxes,
     * and false if they haven't inputted valid text for all the EditText boxes.
     */
    private boolean checkValidInput(View v) {
        if (mJokeTitleEdit.getText().length() < 5) {
            Toast.makeText(v.getContext(), "Please enter a valid joke title.", Toast.LENGTH_LONG)
                    .show();
            return false;
        } else if (mJokeSetupEdit.getText().length() <= 0) {
            Toast.makeText(v.getContext(), "Please enter a valid joke setup.", Toast.LENGTH_LONG)
                    .show();
            return false;
        } else if (mJokePunchlineEdit.getText().length() <= 0) {
            Toast.makeText(v.getContext(), "Please enter a valid joke punchline.", Toast.LENGTH_LONG)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Auto-generated method, not modified by us.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Executes a SubmitJokeTask, which will handle submitting the user's Joke to our web server.
     * @param v A View object where we can display Toast messages.
     */
    public void submitJoke(View v) {
        new SubmitJokeTask().execute(buildJokeURL(v));
    }

    /**
     * Builds a URL String that contains all of the user's Joke info, which we will submit to the
     * server via a GET request. The user's username will also be included in the request.
     * @param v A View object where we can display Toast messages.
     * @return Returns the URL String that was built in this method.
     */
    private String buildJokeURL(View v) {

        StringBuilder sb = new StringBuilder(JOKE_SUB_URL);

        try {
            String jokeTitle = mJokeTitleEdit.getText().toString();
            sb.append("jokeTitle=");
            sb.append(URLEncoder.encode(jokeTitle, "UTF-8"));


            String jokeSetup = mJokeSetupEdit.getText().toString();
            sb.append("&jokeSetup=");
            sb.append(URLEncoder.encode(jokeSetup, "UTF-8"));


            String jokePunchline = mJokePunchlineEdit.getText().toString();
            sb.append("&jokePunchline=");
            sb.append(URLEncoder.encode(jokePunchline, "UTF-8"));

            sb.append("&user=");
            sb.append(URLEncoder.encode(mUsername, "UTF-8"));

            Log.i("SubmitJokeURL", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * This class is used to submit the user's inputted text to our php file, which will then email
     * that text to our private email account, where we can easily review them.
     */
    private class SubmitJokeTask extends AsyncTask<String, Void, String> {

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
         * Notifies the user of what happened via Toast, and clears the EditText boxes if no errors
         * occurred.
         *
         * @param result An error message, or the server's response/webpage's data.
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    mJokeTitleEdit.setText("");
                    mJokeSetupEdit.setText("");
                    mJokePunchlineEdit.setText("");

                    Toast.makeText(getActivity().getApplicationContext(), "Joke successfully submitted for review!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to add: "
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
